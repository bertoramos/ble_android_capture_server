package com.ips.blecapturer.model.database.tables

data class Capture(
    val capture_id: Long,
    val position: Position
    ) {

    companion object {
        const val TABLE_NAME = "capture"
        const val COLUMN_NAME_CAPTURE_ID = "capture_id"
        const val COLUMN_NAME_X_CO = "x_co"
        const val COLUMN_NAME_Y_CO = "y_co"
        const val COLUMN_NAME_Z_CO = "z_co"
    }

}
