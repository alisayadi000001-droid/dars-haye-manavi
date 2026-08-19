package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.SpiritualCategory
import com.example.ui.components.FrostedGlassMeshBackdrop
import com.example.ui.components.LessonCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.GlassPrimaryDark
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GlassSurfaceHighlight
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.LotusGold
import com.example.ui.viewmodel.NavigationScreen
import com.example.ui.viewmodel.SpiritualViewModel

@Composable
fun LessonsListScreen(
    viewModel: SpiritualViewModel,
    modifier: Modifier = Modifier
) {
    val lessons by viewModel.filteredLessons.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val onlyFavorites by viewModel.onlyFavorites.collectAsState()

    FrostedGlassMeshBackdrop(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("lessons_list_screen")
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Glass Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "جستجو در درس‌ها و مفاهیم معنوی...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GlassTextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = GlassPrimaryDark
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = GlassTextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GlassPrimary,
                    unfocusedBorderColor = GlassBorder,
                    focusedContainerColor = GlassSurface,
                    unfocusedContainerColor = GlassSurface,
                    focusedTextColor = GlassTextPrimary,
                    unfocusedTextColor = GlassTextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_lessons_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Glass Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                // Favorite Toggle Chip
                item {
                    FilterChip(
                        selected = onlyFavorites,
                        onClick = { viewModel.toggleFavoritesFilter() },
                        label = {
                            Text(
                                "نشان‌شده‌ها",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (onlyFavorites) LotusGold else GlassTextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = if (onlyFavorites) LotusGold else GlassTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = GlassSurface,
                            selectedContainerColor = LotusGold.copy(alpha = 0.2f),
                            selectedLabelColor = LotusGold
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = onlyFavorites,
                            borderColor = if (onlyFavorites) LotusGold.copy(alpha = 0.5f) else GlassBorder
                        ),
                        modifier = Modifier.testTag("filter_fav_chip")
                    )
                }

                items(SpiritualCategory.entries) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.setCategory(category) },
                        label = {
                            Text(
                                category.persianName,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedCategory == category) GlassTextPrimary else GlassTextSecondary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = GlassSurface,
                            selectedContainerColor = GlassPrimary.copy(alpha = 0.35f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory == category,
                            borderColor = if (selectedCategory == category) GlassPrimary else GlassBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Summary Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "فهرست آموزه‌ها (${lessons.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GlassTextPrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurfaceHighlight)
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(NavigationScreen.IMPORT_LESSONS) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("add_lesson_header_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = GlassPrimaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "افزودن درس جدید",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = GlassPrimaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lessons List
            if (lessons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GlassSurface)
                                .border(1.dp, GlassBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = GlassTextSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "درسی با این مشخصات یافت نشد",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "می‌توانید فیلترها را تغییر دهید یا درس‌های جدید خود را اضافه کنید.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlassTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(lessons, key = { it.id }) { lesson ->
                        LessonCard(
                            lesson = lesson,
                            onClick = { viewModel.openLesson(lesson.id) },
                            onToggleFavorite = { viewModel.toggleLessonFavorite(lesson.id) },
                            onToggleCompleted = { viewModel.toggleLessonCompleted(lesson.id) }
                        )
                    }
                }
            }
        }
    }
}
