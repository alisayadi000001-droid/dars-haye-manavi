package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FontSizeSelectorButton
import com.example.ui.components.FrostedGlassMeshBackdrop
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    viewModel: SpiritualViewModel,
    modifier: Modifier = Modifier
) {
    val lesson by viewModel.selectedLesson.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val isPlayingAmbient by viewModel.isPlayingAmbient.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    if (lesson == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("درس مورد نظر یافت نشد.", color = GlassTextPrimary)
        }
        return
    }

    val currentLesson = lesson!!
    var userNoteText by remember(currentLesson.id) { mutableStateOf(currentLesson.userNote) }

    val baseBodySize = (16 * fontSize.scaleFactor).sp
    val baseLineHeight = (28 * fontSize.scaleFactor).sp
    val baseHeadingSize = (19 * fontSize.scaleFactor).sp

    FrostedGlassMeshBackdrop(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "آموزه ${currentLesson.number}: ${currentLesson.category}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateTo(NavigationScreen.LESSONS) },
                            modifier = Modifier.testTag("back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = GlassTextPrimary
                            )
                        }
                    },
                    actions = {
                        FontSizeSelectorButton(
                            currentSize = fontSize,
                            onSelectSize = { viewModel.setFontSize(it) }
                        )

                        IconButton(
                            onClick = { viewModel.toggleLessonFavorite(currentLesson.id) },
                            modifier = Modifier.testTag("detail_fav_btn")
                        ) {
                            Icon(
                                imageVector = if (currentLesson.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (currentLesson.isFavorite) LotusGold else GlassTextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = GlassSurface
                    ),
                    modifier = Modifier.border(1.dp, GlassBorderSubtle, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("lesson_detail_screen"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Glass Info Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "آموزه شماره ${currentLesson.number}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = GlassPrimaryDark
                                )

                                Text(
                                    text = "${currentLesson.durationMinutes} دقیقه مطالعه",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlassTextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = currentLesson.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = GlassTextPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = currentLesson.subtitle,
                                style = MaterialTheme.typography.bodyLarge,
                                color = GlassTextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Audio Resonance Trigger
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GlassSurfaceElevated)
                                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
                                    .clickable {
                                        val soundId = if (currentLesson.recommendedFrequencyHz == 528) "528hz" else "432hz"
                                        viewModel.toggleAmbientSound(soundId)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                                    .testTag("lesson_audio_toggle_btn")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isPlayingAmbient) GlassPrimary
                                                    else GlassSurfaceHighlight
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isPlayingAmbient) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = GlassTextPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "فرکانس آرامش (${currentLesson.recommendedFrequencyHz} Hz)",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = GlassTextPrimary
                                            )
                                            Text(
                                                text = if (isPlayingAmbient) "در حال پخش طنین آرامش‌بخش..." else "لمس کنید تا حین مطالعه پخش شود",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = GlassTextMuted
                                            )
                                        }
                                    }

                                    if (isPlayingAmbient) {
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

                // Wisdom Quote Glass Card
                if (currentLesson.quote.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(GlassSurface)
                                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = GlassSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "حکمت آغازین",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentLesson.quote,
                                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = baseLineHeight),
                                    fontWeight = FontWeight.Medium,
                                    color = GlassTextPrimary
                                )
                                if (currentLesson.quoteAuthor.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "— ${currentLesson.quoteAuthor}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GlassTextSecondary,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                // Lesson Sections (Frosted Glass Cards)
                itemsIndexed(currentLesson.sections) { _, section ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = baseHeadingSize),
                                fontWeight = FontWeight.Bold,
                                color = GlassPrimaryDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = section.content,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = baseBodySize,
                                    lineHeight = baseLineHeight
                                ),
                                color = GlassTextPrimary
                            )

                            if (!section.keyWisdom.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(GlassSurfaceElevated)
                                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                                        .padding(14.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.Spa,
                                            contentDescription = null,
                                            tint = GlassPrimaryDark,
                                            modifier = Modifier
                                                .size(20.dp)
                                                .padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "نکته کلیدی: ${section.keyWisdom}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GlassTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Questions Glass Card
                if (currentLesson.reflectionQuestions.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(GlassSurface)
                                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.HelpOutline,
                                        contentDescription = null,
                                        tint = GlassPrimaryDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "پرسش‌های ژرف‌اندیشی و تأمل",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassTextPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                currentLesson.reflectionQuestions.forEachIndexed { qIndex, qText ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "${qIndex + 1}.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = GlassPrimaryDark,
                                            modifier = Modifier.width(22.dp)
                                        )
                                        Text(
                                            text = qText,
                                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                                            color = GlassTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Daily Affirmation Glass Card
                if (currentLesson.dailyAffirmation.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(GlassTertiary.copy(alpha = 0.12f))
                                .border(1.dp, GlassTertiary.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "عبارت تأکیدی روزانه",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassTertiary
                                    )

                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(currentLesson.dailyAffirmation))
                                            Toast.makeText(context, "عبارت تأکیدی کپی شد", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Affirmation",
                                            tint = GlassTertiary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "«${currentLesson.dailyAffirmation}»",
                                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = GlassTextPrimary
                                )
                            }
                        }
                    }
                }

                // Reflection Note Editor (Glass)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = GlassPrimaryDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "دفترچه تأملات شما برای این درس",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GlassTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "بینش‌ها، الهامات و تجربیات حین مطالعه را بنویسید (خودکار ذخیره می‌شود):",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlassTextSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = userNoteText,
                                onValueChange = {
                                    userNoteText = it
                                    viewModel.saveNote(currentLesson.id, it)
                                },
                                placeholder = {
                                    Text(
                                        "احساس، بینش یا عهد درونی‌تان را یادداشت کنید...",
                                        color = GlassTextMuted
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .testTag("lesson_note_input"),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GlassPrimary,
                                    unfocusedBorderColor = GlassBorder,
                                    focusedContainerColor = GlassSurfaceElevated,
                                    unfocusedContainerColor = GlassSurfaceElevated,
                                    focusedTextColor = GlassTextPrimary,
                                    unfocusedTextColor = GlassTextPrimary
                                )
                            )
                        }
                    }
                }

                // Mark Complete Glass Button
                item {
                    Button(
                        onClick = {
                            viewModel.toggleLessonCompleted(currentLesson.id)
                            val msg = if (!currentLesson.isCompleted) "درس با موفقیت ثبت شد!" else "وضعیت مطالعه تغییر کرد."
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("toggle_completed_btn"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentLesson.isCompleted) GlassSurfaceHighlight
                            else GlassPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (currentLesson.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentLesson.isCompleted) "این درس را به پایان رسانده‌اید (تغییر وضعیت)" else "علامت‌گذاری به عنوان خوانده شده",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
