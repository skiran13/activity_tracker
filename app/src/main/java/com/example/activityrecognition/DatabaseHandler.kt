package com.example.activityrecognition

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DATABASE_NAME = "ActivityDB"
val TABLE_NAME = "ActivityData"
val COL_ID = "id"
val COL_ACTIVITY = "activity_name"
val COL_DATE = "date"
val COL_TIME = "start_time"
val COL_DURATION = "duration"

class DatabaseHandler(var context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE "+ TABLE_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_ACTIVITY + " VARCHAR(25), "+
                COL_DATE + " VARCHAR(25), "+
                COL_TIME + " VARCHAR(30), "+
                COL_DURATION + " VARCHAR(50))"

        db?.execSQL(createTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun insertData(activity: Activity){
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_ACTIVITY, activity.activity_name)
        cv.put(COL_DATE, activity.date)
        cv.put(COL_TIME, activity.start_time)
        cv.put(COL_DURATION, activity.duration)

        var result = db.insert(TABLE_NAME,null, cv)

        if(result == -1.toLong()) {
            Toast.makeText(context, "Failed to Insert", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(context, "Inserted into DB", Toast.LENGTH_SHORT).show()
        }
    }
}