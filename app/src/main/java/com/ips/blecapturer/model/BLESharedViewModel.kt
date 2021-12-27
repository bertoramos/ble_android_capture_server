package com.ips.blecapturer.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BLESharedViewModel : ViewModel() {

    val beacons_live_map by lazy {
        MutableLiveData<HashMap<Pair<String, Beacon.Protocol>, Beacon>>()
    }

    fun visibleBeacons(): Int? {
        return beacons_live_map.value?.size
    }

    fun addBeacon(beacon: Beacon) {
        if(beacons_live_map.value == null) beacons_live_map.value = HashMap()
        beacons_live_map.value?.set(Pair(beacon.mac, beacon.protocol), beacon)
        beacons_live_map.value = beacons_live_map.value
    }

    fun getBeacon(mac: String, protocol : Beacon.Protocol): Beacon? {
        return beacons_live_map.value?.get(Pair(mac, protocol))
    }

}
