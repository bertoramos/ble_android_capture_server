package com.ips.blecapturer.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ips.blecapturer.model.database.tables.Capture
import com.ips.blecapturer.model.database.tables.Scan
import java.io.*

class CampaignDatabaseHelper(val context: Context, private val database_name: String): SQLiteOpenHelper(context, "$database_name$DB_EXTENSION", null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DB_EXTENSION = ".db"
    }

    val database_file_name = "$database_name$DB_EXTENSION"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Capture.DROP_TABLE)
        db.execSQL(Scan.DROP_TABLE)
        db.execSQL(Capture.CREATE_TABLE)
        db.execSQL(Scan.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {  }

    fun copy() {
        //Log.d("COPYDB", "${context.packageName} $database_name $database_file_name ${this.databaseName} ${this.writableDatabase.path} ${this.readableDatabase.path}")
        //val db = File("/data/data/" + context.packageName + "/databases/$database_file_name")
        val db = File(this.writableDatabase.path)
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