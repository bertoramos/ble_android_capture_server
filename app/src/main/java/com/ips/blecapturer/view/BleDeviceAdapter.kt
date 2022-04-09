package com.ips.blecapturer.view

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.ips.blecapturer.BLEScanner
import com.ips.blecapturer.R
import com.ips.blecapturer.model.Beacon
import kotlin.coroutines.coroutineContext

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

        var beacon: Beacon? = null
        val mac = view.findViewById<TextView>(R.id.macTextView)
        val protocol = view.findViewById<TextView>(R.id.protocolTextView)
        val rssi = view.findViewById<TextView>(R.id.rssiTextView)
        val isWhiteListTag = view.findViewById<ImageView>(R.id.isWhiteListTag)

        fun bind(beacon: Beacon) {
            this.beacon = beacon
            mac.text = beacon.mac
            protocol.text = beacon.protocol.toString()
            rssi.text = beacon.rssi.toString()

            isWhiteListTag.visibility = if(BLEScanner.whiteListContains(beacon.mac, beacon.protocol)) View.VISIBLE else View.GONE
        }

        init {
            view.setOnClickListener {
                if(beacon != null) {
                    if(BLEScanner.whiteListContains(beacon!!.mac, beacon!!.protocol)) {
                        AlertDialog.Builder(it.context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Sure?")
                            .setMessage("Do you want to deny ${mac.text} ${protocol.text}?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes") { _, _ ->
                                if(beacon != null) {
                                    BLEScanner.denyBeacon(beacon!!.mac, beacon!!.protocol)
                                    bindingAdapter?.notifyDataSetChanged()
                                }
                            }
                            .show()
                    } else {
                        AlertDialog.Builder(it.context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Sure?")
                            .setMessage("Do you want to whitelist ${mac.text} ${protocol.text}?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes") { _, _ ->
                                if(beacon != null) {
                                    BLEScanner.allowBeacon(beacon!!.mac, beacon!!.protocol)
                                    bindingAdapter?.notifyDataSetChanged()

                                }
                            }
                            .show()
                    }
                }


            }
        }

    }
}
