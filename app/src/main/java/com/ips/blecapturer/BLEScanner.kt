package com.ips.blecapturer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import com.ips.blecapturer.model.BLESharedViewModel
import com.ips.blecapturer.model.Beacon
import com.ips.blecapturer.model.BeaconWhiteList

// Inspiration:
//  https://medium.com/@nithinjith.p/ble-in-android-kotlin-c485f0e83c16
//  https://medium.com/geekculture/android-ble-scanner-to-scan-for-ibeacon-and-eddystone-96a0c0610d3d

object BLEScanner {

    private lateinit var btScanner: BluetoothLeScanner
    private lateinit var btAdapter: BluetoothAdapter
    private lateinit var btManager: BluetoothManager

    private lateinit var bleViewModel: BLESharedViewModel

    private var beaconWhiteList: BeaconWhiteList = BeaconWhiteList()

    fun allowBeacon(mac: String, protocol: Beacon.Protocol) {
        beaconWhiteList.addBeacon(mac, protocol)
    }

    fun denyBeacon(mac: String, protocol: Beacon.Protocol) {
        beaconWhiteList.removeBeacon(mac, protocol)
    }

    fun allowAllBeacons() {
        beaconWhiteList.clearAll()
    }

    fun getFromWhiteList(position: Int): Pair<String, Beacon.Protocol> {
        return beaconWhiteList.getBeacon(position)
    }

    fun getWhiteListSize(): Int = beaconWhiteList.size()

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupBLEManager(context: Context, viewModel: BLESharedViewModel) : Boolean
    {
        btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter
        btScanner = btAdapter.bluetoothLeScanner

        bleViewModel = viewModel

        return !btAdapter.isEnabled
    }

    fun isEnabled() : Boolean {
        return btAdapter.isEnabled
    }

    fun startScanner()
    {
        btScanner.startScan(leScanCallback)
    }

    fun stopScanner()
    {
        btScanner.stopScan(leScanCallback)
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            var iBeaconManufacturerId: Int = 0x004c
            var eddystoneServiceId : ParcelUuid = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB")

            val deviceName = result?.device?.name
            val deviceAddress = result?.device?.address
            val rssi = result?.rssi

            var beacon_type = Beacon.Protocol.OTHER

            val scanRecord = result?.scanRecord

            if(scanRecord != null)
            {
                val serviceUuids = scanRecord.serviceUuids

                if(serviceUuids != null && serviceUuids.size > 0 && serviceUuids.contains(eddystoneServiceId)) {
                    beacon_type = Beacon.Protocol.EDDYSTONE
                }

                val iBeaconManufactureData = scanRecord.getManufacturerSpecificData(iBeaconManufacturerId)
                if(iBeaconManufactureData != null && iBeaconManufactureData.size >= 23) {
                    beacon_type = Beacon.Protocol.IBEACON
                }
            }

            var allowed = false
            if(deviceAddress != null) {
                val beacon = Beacon(deviceAddress)
                beacon.name = deviceName
                beacon.protocol = beacon_type
                beacon.rssi = rssi
                bleViewModel.addBeacon(beacon)
                allowed = beaconWhiteList.filter(beacon)
            }

            Log.d("BLEScan", "Name : $deviceName Address : $deviceAddress RSSI : $rssi TYPE : $beacon_type   NDEVICES = ${bleViewModel.visibleBeacons()}  ALLOWED : $allowed" )
        }
    }

}