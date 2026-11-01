package ru.kharlov.lab3

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class MainActivity : ComponentActivity() {
    private var feedback: Toast? = null
    companion object { const val NOTE_KEY = "saved_note" }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs = getSharedPreferences("NotesPrefs", MODE_PRIVATE)
        val input = findViewById<EditText>(R.id.note)
        val file = File(filesDir, "my_note.txt")
        fun text(): String? {
            val value = input.text.toString()
            if (value.isBlank()) { show("Введите текст заметки"); return null }
            return value
        }
        findViewById<Button>(R.id.save).setOnClickListener {
            val value = text() ?: return@setOnClickListener
            prefs.edit().putString(NOTE_KEY, value).apply()
            show("Сохранено в SharedPreferences")
        }
        findViewById<Button>(R.id.load).setOnClickListener {
            val saved = prefs.getString(NOTE_KEY, null)
            show(if (saved.isNullOrEmpty()) "Нет сохранённой заметки (SP)" else saved)
        }
        findViewById<Button>(R.id.clear).setOnClickListener {
            prefs.edit().remove(NOTE_KEY).apply()
            input.text.clear()
            show("Данные (SP) очищены")
        }
        findViewById<Button>(R.id.saveFile).setOnClickListener {
            val value = text() ?: return@setOnClickListener
            lifecycleScope.launch {
                val message = withContext(Dispatchers.IO) {
                    try { file.writeText(value, Charsets.UTF_8); "Сохранено в файл my_note.txt" }
                    catch (e: IOException) { "Не удалось записать файл" }
                }
                show(message)
            }
        }
        findViewById<Button>(R.id.loadFile).setOnClickListener {
            lifecycleScope.launch {
                val message = withContext(Dispatchers.IO) {
                    try { if (file.exists()) file.readText(Charsets.UTF_8) else "Файл не найден" }
                    catch (e: IOException) { "Не удалось прочитать файл" }
                }
                show(message)
            }
        }
        findViewById<Button>(R.id.list).setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
        }
    }
    private fun show(message: String) {
        findViewById<TextView>(R.id.result).text = message
        feedback?.cancel()
        feedback = Toast.makeText(this, message, Toast.LENGTH_SHORT).also { it.show() }
    }
    override fun onStop() {
        feedback?.cancel()
        super.onStop()
    }

}
