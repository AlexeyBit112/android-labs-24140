package ru.kharlov.lab3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(private val notes: MutableList<String>, private val onDelete: (Int) -> Unit) : RecyclerView.Adapter<NotesAdapter.Holder>() {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.text)
        val delete: Button = view.findViewById(R.id.delete)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false))
    override fun getItemCount() = notes.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.text.text = notes[position]
        holder.delete.setOnClickListener {
            val current = holder.bindingAdapterPosition
            if (current != RecyclerView.NO_POSITION) onDelete(current)
        }
    }
    fun removeNote(position: Int) {
        if (position !in notes.indices) return
        notes.removeAt(position)
        notifyItemRemoved(position)
    }
}
