package com.example.campusdraft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusdraft.data.local.entity.SavedApplicationEntity
import com.example.campusdraft.data.repository.ApplicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val applications: List<SavedApplicationEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ApplicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllApplications().collect { list ->
                _uiState.update { it.copy(applications = list, isLoading = false) }
            }
        }
    }

    fun toggleFavorite(entity: SavedApplicationEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(entity.id, entity.isFavorite)
        }
    }

    fun delete(entity: SavedApplicationEntity) {
        viewModelScope.launch {
            repository.deleteApplication(entity)
        }
    }
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: ApplicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getFavoriteApplications().collect { list ->
                _uiState.update { it.copy(applications = list, isLoading = false) }
            }
        }
    }

    fun toggleFavorite(entity: SavedApplicationEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(entity.id, entity.isFavorite)
        }
    }

    fun delete(entity: SavedApplicationEntity) {
        viewModelScope.launch {
            repository.deleteApplication(entity)
        }
    }
}