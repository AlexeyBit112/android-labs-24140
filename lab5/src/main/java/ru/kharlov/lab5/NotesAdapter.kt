package ru.kharlov.lab5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.Date

class NotesAdapter(private val onSelect: (Note) -> Unit) : ListAdapter<Note, NotesAdapter.Holder>(Diff) {
    object Diff : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(old: Note, new: Note) = old.id == new.id
        override fun areContentsTheSame(old: Note, new: Note) = old == new
    }
    class Holder(view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false))
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val note = getItem(position)
        holder.itemView.findViewById<TextView>(R.id.noteTitle).text = note.title
        holder.itemView.findViewById<TextView>(R.id.noteContent).text = note.content
        holder.itemView.findViewById<TextView>(R.id.date).text = "№${note.id} · ${DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(note.timestamp))}"
        holder.itemView.contentDescription = "Заметка: ${note.title}"
        holder.itemView.setOnClickListener { onSelect(note) }
    }
}
