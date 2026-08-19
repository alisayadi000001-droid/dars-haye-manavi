package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.FrostedGlassMeshBackdrop
import com.example.ui.components.PersianLayout
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.screens.LessonImportScreen
import com.example.ui.screens.LessonsListScreen
import com.example.ui.screens.MeditationScreen
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
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NavigationScreen
import com.example.ui.viewmodel.SpiritualViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SpiritualViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                PersianLayout {
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    BackHandler(enabled = currentScreen != NavigationScreen.HOME) {
                        when (currentScreen) {
                            NavigationScreen.LESSON_DETAIL, NavigationScreen.IMPORT_LESSONS -> {
                                viewModel.navigateTo(NavigationScreen.LESSONS)
                            }
                            else -> {
                                viewModel.navigateTo(NavigationScreen.HOME)
                            }
                        }
                    }

                    Scaffold(
                        containerColor = Color.Transparent,
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            if (currentScreen != NavigationScreen.LESSON_DETAIL && currentScreen != NavigationScreen.IMPORT_LESSONS) {
                                CenterAlignedTopAppBar(
                                    title = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(GlassPrimary.copy(alpha = 0.2f))
                                                    .border(1.dp, GlassBorderSubtle, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Spa,
                                                    contentDescription = "Lotus Logo",
                                                    tint = GlassPrimaryDark,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = when (currentScreen) {
                                                    NavigationScreen.HOME -> "دروس معنوی"
                                                    NavigationScreen.LESSONS -> "گنجینه آموزه‌ها"
                                                    NavigationScreen.MEDITATION -> "مراقبه و حضور"
                                                    NavigationScreen.JOURNAL -> "دفترچه تأملات"
                                                    else -> "دروس معنوی"
                                                },
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = GlassTextPrimary
                                            )
                                        }
                                    },
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = GlassSurface
                                    ),
                                    modifier = Modifier.border(1.dp, GlassBorderSubtle, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                                )
                            }
                        },
                        bottomBar = {
                            if (currentScreen != NavigationScreen.LESSON_DETAIL && currentScreen != NavigationScreen.IMPORT_LESSONS) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                                        .background(GlassSurface)
                                        .border(1.dp, GlassBorder, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                                ) {
                                    NavigationBar(
                                        containerColor = Color.Transparent,
                                        modifier = Modifier
                                            .windowInsetsPadding(WindowInsets.navigationBars)
                                            .testTag("main_navigation_bar")
                                    ) {
                                        NavigationBarItem(
                                            selected = currentScreen == NavigationScreen.HOME,
                                            onClick = { viewModel.navigateTo(NavigationScreen.HOME) },
                                            icon = {
                                                Icon(
                                                    imageVector = if (currentScreen == NavigationScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                                    contentDescription = "Home"
                                                )
                                            },
                                            label = { Text("خانه", style = MaterialTheme.typography.labelSmall) },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = GlassPrimaryDark,
                                                unselectedIconColor = GlassTextMuted,
                                                selectedTextColor = GlassPrimaryDark,
                                                unselectedTextColor = GlassTextMuted,
                                                indicatorColor = GlassSurfaceHighlight
                                            ),
                                            modifier = Modifier.testTag("nav_home_tab")
                                        )

                                        NavigationBarItem(
                                            selected = currentScreen == NavigationScreen.LESSONS,
                                            onClick = { viewModel.navigateTo(NavigationScreen.LESSONS) },
                                            icon = {
                                                Icon(
                                                    imageVector = if (currentScreen == NavigationScreen.LESSONS) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                                    contentDescription = "Lessons"
                                                )
                                            },
                                            label = { Text("درس‌ها", style = MaterialTheme.typography.labelSmall) },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = GlassPrimaryDark,
                                                unselectedIconColor = GlassTextMuted,
                                                selectedTextColor = GlassPrimaryDark,
                                                unselectedTextColor = GlassTextMuted,
                                                indicatorColor = GlassSurfaceHighlight
                                            ),
                                            modifier = Modifier.testTag("nav_lessons_tab")
                                        )

                                        NavigationBarItem(
                                            selected = currentScreen == NavigationScreen.MEDITATION,
                                            onClick = { viewModel.navigateTo(NavigationScreen.MEDITATION) },
                                            icon = {
                                                Icon(
                                                    imageVector = if (currentScreen == NavigationScreen.MEDITATION) Icons.Filled.SelfImprovement else Icons.Outlined.SelfImprovement,
                                                    contentDescription = "Meditation"
                                                )
                                            },
                                            label = { Text("مراقبه", style = MaterialTheme.typography.labelSmall) },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = GlassSecondary,
                                                unselectedIconColor = GlassTextMuted,
                                                selectedTextColor = GlassSecondary,
                                                unselectedTextColor = GlassTextMuted,
                                                indicatorColor = GlassSurfaceHighlight
                                            ),
                                            modifier = Modifier.testTag("nav_meditation_tab")
                                        )

                                        NavigationBarItem(
                                            selected = currentScreen == NavigationScreen.JOURNAL,
                                            onClick = { viewModel.navigateTo(NavigationScreen.JOURNAL) },
                                            icon = {
                                                Icon(
                                                    imageVector = if (currentScreen == NavigationScreen.JOURNAL) Icons.Filled.Spa else Icons.Outlined.Spa,
                                                    contentDescription = "Journal"
                                                )
                                            },
                                            label = { Text("تأملات", style = MaterialTheme.typography.labelSmall) },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = GlassPrimaryDark,
                                                unselectedIconColor = GlassTextMuted,
                                                selectedTextColor = GlassPrimaryDark,
                                                unselectedTextColor = GlassTextMuted,
                                                indicatorColor = GlassSurfaceHighlight
                                            ),
                                            modifier = Modifier.testTag("nav_journal_tab")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                NavigationScreen.HOME -> HomeScreen(viewModel = viewModel)
                                NavigationScreen.LESSONS -> LessonsListScreen(viewModel = viewModel)
                                NavigationScreen.LESSON_DETAIL -> LessonDetailScreen(viewModel = viewModel)
                                NavigationScreen.MEDITATION -> MeditationScreen(viewModel = viewModel)
                                NavigationScreen.JOURNAL -> JournalScreen(viewModel = viewModel)
                                NavigationScreen.IMPORT_LESSONS -> LessonImportScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
