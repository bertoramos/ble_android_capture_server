package com.ips.blecapturer


import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ips.blecapturer.model.BLESharedViewModel
import com.ips.blecapturer.packets.*
import kotlin.math.abs

object ConnectionHandler {

    var server: UDPServer? = null

    fun connect(clientPort: Int, serverPort: Int, context: Context?) {
        server = UDPServer(clientPort, serverPort)
        server!!.setContext(context)
        if(server != null) {
            server!!.start()
        }
    }

    fun setBLESharedViewModel(viewModel: BLESharedViewModel) {
        if(server != null) {
            server!!.setBLESharedViewModel(viewModel)
        }
    }

    fun setView(view: View) {
        if(server != null) {
            server!!.setView(view)
        }
    }

    fun disconnect() {

        if(server != null) {
            server!!.setRunning(false)
            server!!.closeSocket()
        }
    }

    fun sendAck(pid: Long, ackPid: Long, status: Int) {
        if(server != null) {
            val packet = AckPacket(pid, ackPid, status)
            Log.d("UDPSERVER", "Enviado ack ${packet}")
            val bytes = UDPPacker.pack(packet)

            if(bytes != null) server!!.sendPacket(bytes)
        }
    }

    fun wasAck(ackPid: Long): Boolean {
        if(server != null) {
            val size = server!!.buffer.size
            for(i in 0 until size) {
                val packet = server!!.buffer[i]
                if (packet.ptype == AckPacket.PTYPE) {
                    if((packet as AckPacket).ackPid == ackPid) return true
                }
            }
        }
        return false
    }

    fun sendEndCapture(pid: Long) : Boolean {
        if(server != null) {
            val packet = EndCapturePacket(pid)
            val bytes = UDPPacker.pack(packet)

            if(bytes != null) server!!.sendPacket(bytes)

            // Wait ACK
            var currentTime = System.currentTimeMillis()
            while(abs(System.currentTimeMillis() - currentTime) < 8000) {
                if (wasAck(packet.pid)) return true
            }
        }
        return false
    }

    fun sendServerClosePacket(): Boolean {

        if(server != null) {
            if(server!!.clientAddr != null) {
                server!!.last_pid_sent += 1
                val pid = server!!.last_pid_sent

                val packet = CloseServerPacket(pid)
                val bytes = UDPPacker.pack(packet)

                if(bytes != null) server!!.sendPacket(bytes)

                val currentTime = System.currentTimeMillis()
                while (abs(System.currentTimeMillis() - currentTime) < 8000) {
                    if (wasAck(packet.pid)) return true
                }

                //Log.d("SERVER", "CLIENT CONNECTED")
            }
        }

        return false
    }

}