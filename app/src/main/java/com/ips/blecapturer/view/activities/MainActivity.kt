package com.ips.blecapturer.view.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.ips.blecapturer.R
import com.ips.blecapturer.model.BLESharedViewModel
import com.ips.blecapturer.model.Beacon
import com.ips.blecapturer.model.database.DatabaseViewModel
import com.ips.blecapturer.model.database.tables.Frame
import com.ips.blecapturer.model.database.tables.Position
import kotlin.concurrent.thread
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val database_view_model: DatabaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filterButton: Button = findViewById(R.id.whiteListButton)
        filterButton.setOnClickListener {
            val intent = Intent(this, WhiteListActivity::class.java).apply {

            }
            startActivity(intent)
        }

        databaseMockUp()
    }

    private fun databaseMockUp()
    {
        findViewById<Button>(R.id.createDatabaseMockUp).setOnClickListener {

            val b: AlertDialog.Builder = AlertDialog.Builder(this)
            b.setTitle("Enter a database name")
            val input = EditText(this)
            b.setView(input)
            b.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                val dbname = input.text.toString()
                database_view_model.createDatabase(applicationContext, dbname)
                Toast.makeText(applicationContext, "Created $dbname", Toast.LENGTH_LONG).show()
            })
            b.setNegativeButton("CANCEL", null)
            b.show()
        }

        findViewById<Button>(R.id.insertCaptureMockup).setOnClickListener {
            val max = +30
            val min = -30
            val x = min + Math.random().toFloat() * (max - min)
            val y = min + Math.random().toFloat() * (max - min)
            val z = min + Math.random().toFloat() * (max - min)
            val pos = Position(x, y, z)
            database_view_model.insertCapture(pos)
            Toast.makeText(applicationContext, "Capture inserted", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.insertFrameMockup).setOnClickListener {

            fun randomID(): String = List(2) {
                (('A'..'F') + ('0'..'9')).random()
            }.joinToString("")

            val mac =
                "${randomID()}:${randomID()}:${randomID()}:${randomID()}:${randomID()}:${randomID()}"

            val protocolIdx = Random.nextInt(Beacon.Protocol.values().size)
            val protocol = Beacon.Protocol.values()[protocolIdx]

            val rssi = Random.nextInt(-100, 0)
            database_view_model.insertFrame(mac, protocol, rssi)
            Toast.makeText(applicationContext, "Frame inserted", Toast.LENGTH_LONG).show()

        }

    }

}