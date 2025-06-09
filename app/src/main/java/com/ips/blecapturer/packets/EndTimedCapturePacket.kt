package com.ips.blecapturer.packets

class EndTimedCapturePacket(pid: Long) : Packet(pid, PTYPE) {

    companion object {
        val PTYPE = 8
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(pid=${this.pid}, ptype=${this.ptype})"
    }
}
