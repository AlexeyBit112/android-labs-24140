package ru.kharlov.lab5

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    private fun LiveData<List<Note>>.awaitNotes(predicate: (List<Note>) -> Boolean): List<Note> {
        val ready = CountDownLatch(1)
        var result = emptyList<Note>()
        val observer = Observer<List<Note>> { notes ->
            if (notes != null && predicate(notes)) {
                result = notes
                ready.countDown()
            }
        }
        instrumentation.runOnMainSync { observeForever(observer) }
        try {
            assertTrue("Room did not publish the expected list", ready.await(10, TimeUnit.SECONDS))
            return result
        } finally {
            instrumentation.runOnMainSync { removeObserver(observer) }
        }
    }

    @Test fun crudPublishesLiveData() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val dao = db.noteDao()
            val notes = dao.getAll()
            assertTrue(notes.awaitNotes { it.isEmpty() }.isEmpty())
            val id = dao.insert(Note(title = "O'Reilly", content = "Текст с 'кавычками'"))
            assertTrue(id > 0)
            val inserted = notes.awaitNotes { it.size == 1 }.single()
            assertEquals(id.toInt(), inserted.id)
            assertEquals("O'Reilly", inserted.title)
            val updated = inserted.copy(content = "Новый текст", timestamp = inserted.timestamp + 1)
            assertEquals(1, dao.update(updated))
            assertEquals("Новый текст", notes.awaitNotes { it.singleOrNull()?.content == "Новый текст" }.single().content)
            assertEquals(0, dao.update(updated.copy(id = -1)))
            assertEquals(0, dao.delete(updated.copy(id = -1)))
            assertEquals(1, dao.delete(updated))
            assertTrue(notes.awaitNotes { it.isEmpty() }.isEmpty())
        } finally {
            db.close()
        }
    }

    @Test fun dataSurvivesDatabaseReopen() = runBlocking {
        val name = "room_persistence_test.db"
        context.deleteDatabase(name)
        try {
            val first = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
            val id: Long
            try {
                id = first.noteDao().insert(Note(title = "После перезапуска", content = "Запись на диске"))
            } finally {
                first.close()
            }
            val second = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
            try {
                val note = second.noteDao().getAll().awaitNotes { it.size == 1 }.single()
                assertEquals(id.toInt(), note.id)
                assertEquals("После перезапуска", note.title)
                assertEquals("Запись на диске", note.content)
            } finally {
                second.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }
}
