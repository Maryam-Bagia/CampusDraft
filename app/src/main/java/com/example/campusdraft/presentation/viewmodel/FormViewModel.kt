package com.example.campusdraft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusdraft.data.repository.ApplicationRepository
import com.example.campusdraft.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class FormUiState(
    val applicationType: ApplicationType? = null,
    val fieldValues: Map<FormFieldType, String> = emptyMap(),
    val previewText: String = "",
    val isSaving: Boolean = false,
    val savedId: Long? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class FormViewModel @Inject constructor(
    private val repository: ApplicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    /** Call once when the form screen is opened */
    fun loadApplicationType(id: String) {
        val type = ApplicationTypes.all.find { it.id == id } ?: return
        // Pre-fill the date field with today
        val today = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        val initialValues = mapOf(FormFieldType.DATE to today)
        val formState = FormState(type, initialValues)
        _uiState.update {
            it.copy(
                applicationType = type,
                fieldValues = initialValues,
                previewText = formState.renderText()
            )
        }
    }

    /** Called whenever a field value changes */
    fun onFieldChanged(field: FormFieldType, value: String) {
        _uiState.update { state ->
            val newValues = state.fieldValues.toMutableMap().apply { put(field, value) }
            val appType   = state.applicationType ?: return@update state
            val preview   = FormState(appType, newValues).renderText()
            state.copy(fieldValues = newValues, previewText = preview)
        }
    }

    /** Validate that all non-optional required fields are filled */
    fun validateAndSave(): Boolean {
        val state = _uiState.value
        val type  = state.applicationType ?: return false

        val missing = type.requiredFields.filter { field ->
            !field.isOptional && state.fieldValues[field].isNullOrBlank()
        }

        if (missing.isNotEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Please fill: ${missing.joinToString(", ") { f -> f.label }}")
            }
            return false
        }

        _uiState.update { it.copy(errorMessage = null) }
        saveToDatabase()
        return true
    }

    private fun saveToDatabase() {
        val state = _uiState.value
        val type  = state.applicationType ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val fieldMap = state.fieldValues.entries.associate { (k, v) -> k.name to v }
            val savedId  = repository.saveApplication(
                applicationTypeId = type.id,
                applicationTitle  = type.title,
                fieldValues       = fieldMap,
                renderedText      = state.previewText
            )
            _uiState.update { it.copy(isSaving = false, savedId = savedId) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}