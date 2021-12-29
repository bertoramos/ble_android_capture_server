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
    }

}