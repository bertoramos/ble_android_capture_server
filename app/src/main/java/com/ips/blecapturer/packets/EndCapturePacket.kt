package com.ips.blecapturer.packets

class EndCapturePacket(pid: Long) : Packet(pid, EndCapturePacket.PTYPE) {

    companion object {
        val PTYPE = 4
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(pid=${this.pid}, ptype=${this.ptype})"
    }
}
