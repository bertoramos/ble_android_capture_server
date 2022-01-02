package com.ips.blecapturer.view.fragments

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ips.blecapturer.BLEScanner
import com.ips.blecapturer.R
import com.ips.blecapturer.model.BLESharedViewModel
import com.ips.blecapturer.model.database.DatabaseHandler
import com.ips.blecapturer.model.database.DatabaseViewModel
import com.ips.blecapturer.view.BleDeviceAdapter
import com.ips.blecapturer.view.activities.MainActivity

class BleFragment : Fragment() {

    private val ble_model : BLESharedViewModel by viewModels()

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val PERMISSION_REQUEST_COARSE_LOCATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflaterView = inflater.inflate(R.layout.fragment_ble, container, false)

        if( BLEScanner.setupBLEManager(context!!, ble_model) )
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT)

        checkLocationPermissions()

        scanButtonSetup(inflaterView)

        bleDevicesList(inflaterView)

        return inflaterView
    }

    private fun bleDevicesList(view: View) {
        val beaconsList: RecyclerView = view.findViewById(R.id.ble_devices_list)
        val beaconAdapter = BleDeviceAdapter()
        beaconsList.adapter = beaconAdapter

        beaconsList.layoutManager = LinearLayoutManager(context)

        ble_model.beacons_live_map.observe(this, {
            val collection = it.values
            val list = ArrayList(collection)

            beaconAdapter.devices = list
            beaconAdapter.notifyDataSetChanged()

            Log.d("BLE_LIST", "New beacon ${list.size}")
        })

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scanButtonSetup(view: View) {
        val scanButton = view.findViewById<ToggleButton>(R.id.toggleCaptureButton)
        scanButton.setOnClickListener {
            val locationPermissionGranted = activity!!.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val bleActive = BLEScanner.isEnabled()
            if(scanButton.isChecked) {
                if(locationPermissionGranted && bleActive) {
                    BLEScanner.startScanner()
                } else {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("This app requires bluetooth")
                    builder.setMessage("Please activate bluetooth")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener {
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                            PERMISSION_REQUEST_COARSE_LOCATION
                        )
                    }
                    builder.show()
                    scanButton.isChecked = false
                }
            } else BLEScanner.stopScanner()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkLocationPermissions()
    {
        if(context?.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("This app requires location permission")
            builder.setMessage("Please grant location access")
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setOnDismissListener {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_COARSE_LOCATION
                )
            }
            builder.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode)
        {
            PERMISSION_REQUEST_COARSE_LOCATION -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Se concedieron los permisos
                } else {
                    // Informa que no tiene permisos para escanear dispositivos BLE
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted")
                    builder.setOnDismissListener {  }
                    builder.show()
                }
                return
            }
        }
    }

}