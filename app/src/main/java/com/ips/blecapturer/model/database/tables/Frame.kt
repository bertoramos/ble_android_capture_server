package com.ips.blecapturer.model.database.tables

import com.ips.blecapturer.model.Beacon

data class Frame(
    val frame_id: Long,
    val mac: String, // 00:00:00:00:00:00
    val protocol: Beacon.Protocol,
    val rssi: Int,
    val capture_id_fk: Long
    ) {

    companion object {

        const val TABLE_NAME = "frame"
        const val COLUMN_NAME_FRAME_ID = "frame_id"
        const val COLUMN_NAME_MAC = "mac"
        const val COLUMN_NAME_PROTOCOL = "protocol"
        const val COLUMN_NAME_RSSI = "rssi"
        const val COLUMN_NAME_CAPTURE_ID_FK = "capture_id_fk"

        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME" +
                "(" +
                COLUMN_NAME_FRAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_MAC + " VARCHAR(20)," +
                COLUMN_NAME_PROTOCOL + " TEXT CHECK( protocol IN ('${Beacon.Protocol.EDDYSTONE}', '${Beacon.Protocol.IBEACON}','${Beacon.Protocol.OTHER}') ) NOT NULL DEFAULT '${Beacon.Protocol.OTHER}'," +
                COLUMN_NAME_RSSI + " INT," +
                COLUMN_NAME_CAPTURE_ID_FK + " INT," +
                "FOREIGN KEY($COLUMN_NAME_CAPTURE_ID_FK) REFERENCES capture(${Capture.COLUMN_NAME_CAPTURE_ID})" +
                ")"


    }

}