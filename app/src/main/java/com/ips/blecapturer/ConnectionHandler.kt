package com.ips.blecapturer


import android.util.Log
import com.ips.blecapturer.packets.*
import kotlin.math.abs

object ConnectionHandler {

    var server: UDPServer? = null

    fun connect(clientPort: Int, serverPort: Int) {
        server = UDPServer(clientPort, serverPort)
        if(server != null) {
            server!!.start()
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

}