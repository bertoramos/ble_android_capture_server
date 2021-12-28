package com.ips.blecapturer.view

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ips.blecapturer.BLEScanner
import com.ips.blecapturer.R
import com.ips.blecapturer.model.Beacon

class WhiteListAdapter : RecyclerView.Adapter<WhiteListAdapter.WhiteListHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WhiteListAdapter.WhiteListHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.white_list_item, parent, false)
        return WhiteListAdapter.WhiteListHolder(view)
    }

    override fun onBindViewHolder(holder: WhiteListAdapter.WhiteListHolder, position: Int) {
        val pair = BLEScanner.getFromWhiteList(position)
        holder.bind(pair.first, pair.second)
    }

    override fun getItemCount(): Int = BLEScanner.getWhiteListSize()

    class WhiteListHolder(view: View) : RecyclerView.ViewHolder(view) {

        val mac = view.findViewById<TextView>(R.id.macValue)
        val protocol = view.findViewById<TextView>(R.id.protocolValue)

        fun bind(mac_value: String, protocol_value: Beacon.Protocol) {
            mac.text = mac_value
            protocol.text = protocol_value.toString()
        }

        init {
            view.setOnClickListener {
                //Log.d("white_list", "${mac.text} ${protocol.text} ${layoutPosition}")
                //val pair = BLEScanner.getFromWhiteList(layoutPosition)
                //BLEScanner.denyBeacon(pair.first, pair.second)
                //bindingAdapter?.notifyDataSetChanged()

                AlertDialog.Builder(it.context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Sure?")
                    .setMessage("Are you sure to deny ${mac.text} ${protocol.text}")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes") { _, _ ->
                        Log.d("white_list", "${mac.text} ${protocol.text} ${layoutPosition}")
                        val pair = BLEScanner.getFromWhiteList(layoutPosition)
                        BLEScanner.denyBeacon(pair.first, pair.second)
                        bindingAdapter?.notifyDataSetChanged()
                    }
                    .show()

            }

        }

    }

}

