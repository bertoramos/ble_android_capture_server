package com.ips.blecapturer.model.database

import android.content.ContentValues
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ips.blecapturer.model.Beacon
import com.ips.blecapturer.model.database.tables.Capture
import com.ips.blecapturer.model.database.tables.Frame
import com.ips.blecapturer.model.database.tables.Position
import kotlin.coroutines.coroutineContext

class DatabaseViewModel : ViewModel() {

    private val databaseHelper by lazy {
        MutableLiveData<CampaignDatabaseHelper>()
    }
    private val currentCaptureId by lazy {
        MutableLiveData<Long>()
    }

    private lateinit var context: Context

    fun createDatabase(context: Context, databaseName: String) {
        databaseHelper.value = CampaignDatabaseHelper(context, databaseName)
        this.context = context
        databaseHelper.value!!.copy()
    }

    fun updateDatabaseFile() {
        databaseHelper.value?.copy()
    }

    fun insertFrame(mac: String,
                    protocol: Beacon.Protocol,
                    rssi: Int): Boolean
    {
        if(currentCaptureId.value == null) return false

        val db = databaseHelper.value?.writableDatabase
        val values = ContentValues().apply {
            put(Frame.COLUMN_NAME_MAC, mac)
            put(Frame.COLUMN_NAME_PROTOCOL, protocol.toString())
            put(Frame.COLUMN_NAME_RSSI, rssi)
            put(Frame.COLUMN_NAME_CAPTURE_ID_FK, currentCaptureId.value)
        }

        val newRowId = db?.insert(Frame.TABLE_NAME, null, values)

        val rescode = if (newRowId != null) newRowId > 0  else false
        if(rescode) databaseHelper.value?.copy()
        return rescode
    }

    fun insertCapture(position: Position): Boolean
    {

        val xco = position.x
        val yco = position.y
        val zco = position.z

        val db_write = databaseHelper.value?.writableDatabase
        val values = ContentValues().apply {
            put(Capture.COLUMN_NAME_X_CO, xco)
            put(Capture.COLUMN_NAME_Y_CO, yco)
            put(Capture.COLUMN_NAME_Z_CO, zco)
        }

        val newRowId = db_write?.insert(Capture.TABLE_NAME, null, values)

        databaseHelper.value?.copy()

        currentCaptureId.value = newRowId

        return if(newRowId != null) newRowId > 0 else false
        //return if (newRowId != null) newRowId > 0  else false
    }

}