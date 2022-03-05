package com.ips.blecapturer.view.activities

import android.os.Bundle
import android.transition.Visibility
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
import java.util.*


class WhiteListActivity : AppCompatActivity() {

    private lateinit var whiteListAdapter: WhiteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_white_list)

        whiteList()

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
}