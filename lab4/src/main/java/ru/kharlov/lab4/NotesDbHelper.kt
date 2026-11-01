package ru.kharlov.lab4

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDbHelper(context: Context) : SQLiteOpenHelper(context, "notes.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, timestamp INTEGER NOT NULL)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Учебная стратегия: при изменении версии данные удаляются.
        db.execSQL("DROP TABLE IF EXISTS notes")
        onCreate(db)
    }
    private fun values(title: String, content: String) = ContentValues().apply {
        put("title", title)
        put("content", content)
        put("timestamp", System.currentTimeMillis())
    }
    fun insertNote(title: String, content: String): Long =
        writableDatabase.insert("notes", null, values(title, content))
    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        readableDatabase.rawQuery("SELECT * FROM notes ORDER BY timestamp DESC, id DESC", null).use { cursor ->
            val id = cursor.getColumnIndexOrThrow("id")
            val title = cursor.getColumnIndexOrThrow("title")
            val content = cursor.getColumnIndexOrThrow("content")
            val timestamp = cursor.getColumnIndexOrThrow("timestamp")
            while (cursor.moveToNext()) {
                notes.add(Note(cursor.getLong(id), cursor.getString(title), cursor.getString(content), cursor.getLong(timestamp)))
            }
        }
        return notes
    }
    fun updateNote(id: Long, title: String, content: String): Int =
        writableDatabase.update("notes", values(title, content), "id = ?", arrayOf(id.toString()))
    fun deleteNote(id: Long): Int =
        writableDatabase.delete("notes", "id = ?", arrayOf(id.toString()))
}
