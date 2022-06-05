package com.ips.blecapturer

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.ips.blecapturer.model.BLESharedViewModel
import com.ips.blecapturer.model.database.DatabaseHandler
import com.ips.blecapturer.model.database.tables.Pose
import com.ips.blecapturer.packets.*
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext

class UDPServer(clientPort: Int, serverPort: Int): Thread() {

    companion object {
        val ack_packet_types = listOf(ModePacket.PTYPE, StartCapturePacket.PTYPE, EndCapturePacket.PTYPE)
    }
    var last_pid_recv = 0L
    var last_pid_sent = 0L

    var startMode = false
    var is_running: Boolean = false

    var clientAddr: InetAddress? = null
    var clientPort: Int? = clientPort // 5558

    var serverPort: Int = serverPort // 4445
    var socket: DatagramSocket? = null

    var appContext: Context? = null

    var buffer: ArrayList<Packet> = ArrayList()

    private lateinit var bleViewModel: BLESharedViewModel
    private lateinit var view: View


    fun setBLESharedViewModel(bleViewModel: BLESharedViewModel) {
        this.bleViewModel = bleViewModel
    }

    fun setView(v: View) {
        view = v
    }

    fun setContext(c: Context?) {
        appContext = c
    }

    fun closeSocket() {
        if(socket != null) {
            socket!!.close()
        }
    }

    fun setRunning(running: Boolean) {
        this.is_running = running
    }

    fun receivePacket(): DatagramPacket? {
        if(socket == null) return null;

        var buf = ByteArray(256)
        var packet = DatagramPacket(buf, buf.size)
        socket!!.receive(packet)

        return packet
    }

    fun sendPacket(packet: ByteArray) {
        if(socket == null) return
        if(clientAddr == null || clientPort == null) return
        val packet = DatagramPacket(packet, packet.size, clientAddr!!, clientPort!!)
        socket!!.send(packet)
    }

    private fun sendAckPacket(packet: Packet, status: Int) {
        last_pid_sent += 1
        val pid = last_pid_sent
        val ackPID = packet.pid
        // this.sendPacket(ackPacket)
        ConnectionHandler.sendAck(pid, ackPID, status)
    }
    
    private fun checkStop(packet: ModePacket) {
        if(startMode) {
            if(packet.mode == ModePacket.DISCONNECT) {
                Log.d("UDPSERVER", "ALL CLEARED")
                clientAddr = null
                startMode = false

                last_pid_recv = 0L
                last_pid_sent = 0L

                (appContext as Activity).runOnUiThread {
                    view?.findViewById<TextView>(R.id.clientIPTextView)?.text = ""
                }

            }
        }
    }

    private fun checkStart(packet: ModePacket, address: InetAddress) {
        if(!startMode) {
            if(packet.mode == ModePacket.CONNECT) {
                clientAddr = address
                startMode = true

                (appContext as Activity).runOnUiThread {
                    view?.findViewById<TextView>(R.id.clientIPTextView)?.text = "Client: ${clientAddr?.hostAddress}"
                }

            }
        }
    }

