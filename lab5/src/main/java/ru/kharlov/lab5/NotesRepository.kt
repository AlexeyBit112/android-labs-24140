package ru.kharlov.lab5

class NotesRepository(private val dao: NoteDao) {
    val allNotes = dao.getAll()

    suspend fun insert(note: Note): Long = dao.insert(note)
    suspend fun update(note: Note): Int = dao.update(note)
    suspend fun delete(note: Note): Int = dao.delete(note)
}
