package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.FrostedGlassMeshBackdrop
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.GlassPrimaryDark
import com.example.ui.theme.GlassSecondary
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GlassSurfaceHighlight
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.LotusGold
import com.example.ui.viewmodel.BreathPhase
import com.example.ui.viewmodel.SpiritualViewModel

@Composable
fun MeditationScreen(
    viewModel: SpiritualViewModel,
    modifier: Modifier = Modifier
) {
    val isRunning by viewModel.isMeditationRunning.collectAsState()
    val remainingSec by viewModel.remainingSec.collectAsState()
    val totalSec by viewModel.meditationTotalSec.collectAsState()
    val breathPhase by viewModel.currentBreathPhase.collectAsState()
    val phaseSecLeft by viewModel.breathPhaseSecondsLeft.collectAsState()
    val selectedSoundId by viewModel.selectedSoundId.collectAsState()
    val ambientVolume by viewModel.ambientVolume.collectAsState()

    var selectedDurationMinutes by remember { mutableIntStateOf(5) }
    var selectedSound by remember { mutableStateOf(selectedSoundId) }

    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    FrostedGlassMeshBackdrop(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("meditation_screen"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Title
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "مراقبه، تنفس و سکون جان",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = GlassTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "با ضرباهنگ تنفس هماهنگ شوید و ذهن را به خانه آرامش بیاورید",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GlassTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Glass Breathing Visualizer Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(GlassSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsating glow
                    Box(
                        modifier = Modifier
                            .size(230.dp)
                            .scale(if (isRunning) pulseScale else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        GlassPrimary.copy(alpha = 0.35f),
                                        GlassSecondary.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Inner Core Circle
                    Box(
                        modifier = Modifier
                            .size(175.dp)
                            .scale(if (isRunning && breathPhase == BreathPhase.INHALE) 1.08f else if (isRunning && breathPhase == BreathPhase.EXHALE) 0.92f else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        GlassPrimary,
                                        GlassSecondary
                                    )
                                )
                            )
                            .border(2.dp, GlassBorder, CircleShape)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (isRunning) {
                                Text(
                                    text = breathPhase.persianTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "$phaseSecLeft ثانیه",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = LotusGold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val mins = remainingSec / 60
                                val secs = remainingSec % 60
                                Text(
                                    text = String.format("%02d:%02d", mins, secs),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.SelfImprovement,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(42.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "آماده مراقبه",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "$selectedDurationMinutes دقیقه",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }

            // Duration Selection Chips (Glass)
            if (!isRunning) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "مدت زمان تمرین",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listOf(3, 5, 10, 15, 20)) { mins ->
                                FilterChip(
                                    selected = selectedDurationMinutes == mins,
                                    onClick = { selectedDurationMinutes = mins },
                                    label = {
                                        Text(
                                            "$mins دقیقه",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (selectedDurationMinutes == mins) GlassTextPrimary else GlassTextSecondary
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = GlassSurface,
                                        selectedContainerColor = GlassPrimary.copy(alpha = 0.35f)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selectedDurationMinutes == mins,
                                        borderColor = if (selectedDurationMinutes == mins) GlassPrimary else GlassBorder
                                    )
                                )
                            }
                        }
                    }
                }

                // Sound Frequency Selection (Glass Cards)
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "طنین و فرکانس پس‌زمینه",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        viewModel.meditationSounds.forEach { sound ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        if (selectedSound == sound.id) GlassPrimary.copy(alpha = 0.2f)
                                        else GlassSurface
                                    )
                                    .border(
                                        1.dp,
                                        if (selectedSound == sound.id) GlassPrimary else GlassBorderSubtle,
                                        RoundedCornerShape(18.dp)
                                    )
                                    .clickable { selectedSound = sound.id }
                                    .padding(14.dp)
                            ) {
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
                                                .background(
                                                    if (selectedSound == sound.id) GlassPrimary
                                                    else GlassSurfaceElevated
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Spa,
                                                contentDescription = null,
                                                tint = GlassTextPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = sound.persianTitle,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = GlassTextPrimary
                                            )
                                            Text(
                                                text = sound.frequencyDescription,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = GlassTextSecondary
                                            )
                                        }
                                    }

                                    if (selectedSound == sound.id) {
                                        Icon(
                                            imageVector = Icons.Default.GraphicEq,
                                            contentDescription = null,
                                            tint = GlassPrimaryDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Volume Control (Glass Card)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Volume",
                            tint = GlassPrimaryDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "حجم صدا:",
                            style = MaterialTheme.typography.labelMedium,
                            color = GlassTextSecondary
                        )
                        Slider(
                            value = ambientVolume,
                            onValueChange = { viewModel.setAmbientVolume(it) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = GlassPrimaryDark,
                                activeTrackColor = GlassPrimaryDark,
                                inactiveTrackColor = GlassSurfaceElevated
                            ),
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        )
                    }
                }
            }

            // Main Action Button (Glass/Indigo Button)
            item {
                Button(
                    onClick = {
                        if (isRunning) {
                            viewModel.stopMeditation()
                        } else {
                            viewModel.startMeditation(selectedDurationMinutes, selectedSound)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("meditation_action_btn"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) MaterialTheme.colorScheme.error else GlassPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isRunning) "پایان مراقبه و بازگشت" else "آغاز مراقبه و هماهنگی تنفس",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
