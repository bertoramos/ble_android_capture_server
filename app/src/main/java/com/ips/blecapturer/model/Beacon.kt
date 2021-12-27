package com.ips.blecapturer.model

data class Beacon(public var mac: String) {

    enum class Protocol {
        ANY,
        IBEACON,
        EDDYSTONE
    }

    var name: String? = null
    var protocol: Protocol = Protocol.ANY
    var rssi: Int? = null

    override fun equals(other: Any?): Boolean {
        val b = other as Beacon
        return mac == b.mac && protocol == b.protocol
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
