package com.ips.blecapturer.packets

class ModePacket(pid: Long, val mode: Int) : Packet(pid, ModePacket.PTYPE) {

    companion object {
        val PTYPE = 2
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(pid=${this.pid}, ptype=${this.ptype}, mode=${this.mode})"
    }
}
