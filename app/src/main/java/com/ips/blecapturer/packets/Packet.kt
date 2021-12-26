package com.ips.blecapturer.packets

open class Packet(val pid: Long, val ptype: Int) {

    override fun toString(): String {
        return "${this.javaClass.simpleName}(pid=${this.pid}, ptype=${this.ptype})"
    }

}
