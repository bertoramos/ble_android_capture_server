package com.ips.blecapturer.model.database

import android.content.ContentValues
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ips.blecapturer.model.Beacon
import com.ips.blecapturer.model.database.tables.Frame
import com.ips.blecapturer.model.database.tables.Position

class DatabaseViewModel : ViewModel() {

    private lateinit var databaseHelper: MutableLiveData<CampaignDatabaseHelper>
    private lateinit var currentCaptureId: MutableLiveData<Long>

    fun createDatabase(context: Context, databaseName: String) {
        databaseHelper.value = CampaignDatabaseHelper(context, databaseName)
    }

    fun updateDatabaseFile() {
        databaseHelper.value?.copy()
    }

    fun insertFrame(mac: String,
                    protocol: Beacon.Protocol,
                    rssi: Int): Boolean
    {
        val db = databaseHelper.value?.writableDatabase
        val values = ContentValues().apply {
            put(Frame.COLUMN_NAME_MAC, mac)
            put(Frame.COLUMN_NAME_PROTOCOL, protocol.toString())
            put(Frame.COLUMN_NAME_RSSI, rssi)
        }

        val newRowId = db?.insert(Frame.TABLE_NAME, null, values)

        return if (newRowId != null) newRowId > 0  else false
    }

    fun insertCapture(position: Position)
    {
        // TODO: INSERTAR CAPTURA Y OBTENER CAPTURE_ID
        // COMO SELECCIONAR
            // https://developer.android.com/training/data-storage/sqlite?hl=es
        // CONSULTA DE ROWID
            // SELECT frame_id FROM frame WHERE ROWID=$newRowId
        //
    }

}