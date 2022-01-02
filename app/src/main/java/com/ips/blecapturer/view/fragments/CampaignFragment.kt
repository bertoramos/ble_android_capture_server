package com.ips.blecapturer.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ips.blecapturer.R
import com.ips.blecapturer.model.database.DatabaseHandler
import com.ips.blecapturer.model.database.DatabaseViewModel


class CampaignFragment : Fragment() {

    private val database_view_model: DatabaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DatabaseHandler.databaseViewModel = database_view_model
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_campaign, container, false)

        initView(inflatedView)


        return inflatedView
    }

    private fun initView(view: View)
    {

        val toggleCampaignButton = view.findViewById<ToggleButton>(R.id.toggleCreateCloseCampaignButton)
        toggleCampaignButton.setOnClickListener {
            if(toggleCampaignButton.isChecked) {
                val b: AlertDialog.Builder = AlertDialog.Builder(view.context)
                b.setTitle("Enter a database name")
                val input = EditText(view.context)
                b.setView(input)
                b.setPositiveButton("OK") { _, _ ->
                    val dbname = input.text.toString()
                    if(dbname.isNullOrBlank()) {
                        toggleCampaignButton.isChecked = false
                        Toast.makeText(view.context, "Empty campaign name", Toast.LENGTH_LONG).show()
                    } else {
                        DatabaseHandler.databaseViewModel?.createDatabase(view.context, dbname)
                        Toast.makeText(view.context, "Created $dbname", Toast.LENGTH_LONG).show()
                    }
                }
                b.setNegativeButton("CANCEL") { _, _ ->
                    toggleCampaignButton.isChecked = false
                    Toast.makeText(view.context, "Campaign not created", Toast.LENGTH_LONG).show()
                }
                b.show()
            } else {
                DatabaseHandler.databaseViewModel?.closeDatabase()
                Toast.makeText(view.context, "Campaign was closed", Toast.LENGTH_LONG).show()
            }
        }

        DatabaseHandler.databaseViewModel?.databaseName?.observe(this, { db_name ->
            view.findViewById<TextView>(R.id.currentDatabaseName).text = if(db_name.isEmpty()) resources.getString(R.string.db_not_created) else db_name
            view.findViewById<TextView>(R.id.databaseNameLabel).text = if(db_name.isEmpty()) "" else resources.getString(R.string.db_name)
        })

    }

    /*
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
            val max = +360
            val min = -360
            val x = min + Math.random().toFloat() * (max - min)
            val y = min + Math.random().toFloat() * (max - min)
            val z = min + Math.random().toFloat() * (max - min)
            val yaw = min + Math.random().toFloat() * (max - min)
            val pos = Pose(x, y, z, yaw)
            val timestamp = Date().time
            database_view_model.insertCapture(timestamp, pos)
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
            val timestamp = Date().time
            database_view_model.insertFrame(timestamp, mac, protocol, rssi)
            Toast.makeText(applicationContext, "Frame inserted", Toast.LENGTH_LONG).show()

        }

    }
    */
}