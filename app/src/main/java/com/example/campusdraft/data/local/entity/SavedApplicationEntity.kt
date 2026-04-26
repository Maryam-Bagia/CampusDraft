package com.example.campusdraft.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted record of a generated application stored in Room.
 * Saved whenever the user generates a PDF or copies text.
 */
@Entity(tableName = "saved_applications")
data class SavedApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** ID matching ApplicationType.id */
    val applicationTypeId: String,

    /** Human-readable title of the application type */
    val applicationTitle: String,

    /** JSON-serialised Map<String, String> of field values */
    val fieldValuesJson: String,

    /** The final rendered text that was generated */
    val renderedText: String,

    /** Unix timestamp millis of when this was saved */
    val savedAt: Long = System.currentTimeMillis(),

    /** Whether the user has starred this application */
    val isFavorite: Boolean = false
)