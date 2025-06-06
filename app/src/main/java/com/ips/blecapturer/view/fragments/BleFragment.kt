package com.ips.blecapturer.view.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ips.blecapturer.BLEScanner
import com.ips.blecapturer.R
import com.ips.blecapturer.model.BLESharedViewModel
import com.ips.blecapturer.model.database.CampaignDatabaseHelper
import com.ips.blecapturer.model.database.DatabaseHandler
import com.ips.blecapturer.view.BleDeviceAdapter
import java.util.Locale


class BleFragment : Fragment() {

    private val ble_model : BLESharedViewModel by viewModels()
    private var scan_flag_button = false


    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val PERMISSION_REQUEST_COARSE_LOCATION = 2
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

        if( !BLEScanner.setupBLEManager(context!!, ble_model) ) {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BT
            )
        }

        checkLocationPermissions()

        scanButtonSetup(inflaterView)

        bleDevicesList(inflaterView)

        return inflaterView
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                BLEScanner.setupBLEManager(context!!, ble_model)
            }
        }
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
        val scanButton = view.findViewById<FloatingActionButton>(R.id.scan_button)
        scanButton.setOnClickListener {


            val locationPermissionGranted = activity!!.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val bleActive = BLEScanner.isEnabled()
            if(!scan_flag_button) {
                if(locationPermissionGranted && bleActive) {
                    BLEScanner.startScanner()
                    scan_flag_button = true
                    scanButton.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_search_foreground_24dp))
                } else {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("This app requires bluetooth")
                    builder.setMessage("Please activate bluetooth")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener {
                        
                    }
                    builder.show()
                    //scanButton.isChecked = false
                }
            } else {
                BLEScanner.stopScanner()
                scan_flag_button = false
                scanButton.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_search_off_24dp_foreground))
            }
        }

        DatabaseHandler.databaseViewModel?.databaseHelper?.observe(this, { handler: CampaignDatabaseHelper? ->
            if(handler != null) {
                if(scan_flag_button) BLEScanner.stopScanner()

                scan_flag_button = false
                scanButton.isEnabled = false
                scanButton.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_search_off_24dp_foreground))

            } else scanButton.isEnabled = true
        })
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