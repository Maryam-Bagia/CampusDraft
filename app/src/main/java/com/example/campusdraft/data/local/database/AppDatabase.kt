package com.example.campusdraft.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.campusdraft.data.local.dao.SavedApplicationDao
import com.example.campusdraft.data.local.entity.SavedApplicationEntity

/**
 * Main Room database. Single instance managed by Hilt.
 * Increment version and add migration when schema changes.
 */
@Database(
    entities = [SavedApplicationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedApplicationDao(): SavedApplicationDao

    companion object {
        const val DATABASE_NAME = "smart_app_writer.db"
    }
}