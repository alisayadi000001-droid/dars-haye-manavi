package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FrostedGlassMeshBackdrop
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
import com.example.ui.viewmodel.NavigationScreen
import com.example.ui.viewmodel.SpiritualViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonImportScreen(
    viewModel: SpiritualViewModel,
    modifier: Modifier = Modifier
) {
    var rawText by remember { mutableStateOf("") }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sampleTemplate = """
# عنوان درس جدید: بیداری قلب و سکون
دسته بندی: خودشناسی
زیرعنوان: راهنمای گام به گام صلح درونی
نقل قول: در اعماق آرامش، صدای حقیقت شنیده می‌شود.
عبارت تأکیدی: من سرشار از نور، آگاهی و آرامش الهی هستم.

## بخش اول: درک هیاهوی ذهن
متن توضیحات بخش اول درس...

## بخش دوم: ورود به سکون
متن توضیحات بخش دوم درس...

- پرسش: امروز چه زمان‌هایی توانستم به سکون درون برگردم؟
    """.trimIndent()

    FrostedGlassMeshBackdrop(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "جاسازی و درون‌ریزی فایل‌های درس",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateTo(NavigationScreen.LESSONS) },
                            modifier = Modifier.testTag("import_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = GlassTextPrimary
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
                    .testTag("lesson_import_screen"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Explanatory Banner (Glass)
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
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(GlassPrimary.copy(alpha = 0.2f))
                                        .border(1.dp, GlassPrimary.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = GlassPrimaryDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "آماده‌سازی برای دریافت فایل‌های شما",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GlassTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "این اپلیکیشن کاملاً برای پذیرش و جاسازی محتوای درس‌های معنوی شما طراحی شده است. شما می‌توانید فایل‌های خود را از دو طریق اضافه کنید:\n" +
                                        "۱. ارسال فایل‌ها یا متن درس‌ها در چت تا برایتان در اپ جاسازی شود.\n" +
                                        "۲. چسباندن (Paste) مستقیم متن یا ساختار فایل در کادر زیر برای اضافه شدن آنی به لیست درس‌ها.",
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                                color = GlassTextSecondary
                            )
                        }
                    }
                }

                // Text Input Box (Glass)
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "متن یا کد فایل درس:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = GlassTextPrimary
                            )

                            OutlinedButton(
                                onClick = {
                                    rawText = sampleTemplate
                                    Toast.makeText(context, "قالب نمونه جای‌گذاری شد", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = GlassPrimaryDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("درج قالب نمونه", style = MaterialTheme.typography.labelSmall, color = GlassPrimaryDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = rawText,
                            onValueChange = {
                                rawText = it
                                importStatusMessage = null
                            },
                            placeholder = {
                                Text(
                                    text = "متن فایل درس، عنوان‌ها و بخش‌ها یا ساختار JSON را اینجا بچسبانید...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlassTextMuted
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .testTag("raw_lesson_text_input"),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GlassPrimary,
                                unfocusedBorderColor = GlassBorder,
                                focusedContainerColor = GlassSurface,
                                unfocusedContainerColor = GlassSurface,
                                focusedTextColor = GlassTextPrimary,
                                unfocusedTextColor = GlassTextPrimary
                            )
                        )
                    }
                }

                // Status message (Glass)
                if (importStatusMessage != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSuccess) GlassPrimary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                                )
                                .border(
                                    1.dp,
                                    if (isSuccess) GlassPrimary else MaterialTheme.colorScheme.error,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isSuccess) GlassPrimaryDark else MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = importStatusMessage!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GlassTextPrimary
                                )
                            }
                        }
                    }
                }

                // Action Button (Glass/Indigo)
                item {
                    Button(
                        onClick = {
                            val result = viewModel.importLessons(rawText)
                            isSuccess = result.first
                            importStatusMessage = result.second
                            if (result.first) {
                                rawText = ""
                                Toast.makeText(context, result.second, Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("import_lesson_submit_btn"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "جاسازی و ذخیره در برنامه",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
