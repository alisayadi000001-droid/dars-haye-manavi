package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyWisdom
import com.example.data.model.SpiritualLesson
import com.example.ui.theme.GlassBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassMeshDeep
import com.example.ui.theme.GlassMeshIndigo
import com.example.ui.theme.GlassMeshPink
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.GlassPrimaryContainer
import com.example.ui.theme.GlassPrimaryDark
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GlassSurfaceHighlight
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.LotusGold
import com.example.ui.theme.SereneCyan
import com.example.ui.viewmodel.ReaderFontSize

@Composable
fun PersianLayout(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        content()
    }
}

/**
 * Background mesh gradient providing the dark atmospheric backdrop for frosted glass elements
 */
@Composable
fun FrostedGlassMeshBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GlassBackground,
                        GlassMeshDeep,
                        GlassMeshIndigo.copy(alpha = 0.5f),
                        GlassBackground
                    )
                )
            )
    ) {
        // Subtle ambient radial glow overlays
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GlassMeshIndigo.copy(alpha = 0.4f),
                            GlassMeshPink.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        radius = 1200f
                    )
                )
        )
        content()
    }
}

@Composable
fun DailyWisdomCard(
    wisdom: DailyWisdom,
    onNextWisdom: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .testTag("daily_wisdom_card")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GlassPrimaryContainer)
                            .border(1.dp, GlassBorderSubtle, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "Wisdom Icon",
                            tint = GlassPrimaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "حکمت و نور روزانه",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = GlassPrimaryDark
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(GlassSurfaceElevated)
                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = wisdom.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = GlassTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = wisdom.text,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                fontWeight = FontWeight.Medium,
                color = GlassTextPrimary,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "— ${wisdom.source}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = GlassTextSecondary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurfaceHighlight)
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .clickable(onClick = onNextWisdom)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("next_wisdom_btn")
                ) {
                    Text(
                        text = "حکمت بعدی ↻",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GlassTextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: SpiritualLesson,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(18.dp)
            .testTag("lesson_card_${lesson.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (lesson.isCompleted) GlassPrimaryContainer
                                else GlassSurfaceElevated
                            )
                            .border(
                                1.dp,
                                if (lesson.isCompleted) GlassPrimary.copy(alpha = 0.5f) else GlassBorderSubtle,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${lesson.number}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (lesson.isCompleted) GlassPrimaryDark else GlassTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GlassSurfaceElevated)
                            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = lesson.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = GlassPrimaryDark
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("fav_btn_${lesson.id}")
                    ) {
                        Icon(
                            imageVector = if (lesson.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (lesson.isFavorite) LotusGold else GlassTextMuted
                        )
                    }

                    IconButton(
                        onClick = onToggleCompleted,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("check_btn_${lesson.id}")
                    ) {
                        Icon(
                            imageVector = if (lesson.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Mark Completed",
                            tint = if (lesson.isCompleted) GlassPrimaryDark else GlassTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GlassTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = lesson.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = GlassTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Audiotrack,
                        contentDescription = null,
                        tint = GlassPrimaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${lesson.durationMinutes} دقیقه مطالعه و حضور",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlassTextSecondary
                    )
                }

                if (lesson.isCompleted) {
                    Text(
                        text = "✓ تکمیل شده",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GlassPrimaryDark
                    )
                } else {
                    Text(
                        text = "شروع درس ←",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GlassPrimaryDark
                    )
                }
            }
        }
    }
}

@Composable
fun AmbientMiniPlayer(
    isPlaying: Boolean,
    soundTitle: String,
    volume: Float,
    onTogglePlay: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("ambient_mini_player")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPlaying) GlassPrimary
                            else GlassSurfaceElevated
                        )
                        .border(1.dp, GlassBorder, CircleShape)
                        .clickable(onClick = onTogglePlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play Sound",
                        tint = GlassTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "فرکانس آرامش‌بخش ذهن",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlassTextMuted
                        )
                        if (isPlaying) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = GlassPrimaryDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = soundTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = GlassTextPrimary
                    )
                }
            }

            // Volume mini slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(105.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Volume",
                    tint = GlassTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = GlassPrimaryDark,
                        activeTrackColor = GlassPrimaryDark,
                        inactiveTrackColor = GlassSurfaceElevated
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun FontSizeSelectorButton(
    currentSize: ReaderFontSize,
    onSelectSize: (ReaderFontSize) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.testTag("font_size_btn")
        ) {
            Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = "Font Size",
                tint = GlassTextPrimary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ReaderFontSize.entries.forEach { size ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = size.title,
                            fontWeight = if (size == currentSize) FontWeight.Bold else FontWeight.Normal,
                            color = if (size == currentSize) GlassPrimaryDark else GlassTextPrimary
                        )
                    },
                    onClick = {
                        onSelectSize(size)
                        expanded = false
                    }
                )
            }
        }
    }
}

