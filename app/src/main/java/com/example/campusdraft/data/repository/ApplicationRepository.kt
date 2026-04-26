package com.example.campusdraft.data.repository

import com.example.campusdraft.data.local.dao.SavedApplicationDao
import com.example.campusdraft.data.local.entity.SavedApplicationEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for all persisted application data.
 * Abstracts Room DAO from the ViewModel layer.
 */
@Singleton
class ApplicationRepository @Inject constructor(
    private val dao: SavedApplicationDao
) {

    /** All saved applications as a live stream */
    fun getAllApplications(): Flow<List<SavedApplicationEntity>> =
        dao.getAllApplications()

    /** Favorite applications as a live stream */
    fun getFavoriteApplications(): Flow<List<SavedApplicationEntity>> =
        dao.getFavoriteApplications()

    /** The 10 most-recently saved applications */
    fun getRecentApplications(): Flow<List<SavedApplicationEntity>> =
        dao.getRecentApplications(10)

    /** Save a generated application to the database */
    suspend fun saveApplication(
        applicationTypeId: String,
        applicationTitle: String,
        fieldValues: Map<String, String>,
        renderedText: String
    ): Long {
        val json = JSONObject(fieldValues).toString()
        val entity = SavedApplicationEntity(
            applicationTypeId = applicationTypeId,
            applicationTitle = applicationTitle,
            fieldValuesJson = json,
            renderedText = renderedText
        )
        return dao.insertApplication(entity)
    }

    /** Toggle favorite status */
    suspend fun toggleFavorite(id: Long, currentValue: Boolean) {
        dao.setFavorite(id, !currentValue)
    }

    /** Delete a single saved application */
    suspend fun deleteApplication(entity: SavedApplicationEntity) {
        dao.deleteApplication(entity)
    }

    /** Fetch a single entity by ID */
    suspend fun getById(id: Long): SavedApplicationEntity? =
        dao.getApplicationById(id)

    /** Parse stored JSON back to a Map */
    fun parseFieldValues(json: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val obj = JSONObject(json)
        obj.keys().forEach { key -> map[key] = obj.getString(key) }
        return map
    }
}