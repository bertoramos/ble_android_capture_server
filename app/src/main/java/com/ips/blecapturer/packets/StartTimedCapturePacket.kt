package com.ips.blecapturer.packets

class StartTimedCapturePacket(pid: Long, val captureTime: Float) : Packet(pid, PTYPE) {

    companion object {
        val PTYPE = 7
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(pid=${this.pid}, ptype=${this.ptype}}, captureTime=${this.captureTime})"
    }

}
