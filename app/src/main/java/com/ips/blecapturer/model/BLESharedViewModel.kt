package com.ips.blecapturer.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BLESharedViewModel : ViewModel() {

    val beacons_live_map by lazy {
        MutableLiveData<HashMap<Pair<String, Beacon.Protocol>, Beacon>>()
    }

    private val last_timestamp_pos by lazy {
        MutableLiveData<Long>()
    }

    private val last_xco by lazy {
        MutableLiveData<Float>()
    }

    private val last_yco by lazy {
        MutableLiveData<Float>()
    }

    private val last_zco by lazy {
        MutableLiveData<Float>()
    }

    private val last_yaw by lazy {
        MutableLiveData<Float>()
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

    fun addPose(timestamp_pos: Long, x: Float, y: Float, z: Float, yaw: Float) {
        last_timestamp_pos.postValue(timestamp_pos)
        last_xco.postValue(x)
        last_yco.postValue(y)
        last_zco.postValue(z)
        last_yaw.postValue(yaw)
    }

    fun getXco(): Float? = last_xco.value
    fun getYco(): Float? = last_yco.value
    fun getZco(): Float? = last_zco.value
    fun getYaw(): Float? = last_yaw.value
    fun getTimestampPos(): Long? = last_timestamp_pos.value

}
