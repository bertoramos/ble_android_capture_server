package com.ips.blecapturer.view.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.ips.blecapturer.BLEScanner
import com.ips.blecapturer.ConnectionHandler
import com.ips.blecapturer.R
import com.ips.blecapturer.UDPServer
import com.ips.blecapturer.model.BLESharedViewModel
import com.ips.blecapturer.model.database.DatabaseHandler
import com.ips.blecapturer.model.database.DatabaseViewModel
import com.ips.blecapturer.view.activities.WhiteListActivity
import kotlin.concurrent.thread


class ServerFragment : Fragment() {

    private val DEFAULT_CLIENT_PORT = 5558
    private val DEFAULT_SERVER_PORT = 4445

    private val bleViewModel : BLESharedViewModel by viewModels()

    private val database_view_model: DatabaseViewModel by viewModels()

    private var serverState: Boolean = false
    private var campaignCreated : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DatabaseHandler.databaseViewModel = database_view_model
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflateView = inflater.inflate(R.layout.fragment_server, container, false)

        //inflateView.findViewById<ToggleButton>(R.id.toggleServerButton).setOnCheckedChangeListener { _, state ->
        //    if(state) onServer() else offServer()
        //}

        val serverButton = inflateView.findViewById<MaterialButton>(R.id.serverButton)
        serverButton.setOnClickListener {
            if(!BLEScanner.isEnabled()) {
                val builder = android.app.AlertDialog.Builder(context)
                builder.setTitle("This app requires bluetooth")
                builder.setMessage("Please activate bluetooth")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {

                }
                builder.show()
            } else {
                serverState = if (serverState) {
                    offServer()
                    serverButton.icon = ContextCompat.getDrawable(
                        inflateView.context,
                        R.drawable.ic_power_off_24dp_foreground
                    )
                    serverButton.text = resources.getText(R.string.off_mini)
                    false
                } else {
                    onServer()
                    serverButton.icon = ContextCompat.getDrawable(
                        inflateView.context,
                        R.drawable.ic_power_24dp_foreground
                    )
                    serverButton.text = resources.getText(R.string.on_mini)
                    true
                }

            }
        }

        initView(inflateView)

        whiteListButton(inflateView)

        return inflateView
    }

    private fun whiteListButton(view: View) {
        val filterButton: Button = view.findViewById(R.id.whiteListButton)
        filterButton.setOnClickListener {
            val intent = Intent(context, WhiteListActivity::class.java).apply {

            }
            startActivity(intent)
        }
    }

    private fun onServer() {
        val clientPort = DEFAULT_CLIENT_PORT
        val serverPort = DEFAULT_SERVER_PORT

        ConnectionHandler.connect(clientPort, serverPort)
        ConnectionHandler.setBLESharedViewModel(bleViewModel)
        view?.let { ConnectionHandler.setView(it) }

        val context: Context = requireContext().applicationContext
        val wm: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ip = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        Log.d("BLECapturer" , "My ip: $ip")
        view?.findViewById<TextView>(R.id.serverIPTextView)?.text = "Server IP: $ip"


    }

    private fun offServer() {
        thread (start = true) {
            ConnectionHandler.sendServerClosePacket()
            ConnectionHandler.disconnect()
        }
        view?.findViewById<TextView>(R.id.serverIPTextView)?.text = ""
        view?.findViewById<TextView>(R.id.clientIPTextView)?.text = ""
    }

    private fun create_campaign(view: View, dbname: String) {
        val toggleCampaignButton = view.findViewById<MaterialButton>(R.id.createCampaignButton)

        DatabaseHandler.databaseViewModel?.createDatabase(view.context, dbname)

        campaignCreated = true

        toggleCampaignButton.icon = ContextCompat.getDrawable(view.context, R.drawable.ic_close_24dp_foreground)
        toggleCampaignButton.text = "Database $dbname.db"
    }

    private fun close_campaign(view: View) {

        val toggleCampaignButton = view.findViewById<MaterialButton>(R.id.createCampaignButton)

        DatabaseHandler.databaseViewModel?.closeDatabase()

        campaignCreated = false

        toggleCampaignButton.icon = ContextCompat.getDrawable(view.context, R.drawable.ic_add_24dp_foreground)
        toggleCampaignButton.text = resources.getText(R.string.create_campaign)

    }

    private fun cancel_create_campaign(view: View) {
        val toggleCampaignButton = view.findViewById<MaterialButton>(R.id.createCampaignButton)

        campaignCreated = false
        //view.findViewById<MaterialButton>(R.id.serverButton).isEnabled = false

        toggleCampaignButton.icon = ContextCompat.getDrawable(view.context, R.drawable.ic_add_24dp_foreground)
        toggleCampaignButton.text = resources.getText(R.string.create_campaign)
    }

    private fun initView(view: View)
    {

        val toggleCampaignButton = view.findViewById<MaterialButton>(R.id.createCampaignButton)
        toggleCampaignButton.setOnClickListener {

            if(!campaignCreated) {
                val b: AlertDialog.Builder = AlertDialog.Builder(view.context)
                b.setTitle("Enter a database name")
                val input = EditText(view.context)
                b.setView(input)
                b.setPositiveButton("OK") { _, _ ->
                    val dbname = input.text.toString()
                    if(dbname.isNullOrBlank()) {
                        cancel_create_campaign(view)
                        Toast.makeText(view.context, "Empty campaign name", Toast.LENGTH_LONG).show()
                    } else {
                        create_campaign(view, dbname)
                        Toast.makeText(view.context, "Created $dbname", Toast.LENGTH_LONG).show()
                    }
                }
                b.setNegativeButton("CANCEL") { _, _ ->
                    cancel_create_campaign(view)
                    Toast.makeText(view.context, "Campaign not created", Toast.LENGTH_LONG).show()
                }
                val dialog = b.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)

                dialog.show()
            } else {
                close_campaign(view)
                Toast.makeText(view.context, "Campaign was closed", Toast.LENGTH_LONG).show()
            }

        }

        //DatabaseHandler.databaseViewModel?.databaseName?.observe(this, { db_name ->
        //    view.findViewById<TextView>(R.id.currentDatabaseName).text = if(db_name.isEmpty()) resources.getString(R.string.db_not_created) else db_name
        //    view.findViewById<TextView>(R.id.databaseNameLabel).text = if(db_name.isEmpty()) "" else resources.getString(R.string.db_name)
        //})

    }

}