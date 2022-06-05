package com.ips.blecapturer.packets

class PosePacket(pid: Long, val timestamp: Long, val x: Float, val y: Float, val z: Float, val yaw: Float) : Packet(pid, PosePacket.PTYPE) {

    companion object {
        val PTYPE = 6
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(pid=${this.pid}, ptype=${this.ptype}, timestamp=${this.timestamp}, x=${this.x}, y=${this.y}, z=${this.z}, yaw=${this.yaw})"
    }

}
