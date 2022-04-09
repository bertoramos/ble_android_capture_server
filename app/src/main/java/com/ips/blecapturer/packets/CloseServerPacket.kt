package com.ips.blecapturer.packets

class CloseServerPacket(pid: Long): Packet(pid, CloseServerPacket.PTYPE) {

    companion object {
        val PTYPE = 5
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(pid=${this.pid}, ptype=${this.ptype})"
    }

}
