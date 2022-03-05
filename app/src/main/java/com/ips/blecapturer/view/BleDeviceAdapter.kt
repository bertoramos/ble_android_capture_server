package com.ips.blecapturer.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ips.blecapturer.R
import com.ips.blecapturer.model.Beacon

class BleDeviceAdapter : RecyclerView.Adapter<BleDeviceAdapter.BleDeviceHolder>() {

    var devices = ArrayList<Beacon>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ble_device_item, parent, false)
        return BleDeviceHolder(view)
    }

    override fun onBindViewHolder(holder: BleDeviceHolder, position: Int) {
        val beacon = devices[position]
        holder.bind(beacon)
    }

    override fun getItemCount(): Int = devices.size

    class BleDeviceHolder(view: View): RecyclerView.ViewHolder(view) {

        val mac = view.findViewById<TextView>(R.id.macTextView)
        val protocol = view.findViewById<TextView>(R.id.protocolTextView)
        val rssi = view.findViewById<TextView>(R.id.rssiTextView)

        fun bind(beacon: Beacon) {
            mac.text = beacon.mac
            protocol.text = beacon.protocol.toString()
            rssi.text = beacon.rssi.toString()
        }
    }
}
