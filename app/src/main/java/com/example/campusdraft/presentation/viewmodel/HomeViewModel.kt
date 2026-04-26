package com.example.campusdraft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusdraft.data.local.entity.SavedApplicationEntity
import com.example.campusdraft.data.repository.ApplicationRepository
import com.example.campusdraft.domain.model.AppCategory
import com.example.campusdraft.domain.model.ApplicationType
import com.example.campusdraft.domain.model.ApplicationTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val allTypes: List<ApplicationType>         = ApplicationTypes.all,
    val filteredTypes: List<ApplicationType>    = ApplicationTypes.all,
    val recentApplications: List<SavedApplicationEntity> = emptyList(),
    val selectedCategory: AppCategory?          = null,
    val searchQuery: String                     = "",
    val totalSaved: Int                         = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ApplicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Observe recent applications from Room
        viewModelScope.launch {
            repository.getRecentApplications().collect { recents ->
                _uiState.update { it.copy(recentApplications = recents) }
            }
        }
        viewModelScope.launch {
            repository.getAllApplications().collect { all ->
                _uiState.update { it.copy(totalSaved = all.size) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filtered = filterApplications(query, state.selectedCategory)
            state.copy(searchQuery = query, filteredTypes = filtered)
        }
    }

    fun onCategorySelected(category: AppCategory?) {
        _uiState.update { state ->
            val filtered = filterApplications(state.searchQuery, category)
            state.copy(selectedCategory = category, filteredTypes = filtered)
        }
    }

    private fun filterApplications(query: String, category: AppCategory?): List<ApplicationType> {
        return ApplicationTypes.all.filter { type ->
            val matchesQuery = query.isBlank() ||
                    type.title.contains(query, ignoreCase = true) ||
                    type.description.contains(query, ignoreCase = true) ||
                    type.category.displayName.contains(query, ignoreCase = true)
            val matchesCategory = category == null || type.category == category
            matchesQuery && matchesCategory
        }
    }
}