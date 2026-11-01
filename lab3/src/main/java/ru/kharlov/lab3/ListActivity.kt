package ru.kharlov.lab3

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListActivity : ComponentActivity() {
    private val notes = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        notes.addAll(savedInstanceState?.getStringArrayList("notes") ?: emptyList())
        val recycler = findViewById<RecyclerView>(R.id.recycler)
        lateinit var adapter: NotesAdapter
        fun emptyState() { findViewById<TextView>(R.id.empty).visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE }
        adapter = NotesAdapter(notes) { position -> adapter.removeNote(position); emptyState() }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        emptyState()
        findViewById<Button>(R.id.add).setOnClickListener {
            val input = findViewById<EditText>(R.id.newNote)
            val text = input.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Введите текст заметки", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            notes.add(text)
            adapter.notifyItemInserted(notes.lastIndex)
            recycler.scrollToPosition(notes.lastIndex)
            input.text.clear()
            emptyState()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArrayList("notes", ArrayList(notes))
        super.onSaveInstanceState(outState)
    }
}
