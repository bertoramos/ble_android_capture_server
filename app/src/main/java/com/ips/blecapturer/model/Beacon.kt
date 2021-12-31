package com.ips.blecapturer.model

data class Beacon(var mac: String) {

    enum class Protocol {
        OTHER,
        IBEACON,
        EDDYSTONE,;

        override fun toString(): String {
            return when(this) {
                OTHER -> "Other"
                IBEACON -> "iBeacon"
                EDDYSTONE -> "Eddystone"
            }
        }
    }


    var name: String? = null
    var protocol: Protocol = Protocol.OTHER
    var rssi: Int? = null
    var txpower: Int? = null

    override fun equals(other: Any?): Boolean {
        val b = other as Beacon
        return mac == b.mac && protocol == b.protocol
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

}
