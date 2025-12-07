

/**
 * Main screen: add, view, update, and delete notes using Room + RecyclerView.
 */
package com.example.lab4notesreminderapp.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab4notesreminderapp.R
import com.example.lab4notesreminderapp.data.Note
import com.example.lab4notesreminderapp.data.NotesDatabase
import com.example.lab4notesreminderapp.service.ReminderService
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnReminder: Button
    private lateinit var rvNotes: androidx.recyclerview.widget.RecyclerView

    private lateinit var adapter: NoteAdapter
    private val notes = mutableListOf<Note>()
    private val dao by lazy { NotesDatabase.getInstance(this).noteDao() }

    private val NOTIFICATION_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestNotificationPermission()   // ✅ IMPORTANT

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnAdd = findViewById(R.id.btnAdd)
        btnReminder = findViewById(R.id.btnReminder)
        rvNotes = findViewById(R.id.rvNotes)

        adapter = NoteAdapter(notes,
            onDelete = { deleteWithUndo(it) },
            onUpdate = { showUpdateDialog(it) }
        )

        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        btnAdd.setOnClickListener { addNote() }

        btnReminder.setOnClickListener {
            startService(Intent(this, ReminderService::class.java))
        }

        loadNotes()
    }

    // ✅ Ask permission for Android 13+
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            val data = dao.getAllNotes()
            notes.clear()
            notes.addAll(data)
            adapter.setNotes(notes)
        }
    }

    private fun addNote() {
        val title = etTitle.text.toString()
        val content = etContent.text.toString()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            dao.insert(Note(title = title, content = content))
            loadNotes()
        }

        etTitle.text.clear()
        etContent.text.clear()
    }

    private fun deleteWithUndo(note: Note) {
        lifecycleScope.launch {
            dao.delete(note)
            loadNotes()

            Snackbar.make(rvNotes, "Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    lifecycleScope.launch {
                        dao.insert(note)
                        loadNotes()
                    }
                }.show()
        }
    }

    private fun showUpdateDialog(note: Note) {
        val view = layoutInflater.inflate(R.layout.dialog_edit_note, null)
        val etT = view.findViewById<EditText>(R.id.etDialogTitle)
        val etC = view.findViewById<EditText>(R.id.etDialogContent)

        etT.setText(note.title)
        etC.setText(note.content)

        AlertDialog.Builder(this)
            .setTitle("Edit Note")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                lifecycleScope.launch {
                    dao.update(note.copy(title = etT.text.toString(), content = etC.text.toString()))
                    loadNotes()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
