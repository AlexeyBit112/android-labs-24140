package ru.kharlov.lab4

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import android.database.sqlite.SQLiteException
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private var feedback: Toast? = null
    private lateinit var adapter: NotesAdapter
    private var selectedId: Long? = null
    private var busy = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectedId = savedInstanceState?.getLong("selectedId", -1)?.takeIf { it >= 0 }
        val title = findViewById<EditText>(R.id.title)
        val content = findViewById<EditText>(R.id.content)
        adapter = NotesAdapter { note ->
            if (!busy) {
                selectedId = note.id
                title.setText(note.title)
                content.setText(note.content)
                findViewById<TextView>(R.id.status).text = "Выбрана заметка №${note.id}"
            }
        }
        findViewById<RecyclerView>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
        fun save(update: Boolean) {
            val heading = title.text.toString().trim()
            val text = content.text.toString()
            val id = selectedId
            if (update && id == null) { show("Сначала выберите заметку"); return }
            if (heading.isEmpty()) { show("Введите заголовок"); return }
            operation { db ->
                if (update) {
                    if (db.updateNote(id!!, heading, text) == 1) "Заметка обновлена" else "Заметка не найдена"
                } else {
                    if (db.insertNote(heading, text) != -1L) "Заметка добавлена" else "Не удалось добавить заметку"
                }
            }
        }
        findViewById<Button>(R.id.add).setOnClickListener { save(false) }
        findViewById<Button>(R.id.update).setOnClickListener { save(true) }
        findViewById<Button>(R.id.delete).setOnClickListener {
            val id = selectedId
            if (id == null) show("Сначала выберите заметку")
            else operation { db -> if (db.deleteNote(id) == 1) "Заметка удалена" else "Заметка не найдена" }
        }
        operation(clearForm = false) { "Заметки загружены" }
    }
    private fun operation(clearForm: Boolean = true, action: (NotesDbHelper) -> String) {
        if (busy) return
        busy = true
        controls(false)
        lifecycleScope.launch {
            try {
                val (message, notes) = withContext(Dispatchers.IO) {
                    // Отдельный helper на операцию закрывается в том же потоке.
                    NotesDbHelper(applicationContext).use { db -> action(db) to db.getAllNotes() }
                }
                adapter.submitList(notes)
                if (clearForm) {
                    selectedId = null
                    findViewById<EditText>(R.id.title).text.clear()
                    findViewById<EditText>(R.id.content).text.clear()
                }
                show(if (notes.isEmpty()) "$message. Список пуст" else "$message. Всего: ${notes.size}")
            } catch (e: SQLiteException) {
                show("Не удалось выполнить операцию с базой данных")
            } finally { busy = false; controls(true) }
        }
    }
    private fun controls(enabled: Boolean) {
        listOf(R.id.add, R.id.update, R.id.delete, R.id.title, R.id.content).forEach { findViewById<android.view.View>(it).isEnabled = enabled }
    }
    private fun show(message: String) {
        findViewById<TextView>(R.id.status).text = message
        feedback?.cancel()
        feedback = Toast.makeText(this, message, Toast.LENGTH_SHORT).also { it.show() }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        selectedId?.let { outState.putLong("selectedId", it) }
        super.onSaveInstanceState(outState)
    }
    override fun onStop() {
        feedback?.cancel()
        super.onStop()
    }

}
