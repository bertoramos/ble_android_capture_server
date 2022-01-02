package com.ips.blecapturer.packets

class StartCapturePacket(pid: Long,
                         val captureTime: Float,
                         val x: Float,
                         val y : Float,
                         val z: Float,
                         val yaw: Float) : Packet(pid, PTYPE) {

    companion object {
        val PTYPE = 3
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(pid=${this.pid}, ptype=${this.ptype}, captureTime=${this.captureTime})"
    }

}
