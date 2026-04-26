package com.example.campusdraft.presentation.screens.preview

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.campusdraft.presentation.viewmodel.PreviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    savedId: Long,
    onBack: () -> Unit,
    onNewApplication: () -> Unit,
    viewModel: PreviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(savedId) { viewModel.load(savedId) }

    // Show toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                title = {
                    Text(
                        text = uiState.entity?.applicationTitle ?: "Application Preview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    // Favorite toggle
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite) Color(0xFFE91E63)
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            PreviewActionBar(
                onGeneratePdf = { viewModel.generatePdf(context) },
                onSharePdf    = { viewModel.sharePdf(context) },
                onShareText   = { viewModel.shareAsText(context) },
                onCopy        = { viewModel.copyToClipboard(context) },
                onPrint       = { viewModel.printDocument(context) },
                onNewApp      = onNewApplication,
                hasPdf        = uiState.generatedPdfFile != null
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Success banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50).copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CheckCircle, null,
                        tint = Color(0xFF4CAF50), modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "Application Generated! 🎉",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            "Use the actions below to save, share or print.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF388E3C)
                        )
                    }
                }
            }

            // PDF generated indicator
            AnimatedVisibility(visible = uiState.generatedPdfFile != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.PictureAsPdf, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "PDF saved: ${uiState.generatedPdfFile?.name ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Document preview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Document header strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "FORMAL APPLICATION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.5.sp
                        )
                        Icon(Icons.Outlined.Description, null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))

                    // Application body text
                    Text(
                        text = uiState.entity?.renderedText ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PreviewActionBar(
    onGeneratePdf: () -> Unit,
    onSharePdf:    () -> Unit,
    onShareText:   () -> Unit,
    onCopy:        () -> Unit,
    onPrint:       () -> Unit,
    onNewApp:      () -> Unit,
    hasPdf:        Boolean
) {
    Surface(
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Primary row: PDF + Share PDF
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = if (hasPdf) onSharePdf else onGeneratePdf,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        if (hasPdf) Icons.Outlined.Share else Icons.Filled.PictureAsPdf,
                        null, modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (hasPdf) "Share PDF" else "Generate PDF",
                        fontWeight = FontWeight.Bold
                    )
                }
                if (!hasPdf) {
                    // placeholder so row stays balanced
                }
            }

            // Secondary row: Copy, Share Text, Print, New App
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PreviewActionButton(
                    icon  = Icons.Outlined.ContentCopy,
                    label = "Copy",
                    onClick = onCopy,
                    modifier = Modifier.weight(1f)
                )
                PreviewActionButton(
                    icon  = Icons.Outlined.Share,
                    label = "Text",
                    onClick = onShareText,
                    modifier = Modifier.weight(1f)
                )
                PreviewActionButton(
                    icon  = Icons.Outlined.Print,
                    label = "Print",
                    onClick = onPrint,
                    modifier = Modifier.weight(1f)
                )
                PreviewActionButton(
                    icon  = Icons.Outlined.Add,
                    label = "New",
                    onClick = onNewApp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PreviewActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(16.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
        }
    }
}