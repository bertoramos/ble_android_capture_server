package com.ips.blecapturer.model.database.tables

import android.content.ContentValues

data class Capture(
    val capture_id: Long,
    val timestamp: Long,
    val pose: Pose
    ) {

    companion object {

        const val TABLE_NAME = "capture"

        const val COLUMN_NAME_CAPTURE_ID = "capture_id"
        const val COLUMN_NAME_TIMESTAMP = "timestamp"
        const val COLUMN_NAME_X_CO = "x_co"
        const val COLUMN_NAME_Y_CO = "y_co"
        const val COLUMN_NAME_Z_CO = "z_co"
        const val COLUMN_NAME_YAW  = "yaw"

        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME" +
                "(" +
                COLUMN_NAME_CAPTURE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_TIMESTAMP + " DATETIME, " +
                COLUMN_NAME_X_CO + " REAL, " +
                COLUMN_NAME_Y_CO + " REAL, " +
                COLUMN_NAME_Z_CO + " REAL, " +
                COLUMN_NAME_YAW  + " REAL"   +
                ")"

        fun insertValues(timestamp: Long,
                         pose: Pose) : ContentValues
        {
            val xco = pose.x
            val yco = pose.y
            val zco = pose.z
            val yaw = pose.yaw

            return ContentValues().apply {
                put(COLUMN_NAME_TIMESTAMP, timestamp)
                put(COLUMN_NAME_X_CO, xco)
                put(COLUMN_NAME_Y_CO, yco)
                put(COLUMN_NAME_Z_CO, zco)
                put(COLUMN_NAME_YAW, yaw)
            }
        }

    }
}
