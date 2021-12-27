package com.ips.blecapturer.model

class BeaconWhiteList {

    private val beaconWhiteList = arrayListOf<Pair<String, Beacon.Protocol>>() // MAC & Protocol

    fun addBeacon(mac: String, protocol: Beacon.Protocol) {
        beaconWhiteList.add(Pair(mac, protocol))
    }

    fun removeBeacon(mac:String, protocol: Beacon.Protocol) {
        for (i in 0 until beaconWhiteList.size) {
            if(beaconWhiteList[i].first == mac && beaconWhiteList[i].second == protocol) {
                beaconWhiteList.removeAt(i)
                break
            }
        }
    }

    fun clearAll() {
        beaconWhiteList.clear()
    }

    fun filter(beacon: Beacon) : Boolean {
        if(beaconWhiteList.isEmpty()) return true
        for (prop in beaconWhiteList)
        {
            if(beacon.mac == prop.first && beacon.protocol == prop.second) return true
        }
        return false
    }

    fun filter(list: ArrayList<Beacon>) : ArrayList<Beacon> {
        val result = arrayListOf<Beacon>()
        for(beacon in list) {
            for(prop in beaconWhiteList) {
                if(beacon.mac == prop.first && beacon.protocol == prop.second) result.add(beacon)
            }
        }
        return result
    }

}