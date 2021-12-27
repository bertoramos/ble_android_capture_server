package com.ips.blecapturer.view.fragments

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.ips.blecapturer.ConnectionHandler
import com.ips.blecapturer.R


class ServerFragment : Fragment() {

    private val DEFAULT_CLIENT_PORT = 5558
    private val DEFAULT_SERVER_PORT = 4445

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflateView = inflater.inflate(R.layout.fragment_server, container, false)

        inflateView.findViewById<ToggleButton>(R.id.toggleServerButton).setOnCheckedChangeListener { _, state ->
            if(state) onServer() else offServer()
        }

        return inflateView
    }

    private fun onServer() {
        val clientPort = DEFAULT_CLIENT_PORT
        val serverPort = DEFAULT_SERVER_PORT

        ConnectionHandler.connect(clientPort, serverPort)

        val context: Context = requireContext().applicationContext
        val wm: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ip = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        Log.d("BLECapturer" , "My ip: $ip")
        view?.findViewById<TextView>(R.id.serverNameTextView)?.text = "Server\nIP: $ip"
    }

    private fun offServer() {
        ConnectionHandler.disconnect()
        view?.findViewById<TextView>(R.id.serverNameTextView)?.text = "Server"
    }

}