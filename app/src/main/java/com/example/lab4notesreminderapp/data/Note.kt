
/**
 * F2025 MAD204 - Lab 4
 * Note entity used by Room database.
 */

package com.example.lab4notesreminderapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String
)
