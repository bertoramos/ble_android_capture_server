package com.ips.blecapturer.view.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.sapereaude.maskedEditText.MaskedEditText
import com.ips.blecapturer.R
import com.ips.blecapturer.model.Beacon
import com.ips.blecapturer.view.WhiteListAdapter


class WhiteListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_white_list)

        whiteList()

        findViewById<Button>(R.id.addFilterButton).setOnClickListener {
            addFilterAlertDialog()
        }
    }

    private fun addFilterAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        val customLayout = layoutInflater.inflate(R.layout.white_list_input_item, null)
        alertDialog.setView(customLayout)

        val spinner = customLayout.findViewById<Spinner>(R.id.protocolSpinner)
        val adapter = ArrayAdapter(customLayout.context, android.R.layout.simple_spinner_item, Beacon.Protocol.values())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner.adapter = adapter

        alertDialog.setPositiveButton(
            "OK"
        ) { _, _ ->
            val macEditText = customLayout.findViewById<MaskedEditText>(R.id.macInputFilter)
            val macValue = macEditText.text
            val protocolSpinner = customLayout.findViewById<Spinner>(R.id.protocolSpinner)
            val protocolValue = protocolSpinner.selectedItem
            Toast.makeText(applicationContext, "$macValue $protocolValue", Toast.LENGTH_LONG).show()
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
    }
}