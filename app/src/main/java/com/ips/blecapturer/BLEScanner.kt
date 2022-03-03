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
import com.ips.blecapturer.model.database.DatabaseHandler
import com.ips.blecapturer.model.database.tables.Pose
import java.util.*

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

    fun getDeviceName() {
        Log.d("BLUETOOTH_ADAPTER", btAdapter.name)
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
    fun setupBLEManager(context: Context, bleViewModel: BLESharedViewModel) : Boolean
    {
        btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter
        if(btAdapter.bluetoothLeScanner == null) return false
        btScanner = btAdapter.bluetoothLeScanner

        this.bleViewModel = bleViewModel

        getDeviceName()

        return btAdapter.isEnabled
    }

    fun isEnabled() : Boolean {
        return btAdapter.isEnabled
    }

    fun startScanner()
    {
        btScanner.startScan(leScanCallback)

        val x = bleViewModel.getXco() ?: 0.0f
        val y = bleViewModel.getYco() ?: 0.0f
        val z = bleViewModel.getZco() ?: 0.0f
        val yaw = bleViewModel.getYaw() ?: 0.0f

        val pos = Pose(x, y, z, yaw)
        val timestamp = Date().time
        DatabaseHandler.databaseViewModel?.insertCapture(timestamp, pos)
    }

    fun stopScanner()
    {
        btScanner.stopScan(leScanCallback)
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {

        override fun onScanFailed(errorCode: Int) {
            // TODO: Control de error
            super.onScanFailed(errorCode)
        }


        @RequiresApi(Build.VERSION_CODES.O)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            val device = result?.device
            val scanRecord = result?.scanRecord

            val deviceAddress = device?.address
            val deviceName = device?.name
            var beacon_type = Beacon.Protocol.OTHER
            val rssi = result?.rssi
            val txpower = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) result?.txPower ?: 0 else 0
            val timestamp = Date().time

            // GET BEACON TYPE
            if(scanRecord != null)
            {
                var iBeaconManufacturerId = 0x004c
                var eddystoneServiceId : ParcelUuid = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB")

                val serviceUuids = scanRecord.serviceUuids
                if(serviceUuids != null && serviceUuids.size > 0 && serviceUuids.contains(eddystoneServiceId)) beacon_type = Beacon.Protocol.EDDYSTONE
                val iBeaconManufactureData = scanRecord.getManufacturerSpecificData(iBeaconManufacturerId)
                if(iBeaconManufactureData != null && iBeaconManufactureData.size >= 23) beacon_type = Beacon.Protocol.IBEACON
            }

            var allowed = false

            if(deviceAddress != null && rssi != null) {
                val beacon = Beacon(deviceAddress)
                beacon.name = deviceName
                beacon.protocol = beacon_type
                beacon.rssi = rssi
                beacon.txpower = txpower
                allowed = beaconWhiteList.filter(beacon)

                if(beaconWhiteList.size() == 0) {
                    bleViewModel.addBeacon(beacon)
                    DatabaseHandler.databaseViewModel?.insertScan(timestamp, deviceAddress, beacon_type, rssi, txpower)
                } else if(allowed) {
                    bleViewModel.addBeacon(beacon)
                    DatabaseHandler.databaseViewModel?.insertScan(timestamp, deviceAddress, beacon_type, rssi, txpower)
                }
            }

            val freq = result?.periodicAdvertisingInterval

            Log.d("BLEScan",
                "Name : $deviceName " +
                     "Address : $deviceAddress " +
                     "RSSI : $rssi " +
                     "TYPE : $beacon_type " +
                     "TXPOWER : $txpower " +
                     "NDEVICES = ${bleViewModel.visibleBeacons()} " +
                     "ALLOWED : $allowed"
            )

        }
    }

}