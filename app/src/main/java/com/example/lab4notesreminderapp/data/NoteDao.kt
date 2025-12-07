package com.example.lab4notesreminderapp.data

/**
 * DAO interface defining CRUD operations for notes.
 */


import androidx.room.*

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note)

    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<Note>

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
