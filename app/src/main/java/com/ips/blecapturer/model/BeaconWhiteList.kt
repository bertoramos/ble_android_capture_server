package com.ips.blecapturer.model

class BeaconWhiteList {

    val beaconWhiteList = arrayListOf<Pair<String, Beacon.Protocol>>() // MAC & Protocol

    /*
    init {

        beaconWhiteList.add(Pair("01:02:03:04:05:06", Beacon.Protocol.EDDYSTONE))
        beaconWhiteList.add(Pair("01:02:03:04:05:06", Beacon.Protocol.IBEACON))
        beaconWhiteList.add(Pair("06:05:04:03:02:01", Beacon.Protocol.EDDYSTONE))
        beaconWhiteList.add(Pair("06:05:04:03:02:01", Beacon.Protocol.IBEACON))
    }
    */

    fun contains(mac: String, protocol: Beacon.Protocol): Boolean {
        return beaconWhiteList.contains(Pair(mac, protocol))
    }

    fun addBeacon(mac: String, protocol: Beacon.Protocol) {
        var p = Pair(mac, protocol)
        if(!beaconWhiteList.contains(p)) beaconWhiteList.add(p)
    }

    fun removeBeacon(mac:String, protocol: Beacon.Protocol) {
        for (i in 0 until beaconWhiteList.size) {
            if(beaconWhiteList[i].first == mac && beaconWhiteList[i].second == protocol) {
                beaconWhiteList.removeAt(i)
                break
            }
        }
    }

    fun getBeacon(position: Int): Pair<String, Beacon.Protocol> {
        return beaconWhiteList[position]
    }

    fun size(): Int = beaconWhiteList.size

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