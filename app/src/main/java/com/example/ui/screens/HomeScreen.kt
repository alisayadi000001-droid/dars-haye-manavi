package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SpiritualCategory
import com.example.ui.components.AmbientMiniPlayer
import com.example.ui.components.DailyWisdomCard
import com.example.ui.components.FrostedGlassMeshBackdrop
import com.example.ui.components.LessonCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassMeshIndigo
import com.example.ui.theme.GlassMeshPink
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.GlassPrimaryDark
import com.example.ui.theme.GlassSecondary
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GlassSurfaceHighlight
import com.example.ui.theme.GlassTertiary
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.LotusGold
import com.example.ui.viewmodel.NavigationScreen
import com.example.ui.viewmodel.SpiritualViewModel

@Composable
fun HomeScreen(
    viewModel: SpiritualViewModel,
    modifier: Modifier = Modifier
) {
    val lessons by viewModel.allLessons.collectAsState()
    val dailyWisdomList by viewModel.dailyWisdomList.collectAsState()
    val dailyWisdomIndex by viewModel.dailyWisdomIndex.collectAsState()
    val isPlayingAmbient by viewModel.isPlayingAmbient.collectAsState()
    val selectedSoundId by viewModel.selectedSoundId.collectAsState()
    val ambientVolume by viewModel.ambientVolume.collectAsState()

    val completedCount = lessons.count { it.isCompleted }
    val favoriteCount = lessons.count { it.isFavorite }
    val currentWisdom = dailyWisdomList.getOrNull(dailyWisdomIndex)

    val nextLesson = lessons.firstOrNull { !it.isCompleted } ?: lessons.firstOrNull()

    FrostedGlassMeshBackdrop(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_screen"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 95.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Welcome Header with Glass Avatar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "دروس معنوی",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                        Text(
                            text = "سفر آگاهی، حضور و آرامش درونی",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlassTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(GlassSurface)
                            .border(1.dp, GlassBorder, CircleShape)
                            .clickable { viewModel.navigateTo(NavigationScreen.JOURNAL) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = GlassPrimaryDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Featured Hero Card (درس امروز / Featured Lesson)
            if (nextLesson != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
                            .padding(22.dp)
                            .testTag("hero_lesson_card")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GlassPrimary.copy(alpha = 0.2f))
                                        .border(1.dp, GlassPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "آموزه امروز",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassPrimaryDark
                                    )
                                }

                                Text(
                                    text = "${nextLesson.durationMinutes} دقیقه",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlassTextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = nextLesson.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = GlassTextPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = nextLesson.subtitle,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = GlassTextSecondary
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = { viewModel.openLesson(nextLesson.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("start_learning_btn"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlassPrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "شروع یادگیری و حضور",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Grid Cards (مدیتیشن / کتابخانه درس‌ها)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Meditation Grid Glass Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                            .clickable { viewModel.navigateTo(NavigationScreen.MEDITATION) }
                            .padding(18.dp)
                            .testTag("grid_meditation_card"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GlassSecondary.copy(alpha = 0.2f))
                                    .border(1.dp, GlassSecondary.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SelfImprovement,
                                    contentDescription = "Meditation",
                                    tint = GlassSecondary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "مدیتیشن",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GlassTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "۵ طنین آرامش",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlassTextMuted
                            )
                        }
                    }

                    // Library Grid Glass Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                            .clickable { viewModel.navigateTo(NavigationScreen.LESSONS) }
                            .padding(18.dp)
                            .testTag("grid_library_card"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GlassTertiary.copy(alpha = 0.2f))
                                    .border(1.dp, GlassTertiary.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = "Lessons Library",
                                    tint = GlassTertiary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "کتابخانه",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GlassTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${lessons.size} درس حکمت",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlassTextMuted
                            )
                        }
                    }
                }
            }

            // Quick Stats Row (Glass)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(GlassPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GlassPrimaryDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "$completedCount از ${lessons.size}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GlassTextPrimary
                                )
                                Text(
                                    text = "درس طی‌شده",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlassTextSecondary
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(LotusGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = LotusGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "$favoriteCount مورد",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GlassTextPrimary
                                )
                                Text(
                                    text = "نشان‌شده‌ها",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlassTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Daily Wisdom Glass Card
            if (currentWisdom != null) {
                item {
                    DailyWisdomCard(
                        wisdom = currentWisdom,
                        onNextWisdom = { viewModel.nextDailyWisdom() }
                    )
                }
            }

            // Ambient Sound Wave Player
            item {
                val currentSound = viewModel.meditationSounds.find { it.id == selectedSoundId }
                AmbientMiniPlayer(
                    isPlaying = isPlayingAmbient,
                    soundTitle = currentSound?.persianTitle ?: "فرکانس آرامش ۴۳۲ هرتز",
                    volume = ambientVolume,
                    onTogglePlay = { viewModel.toggleAmbientSound() },
                    onVolumeChange = { viewModel.setAmbientVolume(it) }
                )
            }

            // Categories Explorer
            item {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = "موضوعات و ساحت‌های آگاهی",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GlassTextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(SpiritualCategory.entries.filter { it != SpiritualCategory.ALL }) { cat ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GlassSurface)
                                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.setCategory(cat)
                                        viewModel.navigateTo(NavigationScreen.LESSONS)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = cat.persianName,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = cat.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GlassTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
