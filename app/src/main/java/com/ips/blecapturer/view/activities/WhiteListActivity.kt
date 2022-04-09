package com.ips.blecapturer.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import br.com.sapereaude.maskedEditText.MaskedEditText
import com.ips.blecapturer.BLEScanner
import com.ips.blecapturer.R
import com.ips.blecapturer.model.Beacon
import com.ips.blecapturer.view.WhiteListAdapter
import java.io.InputStream
import java.io.OutputStream


class WhiteListActivity : AppCompatActivity() {

    private lateinit var whiteListAdapter: WhiteListAdapter

    companion object {
        var REQUEST_CREATE_FILE = 1
        var REQUEST_OPEN_FILE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_white_list)

        whiteList()

        checkWhiteListEmpty()

        findViewById<Button>(R.id.addFilterButton).setOnClickListener {
            addFilterAlertDialog()

            checkWhiteListEmpty()
        }

        whiteListAdapter.registerAdapterDataObserver(object: AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkWhiteListEmpty()
            }
        })

        clearWhiteListButton()
        saveWhitelistButton()
        loadWhitelistButton()
    }

    private fun checkWhiteListEmpty() {
        val allowedLabel = findViewById<TextView>(R.id.all_allowed_label)
        allowedLabel.visibility = if (BLEScanner.getWhiteListSize() != 0) View.GONE else View.VISIBLE
    }


    private fun addFilterAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        val customLayout = layoutInflater.inflate(R.layout.white_list_input_item, null)
        alertDialog.setView(customLayout)

        val spinner = customLayout.findViewById<Spinner>(R.id.protocolSpinner)
        val adapter = ArrayAdapter(customLayout.context, android.R.layout.simple_spinner_dropdown_item, Beacon.Protocol.values())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        alertDialog.setPositiveButton(
            "OK"
        ) { _, _ ->
            val macEditText = customLayout.findViewById<MaskedEditText>(R.id.macInputFilter)
            val macValue = macEditText.text.toString().uppercase()
            val protocolValue = spinner.selectedItem as Beacon.Protocol
            Toast.makeText(applicationContext, "$macValue $protocolValue", Toast.LENGTH_LONG).show()

            BLEScanner.allowBeacon(macValue, protocolValue)
            whiteListAdapter.notifyDataSetChanged()

        }
        alertDialog.setNegativeButton("Cancel", null)

        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun whiteList() {
        val whiteList: RecyclerView = findViewById(R.id.whiteListRecyclerView)
        val adapter = WhiteListAdapter()
        whiteList.adapter = adapter

        whiteList.layoutManager = LinearLayoutManager(applicationContext)

        whiteListAdapter = adapter
    }

    private fun clearWhiteListButton() {
        findViewById<Button>(R.id.clearWhiteList).setOnClickListener {
            BLEScanner.allowAllBeacons()
            whiteListAdapter.notifyDataSetChanged()
        }
    }

    private fun saveWhitelistButton() {
        findViewById<Button>(R.id.saveWhiteList).setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("*text/plain")
            intent.putExtra(Intent.EXTRA_TITLE, "file.conf")
            startActivityForResult(intent, REQUEST_CREATE_FILE)
        }
    }

    private fun loadWhitelistButton() {
        findViewById<Button>(R.id.loadWhiteList).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"

            startActivityForResult(intent, REQUEST_OPEN_FILE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CREATE_FILE) {
            Log.d("REQUEST", "${data?.data}")
            var os : OutputStream? = null
            if (data != null) {
                os = contentResolver.openOutputStream(data.data!!)

                val whitelist = BLEScanner.getBeaconList()

                var content = ""
                whitelist.forEach { el ->
                    val mac: String = el.first
                    val protocol: Beacon.Protocol = el.second
                    content += "$mac $protocol\n"
                }
                val bt = content.toByteArray()
                os?.write(bt)
            }
            os?.close()
        }

        val errorRead = Toast.makeText(applicationContext, "Not valid format", Toast.LENGTH_LONG)

        if(requestCode == REQUEST_OPEN_FILE) {
            val tmpWhiteList = arrayListOf<Pair<String, Beacon.Protocol>>()

            Log.d("REQUEST", "${data?.data}")
            var iss : InputStream? = null
            if(data != null) {
                iss = contentResolver.openInputStream(data.data!!)
                iss?.bufferedReader()?.forEachLine { line ->
                    Log.d("REQUEST_READ", line.split(" ").toString())
                    val lineArr = line.split(" ")
                    Log.d("REQUEST", "${lineArr.size}")
                    if(lineArr.size != 2) {
                        errorRead.show()
                        return@forEachLine
                    }

                    val mac = lineArr[0]
                    val protocol = Beacon.Protocol.fromString(lineArr[1])

                    if(!mac.matches(Regex("([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}")) || protocol == null) {
                        errorRead.show()
                        return@forEachLine
                    }

                    tmpWhiteList.add(Pair(mac, protocol))
                }
            }

            BLEScanner.allowAllBeacons()
            tmpWhiteList.forEach {
                val mac = it.first
                val protocol = it.second
                BLEScanner.allowBeacon(mac, protocol)
            }
            whiteListAdapter.notifyDataSetChanged()
        }
    }
}