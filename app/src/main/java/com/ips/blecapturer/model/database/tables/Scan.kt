package com.ips.blecapturer.model.database.tables

import android.content.ContentValues
import com.ips.blecapturer.model.Beacon

data class Scan(
    val scan_id: Long,
    val timestamp: Long,
    val mac: String, // 00:00:00:00:00:00
    val protocol: Beacon.Protocol,
    val rssi: Int,
    val xcoo: Float,
    val ycoo: Float,
    val zcoo: Float,
    val yaw : Float
    ) {

    companion object {

        const val TABLE_NAME = "scan"

        const val COLUMN_NAME_SCAN_ID = "scan_id"
        const val COLUMN_NAME_TIMESTAMP_BLE = "timestamp_ble"
        const val COLUMN_NAME_TIMESTAMP_POS = "timestamp_pos"
        const val COLUMN_NAME_MAC = "mac"
        const val COLUMN_NAME_PROTOCOL = "protocol"
        const val COLUMN_NAME_RSSI = "rssi"
        const val COLUMN_NAME_TXPOWER = "txpower"
        const val COLUMN_NAME_XCOO = "xcoo"
        const val COLUMN_NAME_YCOO = "ycoo"
        const val COLUMN_NAME_ZCOO = "zcoo"
        const val COLUMN_NAME_YAW  = "yaw"

        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME" +
                "(" +
                COLUMN_NAME_SCAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_TIMESTAMP_BLE + " DATETIME, " +
                COLUMN_NAME_TIMESTAMP_POS + " DATETIME, " +
                COLUMN_NAME_MAC + " VARCHAR(20)," +
                COLUMN_NAME_PROTOCOL + " TEXT CHECK( protocol IN ('${Beacon.Protocol.EDDYSTONE}', '${Beacon.Protocol.IBEACON}','${Beacon.Protocol.OTHER}') ) NOT NULL DEFAULT '${Beacon.Protocol.OTHER}'," +
                COLUMN_NAME_RSSI + " INT," +
                COLUMN_NAME_TXPOWER + " INT," +
                COLUMN_NAME_XCOO + " FLOAT," +
                COLUMN_NAME_YCOO + " FLOAT, " +
                COLUMN_NAME_ZCOO + " FLOAT, " +
                COLUMN_NAME_YAW  + " FLOAT" +
                ")"

        fun insertValues(
            timestamp_ble: Long,
            timestamp_pos: Long,
            mac: String,
            protocol: Beacon.Protocol,
            rssi: Int,
            txpower: Int,
            pose: Pose) : ContentValues
        {
            val xcoo = pose.x
            val ycoo = pose.y
            val zcoo = pose.z
            val yaw  = pose.yaw

            return ContentValues().apply {
                put(COLUMN_NAME_TIMESTAMP_BLE, timestamp_ble)
                put(COLUMN_NAME_TIMESTAMP_POS, timestamp_pos)
                put(COLUMN_NAME_MAC, mac)
                put(COLUMN_NAME_PROTOCOL, protocol.toString())
                put(COLUMN_NAME_RSSI, rssi)
                put(COLUMN_NAME_TXPOWER, txpower)
                put(COLUMN_NAME_XCOO, xcoo)
                put(COLUMN_NAME_YCOO, ycoo)
                put(COLUMN_NAME_ZCOO, zcoo)
                put(COLUMN_NAME_YAW, yaw)
            }
        }
    }
}
