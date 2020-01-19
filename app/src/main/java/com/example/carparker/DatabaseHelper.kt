package com.example.carparker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.content.contentValuesOf
import com.mapbox.geojson.Point

class DatabaseHelper(context : Context) : SQLiteOpenHelper(context, "latlng.db", null, 1) {

    var context : Context? = null
    init {
        this.context = context
    }

    companion object{
        val DB_VERSION = 1
        val CREATE_TABLE_INFO = "CREATE TABLE info(id INTEGER PRIMARY KEY AUTOINCREMENT, lat DOUBLE, lng DOUBLE)"

    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0!!.execSQL(CREATE_TABLE_INFO)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun insertIntoInfo (lat : Double, lng : Double) : Boolean {
        var contentValue = ContentValues()
        contentValue.put("lat", lat)
        contentValue.put("lng", lng)

        val rowID = writableDatabase.insert("INFO", null, contentValue)
        return  rowID>0
    }


    fun returnAllInfo () : ArrayList<Point> {
        var points : ArrayList<Point> = ArrayList()

        var cursor = readableDatabase.rawQuery("SELECT * FROM info;", null)

        //@TODO FIX THIS CALL TO RETURN A LIST OF POINS
        // THERE IS AN ISSUE WITH THIS CALL BELOW
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val point = Point.fromLngLat(cursor.getDouble(cursor.getColumnIndex("lat")), cursor.getDouble(cursor.getColumnIndex("lng")))
                points.add(point)
                Log.d("DBDB", "point added")
                cursor.moveToNext()
            }
        }

        cursor.close()
        return points
    }

    fun returnLastPark() : Point? {
        var cursor = readableDatabase.rawQuery("SELECT * FROM info ORDER BY id DESC LIMIT 1;", null)
        if (cursor.moveToFirst()) {
            return Point.fromLngLat(cursor.getDouble(cursor.getColumnIndex("lng")), (cursor.getDouble(cursor.getColumnIndex("lat"))))
        }
         return Point.fromLngLat(0.00, 0.00)
    }

}