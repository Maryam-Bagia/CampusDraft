package com.example.campusdraft.presentation.screens.form

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.campusdraft.domain.model.FormFieldType
import com.example.campusdraft.presentation.components.color
import com.example.campusdraft.presentation.viewmodel.FormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    applicationTypeId: String,
    onBack: () -> Unit,
    onPreview: (Long) -> Unit,
    viewModel: FormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPreviewPanel by remember { mutableStateOf(false) }

    // Load the application type on first composition
    LaunchedEffect(applicationTypeId) {
        viewModel.loadApplicationType(applicationTypeId)
    }

    // Navigate to preview when savedId is available
    LaunchedEffect(uiState.savedId) {
        uiState.savedId?.let { onPreview(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                title = {
                    Column {
                        Text(
                            text = uiState.applicationType?.title ?: "Fill Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        uiState.applicationType?.category?.let { cat ->
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = cat.color()
                            )
                        }
                    }
                },
                actions = {
                    // Toggle preview panel
                    IconButton(onClick = { showPreviewPanel = !showPreviewPanel }) {
                        Icon(
                            if (showPreviewPanel) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle Preview",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            FormBottomBar(
                onGenerateClick = {
                    viewModel.validateAndSave()
                    // navigation is handled by LaunchedEffect above
                },
                isLoading = uiState.isSaving
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Error snackbar
            AnimatedVisibility(visible = uiState.errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, null, tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = viewModel::clearError, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Filled.Close, null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            if (showPreviewPanel) {
                // Preview panel
                PreviewPanel(
                    previewText = uiState.previewText,
                    modifier = Modifier.weight(0.45f)
                )
                HorizontalDivider()
            }

            // Form fields
            val appType = uiState.applicationType
            if (appType != null) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Instruction card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Fill in the fields below. The application will be auto-generated for you.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Render each required field
                    appType.requiredFields.forEach { field ->
                        FormField(
                            field = field,
                            value = uiState.fieldValues[field] ?: "",
                            onValueChange = { viewModel.onFieldChanged(field, it) }
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun FormField(
    field: FormFieldType,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        // Label with optional badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = field.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (field.isOptional) {
                Spacer(Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        "optional",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    field.placeholder,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            singleLine = !field.isMultiLine,
            minLines = if (field.isMultiLine) 3 else 1,
            maxLines = if (field.isMultiLine) 5 else 1,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = when (field) {
                    FormFieldType.ENROLLMENT_NUMBER -> KeyboardType.Text
                    FormFieldType.LEAVE_DAYS, FormFieldType.EXTENSION_DAYS -> KeyboardType.Number
                    else -> KeyboardType.Text
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PreviewPanel(previewText: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Preview, null, tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                "Live Preview",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = previewText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun FormBottomBar(onGenerateClick: () -> Unit, isLoading: Boolean) {
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onGenerateClick,
                enabled = !isLoading,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Filled.AutoAwesome, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Generate Application", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}