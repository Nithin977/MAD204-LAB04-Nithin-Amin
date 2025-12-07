package com.example.lab4notesreminderapp.ui

/**
 * RecyclerView adapter for displaying notes.
 * Click = edit, long click = delete.
 */

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab4notesreminderapp.R
import com.example.lab4notesreminderapp.data.Note

class NoteAdapter(
    private var notes: MutableList<Note>,
    private val onDelete: (Note) -> Unit,
    private val onUpdate: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvContent: TextView = view.findViewById(R.id.tvContent)

        init {
            view.setOnLongClickListener {
                onDelete(notes[adapterPosition])
                true
            }
            view.setOnClickListener {
                onUpdate(notes[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.tvTitle.text = note.title
        holder.tvContent.text = note.content
    }

    override fun getItemCount() = notes.size

    fun setNotes(newNotes: List<Note>) {
        notes = newNotes.toMutableList()
        notifyDataSetChanged()
    }
}

