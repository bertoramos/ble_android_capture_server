package com.ips.blecapturer.model.database

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ips.blecapturer.model.Beacon
import com.ips.blecapturer.model.database.tables.Capture
import com.ips.blecapturer.model.database.tables.Scan
import com.ips.blecapturer.model.database.tables.Pose

class DatabaseViewModel : ViewModel() {

    val databaseHelper by lazy {
        MutableLiveData<CampaignDatabaseHelper>()
    }
    private val currentCaptureId by lazy {
        MutableLiveData<Long>()
    }

    val databaseName by lazy {
        MutableLiveData<String>()
    }

    private lateinit var context: Context

    fun isDatabaseCreated() : Boolean = databaseHelper.value != null

    fun createDatabase(context: Context, databaseName: String) {
        databaseHelper.value = CampaignDatabaseHelper(context, databaseName)
        this.context = context
        databaseHelper.value!!.copy()

        this.databaseName.value = databaseHelper.value!!.databaseName
    }

    fun closeDatabase() {
        updateDatabaseFile()

        databaseHelper.value = null
        currentCaptureId.value = null
        this.databaseName.value = ""
    }
    
    fun updateDatabaseFile() {
        databaseHelper.value?.copy()
    }

    fun insertScan(
        timestamp_ble: Long,
        timestamp_pos: Long,
        mac: String,
        protocol: Beacon.Protocol,
        rssi: Int,
        txpower: Int,
        pose: Pose
    ): Boolean
    {
        //if(currentCaptureId.value == null) return false

        val db = databaseHelper.value?.writableDatabase
        val values = Scan.insertValues(timestamp_ble, timestamp_pos, mac, protocol, rssi, txpower, pose)

        val newRowId = db?.insert(Scan.TABLE_NAME, null, values)

        val rescode = if (newRowId != null) newRowId > 0 else false
        if(rescode) this.updateDatabaseFile()
        
        return rescode
    }

    fun insertCapture(timestamp: Long, pose: Pose): Boolean
    {
        val db_write = databaseHelper.value?.writableDatabase
        val values = Capture.insertValues(timestamp, pose)

        val newRowId = db_write?.insert(Capture.TABLE_NAME, null, values)

        currentCaptureId.postValue(newRowId)

        val rescode = if(newRowId != null) newRowId > 0 else false
        if(rescode) this.updateDatabaseFile()
        return rescode
    }

}