/* Repository would be responsible for interacting with the SQLite database */

package com.project.notepad

import android.content.ContentValues
import android.database.Cursor
import com.project.notepad.DbHelper.Companion.ID_COL
import com.project.notepad.DbHelper.Companion.NOTE_DESCRIPTION
import com.project.notepad.DbHelper.Companion.NOTE_NAME
import com.project.notepad.DbHelper.Companion.TABLE_NAME

class NoteRepository(private val dbHelper: DbHelper) {

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val cursor: Cursor = dbHelper.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                notes.add(
                    Note(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    )
                )
            } while (cursor.moveToNext())
            // moving our cursor to next.
        }
        // at last closing our cursor and returning our array list.
        cursor.close()
        return notes
    }

    fun addNewNote(note: Note) {
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(NOTE_NAME, note.noteName)
            put(NOTE_DESCRIPTION, note.noteDescription)
        }

        // Insert the new row, returning the primary key value of the new row
        dbHelper.writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun removeNoteById(noteId: Int) {
        val selection = "$ID_COL LIKE ?"
        val selectionArgs = arrayOf(noteId.toString());
        dbHelper.writableDatabase.delete(
            TABLE_NAME,
            selection,
            selectionArgs)
    }

    fun updateNote(note: Note) {
        val selection = "$ID_COL LIKE ?"
        val selectionArgs = arrayOf(note.id.toString())
        val values = ContentValues().apply {
            put(NOTE_NAME, note.noteName)
            put(NOTE_DESCRIPTION, note.noteDescription)
        }
        dbHelper.writableDatabase.update(
            TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
    }
}