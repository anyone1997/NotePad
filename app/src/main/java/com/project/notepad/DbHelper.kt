package com.project.notepad

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {
    companion object {
        // If you change the database schema, you must increment the database version.
        const val ID_COL = "id"
        const val TABLE_NAME = "notes"
        const val NOTE_NAME = "noteName"
        const val NOTE_DESCRIPTION = "noteDescription"
        const val DATABASE_VERSION = 5
        const val DATABASE_NAME = "Notes.db"
        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$NOTE_NAME TEXT," +
                "$NOTE_DESCRIPTION TEXT)") // Creating a table
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Perform necessary database upgrades, e.g., ALTER TABLE, CREATE TABLE, etc.
        db.execSQL(SQL_DELETE_ENTRIES) // Example of dropping a table
        onCreate(db) // Recreate the database with the updated schema
    }
}