package com.example.campusdraft.presentation.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusdraft.data.local.entity.SavedApplicationEntity
import com.example.campusdraft.data.repository.ApplicationRepository
import com.example.campusdraft.domain.usecase.PdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class PreviewUiState(
    val entity: SavedApplicationEntity? = null,
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val toastMessage: String? = null,
    val generatedPdfFile: File? = null
)

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val repository: ApplicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    fun load(savedId: Long) {
        viewModelScope.launch {
            val entity = repository.getById(savedId)
            _uiState.update {
                it.copy(entity = entity, isLoading = false, isFavorite = entity?.isFavorite ?: false)
            }
        }
    }

    fun toggleFavorite() {
        val entity = _uiState.value.entity ?: return
        viewModelScope.launch {
            repository.toggleFavorite(entity.id, entity.isFavorite)
            _uiState.update { it.copy(isFavorite = !it.isFavorite) }
        }
    }

    fun generatePdf(context: Context) {
        val entity = _uiState.value.entity ?: return
        viewModelScope.launch {
            val file = PdfGenerator.generatePdf(context, entity.applicationTitle, entity.renderedText)
            if (file != null) {
                _uiState.update { it.copy(generatedPdfFile = file, toastMessage = "PDF saved!") }
            } else {
                _uiState.update { it.copy(toastMessage = "Failed to generate PDF") }
            }
        }
    }

    fun sharePdf(context: Context) {
        val file = _uiState.value.generatedPdfFile
        val entity = _uiState.value.entity ?: return
        if (file == null) {
            generatePdf(context)
            return
        }
        val uri = PdfGenerator.getShareUri(context, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, entity.applicationTitle)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share PDF via"))
    }

    fun shareAsText(context: Context) {
        val entity = _uiState.value.entity ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, entity.renderedText)
            putExtra(Intent.EXTRA_SUBJECT, entity.applicationTitle)
        }
        context.startActivity(Intent.createChooser(intent, "Share text via"))
    }

    fun copyToClipboard(context: Context) {
        val entity = _uiState.value.entity ?: return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(entity.applicationTitle, entity.renderedText))
        _uiState.update { it.copy(toastMessage = "Copied to clipboard!") }
    }

    fun printDocument(context: Context) {
        val entity = _uiState.value.entity ?: return
        PdfGenerator.printDocument(context, entity.applicationTitle, entity.renderedText)
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}