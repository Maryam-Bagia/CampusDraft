package com.example.campusdraft.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.campusdraft.domain.model.AppCategory
import com.example.campusdraft.presentation.components.*
import com.example.campusdraft.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onApplicationTypeSelected: (String) -> Unit,
    onHistoryClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            HomeTopBar(
                onHistoryClick  = onHistoryClick,
                onFavoritesClick = onFavoritesClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // ── Hero / Stats banner ─────────────────────────────────────────
            item {
                HeroBanner(
                    totalSaved = uiState.totalSaved,
                    recentCount = uiState.recentApplications.size
                )
            }

            // ── Search bar ──────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(8.dp))
                SearchBarSection(
                    query = uiState.searchQuery,
                    onQueryChanged = viewModel::onSearchQueryChanged,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            // ── Category filter chips ───────────────────────────────────────
            item {
                CategoryFilterRow(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = viewModel::onCategorySelected
                )
                Spacer(Modifier.height(16.dp))
            }

            // ── Recent applications strip ───────────────────────────────────
            if (uiState.recentApplications.isNotEmpty() && uiState.searchQuery.isBlank()) {
                item {
                    SectionHeader("Recent Applications")
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.recentApplications.take(5)) { saved ->
                            RecentChip(
                                title = saved.applicationTitle,
                                onClick = { onHistoryClick() }
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }

            // ── Application list section header ─────────────────────────────
            item {
                val header = if (uiState.selectedCategory != null)
                    "${uiState.selectedCategory!!.displayName} Applications"
                else "All Application Types"
                SectionHeader(
                    "${header}  (${uiState.filteredTypes.size})"
                )
                Spacer(Modifier.height(8.dp))
            }

            // ── Application cards ───────────────────────────────────────────
            if (uiState.filteredTypes.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = "No results found",
                        subtitle = "Try a different search term or category",
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            } else {
                items(
                    items = uiState.filteredTypes,
                    key   = { it.id }
                ) { appType ->
                    ApplicationCard(
                        applicationType = appType,
                        onClick = { onApplicationTypeSelected(appType.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    onHistoryClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    "Campus",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Draft",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        },
        actions = {
            IconButton(onClick = onFavoritesClick) {
                Icon(Icons.Outlined.FavoriteBorder, "Favorites",
                    tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onHistoryClick) {
                Icon(Icons.Outlined.History, "History",
                    tint = MaterialTheme.colorScheme.primary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun HeroBanner(totalSaved: Int, recentCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Column {
            // Greeting text
            Text(
                text = "Write Professional",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Applications in Seconds ✍️",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Choose a template, fill details, generate PDF",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            // Stats row
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip(
                    label = "Templates",
                    value = "12",
                    color = MaterialTheme.colorScheme.primary
                )
                StatChip(
                    label = "Generated",
                    value = totalSaved.toString(),
                    color = Color(0xFF4CAF50)
                )
                StatChip(
                    label = "Recent",
                    value = recentCount.toString(),
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarSection(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        placeholder = {
            Text("Search applications…",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        leadingIcon = {
            Icon(Icons.Outlined.Search, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary)
        },
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(Icons.Filled.Clear, "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun CategoryFilterRow(
    selectedCategory: AppCategory?,
    onCategorySelected: (AppCategory?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        item {
            CategoryChip(
                label = "All",
                selected = selectedCategory == null,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onCategorySelected(null) }
            )
        }
        items(AppCategory.entries) { category ->
            CategoryChip(
                label = category.displayName,
                selected = selectedCategory == category,
                color = category.color(),
                onClick = {
                    onCategorySelected(if (selectedCategory == category) null else category)
                }
            )
        }
    }
}

@Composable
private fun RecentChip(title: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1
            )
        }
    }
}