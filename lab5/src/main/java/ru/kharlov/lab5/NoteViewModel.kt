package ru.kharlov.lab5

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotesRepository(AppDatabase.getDatabase(application).noteDao())
    val allNotes = repository.allNotes

    private val selected = MutableLiveData<Note?>(null)
    val selectedNote: LiveData<Note?> = selected
    private val working = MutableLiveData(false)
    val busy: LiveData<Boolean> = working
    private val notice = MutableLiveData<String?>(null)
    val message: LiveData<String?> = notice

    fun select(note: Note) {
        if (working.value == true) return
        selected.value = note
        notice.value = "Выбрана заметка №${note.id}"
    }

    fun insert(title: String, content: String) {
        val heading = title.trim()
        if (heading.isEmpty()) { notice.value = "Введите заголовок"; return }
        change {
            val id = repository.insert(Note(title = heading, content = content))
            if (id > 0) "Заметка добавлена" else "Не удалось добавить заметку"
        }
    }

    fun update(title: String, content: String) {
        val note = selected.value
        if (note == null) { notice.value = "Сначала выберите заметку"; return }
        val heading = title.trim()
        if (heading.isEmpty()) { notice.value = "Введите заголовок"; return }
        change {
            val changed = note.copy(title = heading, content = content, timestamp = System.currentTimeMillis())
            if (repository.update(changed) == 1) "Заметка обновлена" else "Заметка не найдена"
        }
    }

    fun delete() {
        val note = selected.value
        if (note == null) { notice.value = "Сначала выберите заметку"; return }
        change {
            if (repository.delete(note) == 1) "Заметка удалена" else "Заметка не найдена"
        }
    }

    private fun change(action: suspend () -> String) {
        if (working.value == true) return
        viewModelScope.launch {
            working.value = true
            try {
                val text = action()
                selected.value = null
                notice.value = text
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("RoomNotes", "Database operation failed", e)
                notice.value = "Не удалось выполнить операцию с базой данных"
            } finally {
                working.value = false
            }
        }
    }

    fun messageShown() { notice.value = null }
}
