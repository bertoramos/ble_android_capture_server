package com.ips.blecapturer.packets

import com.ips.blecapturer.model.database.tables.Pose
import org.msgpack.core.MessagePack
import org.msgpack.core.MessageUnpacker

object UDPUnpacker {

    fun unpack(buf: ByteArray): Packet? {
        var unpacker = MessagePack.newDefaultUnpacker(buf)
        var len = unpacker.unpackArrayHeader()

        if (len >= 2) {

            val pid = unpacker.unpackLong()
            val ptype = unpacker.unpackInt()

            return when (ptype) {
                0 -> Packet(pid, ptype)
                AckPacket.PTYPE -> unpackAckPacket(pid, ptype, unpacker, len)
                ModePacket.PTYPE -> unpackModePacket(pid, ptype, unpacker, len)
                StartCapturePacket.PTYPE -> unpackStartCapturePacket(pid, ptype, unpacker, len)
                EndCapturePacket.PTYPE -> unpackEndCapturePacket(pid, ptype, unpacker, len)
                CloseServerPacket.PTYPE -> unpackCloseServerPacket(pid, ptype, unpacker, len)
                PosePacket.PTYPE -> unpackPosePacket(pid, ptype, unpacker, len)
                else -> null
            }

        }
        return null
    }

    private fun unpackAckPacket(pid: Long, ptype: Int, unpacker: MessageUnpacker, size: Int): AckPacket? {
        if(size == 4) {
            val ackPacket = unpacker.unpackLong()
            val status = unpacker.unpackInt()
            return AckPacket(pid, ackPacket, status)
        }
        return null
    }

    private fun unpackModePacket(pid: Long, ptype: Int, unpacker: MessageUnpacker, size: Int): ModePacket? {
        if(size == 3) {
            val mode = unpacker.unpackInt()
            return ModePacket(pid, mode)
        }
        return null
    }

    private fun unpackStartCapturePacket(pid: Long, ptype: Int, unpacker: MessageUnpacker, size: Int): StartCapturePacket? {
        if(size == 2) {
            return StartCapturePacket(pid)
        }
        return null
    }

    private fun unpackEndCapturePacket(pid: Long, ptype: Int, unpacker: MessageUnpacker, size: Int): EndCapturePacket? {
        if(size == 2) {
            return EndCapturePacket(pid)
        }
        return null
    }

    private fun unpackCloseServerPacket(pid: Long, ptype: Int, unpacker: MessageUnpacker, size: Int): CloseServerPacket? {
        if(size == 2) {
            return CloseServerPacket(pid)
        }
        return null
    }


    private fun unpackPosePacket(pid: Long, ptype: Int, unpacker: MessageUnpacker, size: Int): PosePacket? {
        if(size == 7) {
            val timestamp = unpacker.unpackLong()
            val x = unpacker.unpackFloat()
            val y = unpacker.unpackFloat()
            val z = unpacker.unpackFloat()
            val yaw = unpacker.unpackFloat()
            return PosePacket(pid, timestamp, x, y, z, yaw)
        }
        return null
    }

}
