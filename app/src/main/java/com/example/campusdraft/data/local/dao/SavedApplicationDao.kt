package com.example.campusdraft.data.local.dao

import androidx.room.*
import com.example.campusdraft.data.local.entity.SavedApplicationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for all saved-application operations.
 */
@Dao
interface SavedApplicationDao {

    /** Stream all applications ordered by newest first */
    @Query("SELECT * FROM saved_applications ORDER BY savedAt DESC")
    fun getAllApplications(): Flow<List<SavedApplicationEntity>>

    /** Stream only favorited applications */
    @Query("SELECT * FROM saved_applications WHERE isFavorite = 1 ORDER BY savedAt DESC")
    fun getFavoriteApplications(): Flow<List<SavedApplicationEntity>>

    /** Stream the N most-recently saved applications */
    @Query("SELECT * FROM saved_applications ORDER BY savedAt DESC LIMIT :limit")
    fun getRecentApplications(limit: Int = 10): Flow<List<SavedApplicationEntity>>

    /** Fetch a single application by its primary key */
    @Query("SELECT * FROM saved_applications WHERE id = :id")
    suspend fun getApplicationById(id: Long): SavedApplicationEntity?

    /** Insert or replace an application record */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(entity: SavedApplicationEntity): Long

    /** Update an existing record (used for toggling favorite) */
    @Update
    suspend fun updateApplication(entity: SavedApplicationEntity)

    /** Toggle the favorite flag for a given id */
    @Query("UPDATE saved_applications SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    /** Delete a single saved application */
    @Delete
    suspend fun deleteApplication(entity: SavedApplicationEntity)

    /** Delete all saved applications */
    @Query("DELETE FROM saved_applications")
    suspend fun deleteAll()
}