package com.ips.blecapturer.packets

class AckPacket(pid: Long, val ackPid: Long, val status: Int) : Packet(pid, AckPacket.PTYPE) {

    companion object {
        val PTYPE = 1

        val STATUS_OK = 0
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(pid=${this.pid}, ptype=${this.ptype}, ackPacket=${this.ackPid}, status=${this.status})"
    }
}