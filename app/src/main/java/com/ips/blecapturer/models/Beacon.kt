package com.ips.blecapturer.models

data class Beacon(public var mac: String) {

    enum class Protocol {
        ANY,
        IBEACON,
        EDDYSTONE
    }

    var name: String? = null
    var protocol: Protocol = Protocol.ANY
    var rssi: Int? = null
}
