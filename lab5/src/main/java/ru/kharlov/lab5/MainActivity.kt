package ru.kharlov.lab5

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()
    private var feedback: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val title = findViewById<EditText>(R.id.title)
        val content = findViewById<EditText>(R.id.content)
        val adapter = NotesAdapter { viewModel.select(it) }
        findViewById<RecyclerView>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = adapter
        }

        viewModel.allNotes.observe(this) { notes ->
            adapter.submitList(notes)
            findViewById<TextView>(R.id.count).text = getString(R.string.count_notes, notes.size)
        }
        viewModel.selectedNote.observe(this) { note ->
            if (note == null) {
                title.text.clear()
                content.text.clear()
            } else {
                findViewById<TextView>(R.id.status).text = "Выбрана заметка №${note.id}"
                title.setText(note.title)
                content.setText(note.content)
            }
        }
        viewModel.busy.observe(this) { busy ->
            listOf(R.id.add, R.id.update, R.id.delete, R.id.title, R.id.content).forEach {
                findViewById<View>(it).isEnabled = !busy
            }
        }
        viewModel.message.observe(this) { text ->
            if (text != null) {
                findViewById<TextView>(R.id.status).text = text
                feedback?.cancel()
                feedback = Toast.makeText(this, text, Toast.LENGTH_SHORT).also { it.show() }
                viewModel.messageShown()
            }
        }
        findViewById<Button>(R.id.add).setOnClickListener {
            viewModel.insert(title.text.toString(), content.text.toString())
        }
        findViewById<Button>(R.id.update).setOnClickListener {
            viewModel.update(title.text.toString(), content.text.toString())
        }
        findViewById<Button>(R.id.delete).setOnClickListener { viewModel.delete() }
    }

    override fun onStop() {
        feedback?.cancel()
        super.onStop()
    }
}
