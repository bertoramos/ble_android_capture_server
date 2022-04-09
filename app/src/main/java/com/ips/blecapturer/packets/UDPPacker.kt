package com.ips.blecapturer.packets

import android.os.Message
import org.msgpack.core.MessagePack

object UDPPacker {

    fun pack(packet: Packet): ByteArray?
    {
        return when(packet.ptype)
        {
            0 -> packPacket(packet)
            AckPacket.PTYPE -> packAckPacket(packet as AckPacket)
            ModePacket.PTYPE -> packModePacket(packet as ModePacket)
            StartCapturePacket.PTYPE -> packStartPacket(packet as StartCapturePacket)
            EndCapturePacket.PTYPE -> packEndPacket(packet as EndCapturePacket)
            CloseServerPacket.PTYPE -> packCloseServerPacket(packet as CloseServerPacket)
            else -> null
        }
    }

    fun packPacket(packet: Packet): ByteArray {
        val packer = MessagePack.newDefaultBufferPacker()
        packer
            .packArrayHeader(2)
            .packLong(packet.pid)
            .packInt(packet.ptype)
        return packer.toByteArray()
    }

    fun packAckPacket(packet: AckPacket): ByteArray {
        val packer = MessagePack.newDefaultBufferPacker()
        packer
            .packArrayHeader(4)
            .packLong(packet.pid)
            .packInt(packet.ptype)
            .packLong(packet.ackPid)
            .packInt(packet.status)
        return packer.toByteArray()
    }

    fun packModePacket(packet: ModePacket): ByteArray {
        val packer = MessagePack.newDefaultBufferPacker()
        packer
            .packArrayHeader(3)
            .packLong(packet.pid)
            .packInt(packet.ptype)
            .packInt(packet.mode)
        return packer.toByteArray()
    }

    fun packStartPacket(packet: StartCapturePacket): ByteArray {
        val packer = MessagePack.newDefaultBufferPacker()
        packer
            .packArrayHeader(7)
            .packLong(packet.pid)
            .packInt(packet.ptype)
            .packFloat(packet.captureTime)
            .packFloat(packet.x)
            .packFloat(packet.y)
            .packFloat(packet.z)
            .packFloat(packet.yaw)
        return packer.toByteArray()
    }

    fun packEndPacket(packet: EndCapturePacket): ByteArray {
        val packer = MessagePack.newDefaultBufferPacker()
        packer
            .packArrayHeader(2)
            .packLong(packet.pid)
            .packInt(packet.ptype)
        return packer.toByteArray()
    }

    fun packCloseServerPacket(packet: CloseServerPacket): ByteArray {
        val packer = MessagePack.newDefaultBufferPacker()
        packer
            .packArrayHeader(2)
            .packLong(packet.pid)
            .packInt(packet.ptype)
        return packer.toByteArray()
    }
}