    override fun run()
    {
        this.is_running = true

        try {

            socket = DatagramSocket(serverPort)
            Log.d("BLECapturer", "socket open")

            while(this.is_running)
            {
                var datagram: DatagramPacket? = this.receivePacket()

                if(datagram != null) {
                    var buf = datagram?.data
                    var addr = datagram.address

                    if(buf != null) {
                        val packet = UDPUnpacker.unpack(buf)

                        if(packet != null && packet.pid > last_pid_recv) {
                            Log.d("UDPSERVER", "Se recibe un paquete no nulo ${packet.pid} ${last_pid_recv}")

                            Log.d("UDPSERVER", "Se recibe un paquete con pid correcto")

                            if(packet.ptype == ModePacket.PTYPE) {
                                Log.d("UDPSERVER", "Es un paquete de modo")
                                checkStart(packet as ModePacket, addr)
                                if (packet.ptype in ack_packet_types) sendAckPacket(
                                    packet,
                                    AckPacket.STATUS_OK
                                )
                                checkStop(packet)
                            } else if(packet.ptype == PosePacket.PTYPE) {
                                Log.d("CURRENT_POS", packet.toString())
                                BLEScanner.timestamp = (packet as PosePacket).timestamp
                                BLEScanner.xco = (packet as PosePacket).x
                                BLEScanner.yco = (packet as PosePacket).y
                                BLEScanner.zco = (packet as PosePacket).z
                                BLEScanner.yaw = (packet as PosePacket).yaw

                                /*val timestamp = (packet as PosePacket).timestamp
                                val x = (packet as PosePacket).x
                                val y = (packet as PosePacket).y
                                val z = (packet as PosePacket).z
                                val yaw = (packet as PosePacket).yaw
                                bleViewModel.addPose(timestamp, x, y, z, yaw)
                                *///Log.d("CURRENT_POS", "${bleViewModel.getTimestampPos()} ${bleViewModel.getXco()} ${bleViewModel.getYco()} ${bleViewModel.getZco()} ${bleViewModel.getYaw()}")
                            } else {
                                buffer.add(packet)
                                if(packet.ptype in ack_packet_types) sendAckPacket(packet, AckPacket.STATUS_OK)
                                checkRecvPacket(packet)
                            }

                            if(startMode) last_pid_recv = packet.pid

                            Log.d("UDPSERVER", "$packet")

                        } else {
                            Log.d("UDPSERVER", "no valid")
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Log.d("BLECapturer", e.toString())
        } finally {
            if(socket != null)
            {
                socket!!.close()
                Log.d("BLECapturer", "socket closed")
            } else {
                Log.d("BLECapturer", "socket not closed")
            }
        }

    }

    private fun checkRecvPacket(packet: Packet) {
        if(packet.ptype == StartCapturePacket.PTYPE) {
            // Start capture thread
            Log.d("CAPTURE", "START")
            start_capture_thread(packet)
        }
        if(packet.ptype == EndCapturePacket.PTYPE) {
            // Stop capture thread
            Log.d("CAPTURE", "STOP")
            stop_capture_thread(packet)
        }

    }

    private fun stop_capture_thread(packet: Packet) {
        //thread(start=true) {
            val db = DatabaseHandler.databaseViewModel?.databaseHelper?.value?.writableDatabase
            if(db != null) {
                try {
                    BLEScanner.stopScanner()
                    db.setTransactionSuccessful()
                } catch(e: Exception) {
                    e.printStackTrace()
                } finally {
                    db.endTransaction()
                }
            }
        //}
    }

    private fun start_capture_thread(packet: Packet) {
        //thread(start=true) {
            val startCapturePacket = (packet as StartCapturePacket)
            //val captureTime = (startCapturePacket.captureTime * 1000).toLong()

            //Log.d("CAPTURE_POS", "${startCapturePacket.x} ${startCapturePacket.y} ${startCapturePacket.z} ${startCapturePacket.yaw}")
            //bleViewModel.addPose(startCapturePacket.x, startCapturePacket.y, startCapturePacket.z, startCapturePacket.yaw)
            //Log.d("CAPTURE_POS", "X: ${bleViewModel.getXco()} Y: ${bleViewModel.getYco()} Z: ${bleViewModel.getZco()} YAW: ${bleViewModel.getYaw()}")

            val db = DatabaseHandler.databaseViewModel?.databaseHelper?.value?.writableDatabase
            Log.d("DBHANDLER", "$db ${db == null}")
            if(db != null) {
                try {
                    db.beginTransaction()

                    BLEScanner.startScanner()
                    //sleep(captureTime)
                    //BLEScanner.stopScanner()

                    //db.setTransactionSuccessful()

                } catch(e: Exception) {
                    e.printStackTrace()
                } finally {
                    //db.endTransaction()
                }
            }

            //last_pid_sent += 1
            //val pid = last_pid_sent
            //ConnectionHandler.sendEndCapture(pid)
            // buffer.remove(packet)
       // }
    }

}
