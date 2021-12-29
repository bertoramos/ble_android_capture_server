package com.ips.blecapturer.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ips.blecapturer.model.Beacon
import com.ips.blecapturer.model.database.tables.Capture
import com.ips.blecapturer.model.database.tables.Frame
import java.io.*

class CampaignDatabaseHelper(val context: Context, private val database_name: String): SQLiteOpenHelper(context, database_name, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
    }

    val DROP_CAPTURE_TABLE = "DROP TABLE IF EXISTS ${Capture.TABLE_NAME}"

    val CREATE_CAPTURE_TABLE = "CREATE TABLE ${Capture.TABLE_NAME}" +
            "(" +
                "capture_id" + " BIGINT PRIMARY KEY, " +
                "x_co" + " REAL, " +
                "y_co" + " REAL, " +
                "z_co" + " REAL " +
            ")"

    val DROP_FRAME_TABLE = "DROP TABLE IF EXISTS ${Frame.TABLE_NAME}"

    val CREATE_FRAME_TABLE = "CREATE TABLE ${Frame.TABLE_NAME}" +
            "(" +
                "frame_id" + " BIGINT PRIMARY KEY," +
                "mac" + " VARCHAR(20)," +
                "protocol" + " TEXT CHECK( protocol IN ('${Beacon.Protocol.EDDYSTONE}', '${Beacon.Protocol.IBEACON}','${Beacon.Protocol.OTHER}') ) NOT NULL DEFAULT '${Beacon.Protocol.OTHER}'," +
                "rssi" + " INT," +
                "capture_id_fk" + " BIGINT," +
                "FOREIGN KEY(capture_id_fk) REFERENCES capture(capture_id)" +
            ")"

    val database_file_name = "$database_name.db"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DROP_CAPTURE_TABLE)
        db.execSQL(DROP_FRAME_TABLE)
        db.execSQL(CREATE_CAPTURE_TABLE)
        db.execSQL(CREATE_FRAME_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {

    }

    fun copy() {
        val db = File("/data/data/" + context.packageName + "/databases/$database_file_name")

        try {
            val inputStream: InputStream = FileInputStream(db)

            val file = File(context.getExternalFilesDir(null), "/$database_file_name")
            val outputStream: OutputStream = FileOutputStream( file.path, false)
            val buff = ByteArray(4048)

            var len : Int
            while (inputStream.read(buff).also { len = it } > 0) {
                outputStream.write(buff, 0, len)
                outputStream.flush()
            }
            outputStream.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}