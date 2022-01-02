package com.ips.blecapturer.model.database.tables

import android.content.ContentValues
import com.ips.blecapturer.model.Beacon

data class Scan(
    val scan_id: Long,
    val timestamp: Long,
    val mac: String, // 00:00:00:00:00:00
    val protocol: Beacon.Protocol,
    val rssi: Int,
    val capture_id_fk: Long
    ) {

    companion object {

        const val TABLE_NAME = "scan"

        const val COLUMN_NAME_SCAN_ID = "scan_id"
        const val COLUMN_NAME_TIMESTAMP = "timestamp"
        const val COLUMN_NAME_MAC = "mac"
        const val COLUMN_NAME_PROTOCOL = "protocol"
        const val COLUMN_NAME_RSSI = "rssi"
        const val COLUMN_NAME_TXPOWER = "txpower"
        const val COLUMN_NAME_CAPTURE_ID_FK = "capture_id_fk"

        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME" +
                "(" +
                COLUMN_NAME_SCAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_TIMESTAMP + " DATETIME, " +
                COLUMN_NAME_MAC + " VARCHAR(20)," +
                COLUMN_NAME_PROTOCOL + " TEXT CHECK( protocol IN ('${Beacon.Protocol.EDDYSTONE}', '${Beacon.Protocol.IBEACON}','${Beacon.Protocol.OTHER}') ) NOT NULL DEFAULT '${Beacon.Protocol.OTHER}'," +
                COLUMN_NAME_RSSI + " INT," +
                COLUMN_NAME_TXPOWER + " INT," +
                COLUMN_NAME_CAPTURE_ID_FK + " INT," +
                "FOREIGN KEY($COLUMN_NAME_CAPTURE_ID_FK) REFERENCES ${Capture.TABLE_NAME}(${Capture.COLUMN_NAME_CAPTURE_ID})" +
                ")"

        fun insertValues(
            timestamp: Long,
            mac: String,
            protocol: Beacon.Protocol,
            rssi: Int,
            txpower: Int,
            capture_id_fk: Long) : ContentValues
        {
            return ContentValues().apply {
                put(COLUMN_NAME_TIMESTAMP, timestamp)
                put(COLUMN_NAME_MAC, mac)
                put(COLUMN_NAME_PROTOCOL, protocol.toString())
                put(COLUMN_NAME_RSSI, rssi)
                put(COLUMN_NAME_TXPOWER, txpower)
                put(COLUMN_NAME_CAPTURE_ID_FK, capture_id_fk)
            }
        }
    }
}