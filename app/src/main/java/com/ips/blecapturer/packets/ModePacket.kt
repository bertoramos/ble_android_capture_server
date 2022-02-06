package com.ips.blecapturer.packets

class ModePacket(pid: Long, val mode: Int) : Packet(pid, ModePacket.PTYPE) {

    companion object {
        val PTYPE = 2

        val CONNECT = 1
        val DISCONNECT = 0
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(pid=${this.pid}, ptype=${this.ptype}, mode=${this.mode})"
    }
}
