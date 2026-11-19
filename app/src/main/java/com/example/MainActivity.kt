package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.HeadsetMic
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.ai.PlaceholderAiRepository
import com.example.data.local.AppDatabase
import com.example.data.local.CallSathiRepository
import com.example.domain.model.UiLanguage
import com.example.ui.screens.AcademyScreen
import com.example.ui.screens.AccountAndMoreScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LandingAuthScreen
import com.example.ui.screens.LiveAssistantScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.theme.CallSathiTheme
import com.example.ui.theme.ElectricBlue
import com.example.ui.viewmodel.MainViewModel
import com.example.util.VoiceAssistManager
import com.example.util.stringsFor

object Routes {
    const val DASHBOARD = "dashboard"
    const val LIVE_ASSISTANT = "live_assistant"
    const val PRACTICE = "practice"
    const val ACADEMY = "academy"
    const val ACCOUNT = "account"
}

private data class NavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallSathiApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallSathiApp() {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel(
        factory = remember {
            val db = AppDatabase.getInstance(context)
            val repo = CallSathiRepository(context, db)
            val aiRepo = PlaceholderAiRepository()
            val voiceManager = VoiceAssistManager(context)
            MainViewModel.Factory(repo, aiRepo, voiceManager)
        }
    )

    val activeUserId by viewModel.activeUserId.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isDemoMode.collectAsStateWithLifecycle()
    val uiLanguage by viewModel.uiLanguage.collectAsStateWithLifecycle()
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val profile by viewModel.currentProfile.collectAsStateWithLifecycle()
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val usage by viewModel.usageRecord.collectAsStateWithLifecycle()
    val adminSettings by viewModel.adminSettings.collectAsStateWithLifecycle()
    val practiceHistory by viewModel.practiceHistory.collectAsStateWithLifecycle()
    val learningProgress by viewModel.learningProgress.collectAsStateWithLifecycle()
    val userTransactions by viewModel.userTransactions.collectAsStateWithLifecycle()
    val userTickets by viewModel.userTickets.collectAsStateWithLifecycle()
    val allProfilesForAdmin by viewModel.allProfilesForAdmin.collectAsStateWithLifecycle()
    val allTransactionsForAdmin by viewModel.allTransactionsForAdmin.collectAsStateWithLifecycle()
    val allTicketsForAdmin by viewModel.allTicketsForAdmin.collectAsStateWithLifecycle()
    val liveState by viewModel.liveState.collectAsStateWithLifecycle()
    val practiceState by viewModel.practiceState.collectAsStateWithLifecycle()
    val rmsLevel by viewModel.voiceAssistManager.rmsLevel.collectAsStateWithLifecycle()
    val partialSpeech by viewModel.voiceAssistManager.partialText.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val layoutDirection = if (uiLanguage == UiLanguage.URDU) LayoutDirection.Rtl else LayoutDirection.Ltr

    CallSathiTheme(darkTheme = darkMode) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            if (activeUserId == null) {
                LandingAuthScreen(
                    language = uiLanguage,
                    darkMode = darkMode,
                    authState = authState,
                    adminSettings = adminSettings,
                    onToggleLanguage = viewModel::toggleUiLanguage,
                    onToggleDarkMode = viewModel::toggleDarkMode,
                    onWatchDemo = viewModel::enterDemoMode,
                    onSignUp = viewModel::signUp,
                    onLogin = viewModel::login,
                    onAdminDemoLogin = viewModel::loginAsAdminDemo,
                    onResetPassword = viewModel::requestPasswordReset
                )
            } else {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route ?: Routes.DASHBOARD
                val snackbarHostState = remember { SnackbarHostState() }
                var accountSubTab by remember { mutableIntStateOf(0) }
                val copy = stringsFor(uiLanguage)

                LaunchedEffect(toastMessage) {
                    val msg = toastMessage
                    if (!msg.isNullOrBlank()) {
                        snackbarHostState.showSnackbar(msg)
                        viewModel.consumeToast()
                    }
                }

                val destinations = listOf(
                    NavDestination(Routes.DASHBOARD, copy.navHome, Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
                    NavDestination(Routes.LIVE_ASSISTANT, copy.navLive, Icons.Filled.Mic, Icons.Outlined.Mic, "nav_live"),
                    NavDestination(Routes.PRACTICE, copy.navPractice, Icons.Filled.RecordVoiceOver, Icons.Outlined.RecordVoiceOver, "nav_practice"),
                    NavDestination(Routes.ACADEMY, copy.navAcademy, Icons.Filled.School, Icons.Outlined.School, "nav_academy"),
                    NavDestination(Routes.ACCOUNT, copy.navAccount, Icons.Filled.Person, Icons.Outlined.Person, "nav_account")
                )

                fun navigateTo(route: String) {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isWideScreen = maxWidth >= 600.dp

                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.HeadsetMic,
                                            contentDescription = "CallSathi AI",
                                            tint = ElectricBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "CallSathi AI",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                actions = {
                                    TextButton(
                                        onClick = viewModel::toggleUiLanguage,
                                        modifier = Modifier.testTag("topbar_language_toggle")
                                    ) {
                                        Icon(Icons.Rounded.Language, contentDescription = "Language", modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (uiLanguage == UiLanguage.URDU) "EN" else "اردو")
                                    }
                                    IconButton(onClick = viewModel::toggleDarkMode) {
                                        Icon(
                                            imageVector = if (darkMode) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                                            contentDescription = "Toggle theme"
                                        )
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            if (!isWideScreen) {
                                NavigationBar(
                                    windowInsets = WindowInsets.navigationBars,
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    destinations.forEach { dest ->
                                        val selected = currentRoute == dest.route
                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = { navigateTo(dest.route) },
                                            icon = {
                                                Icon(
                                                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                                    contentDescription = dest.label
                                                )
                                            },
                                            label = { Text(dest.label, maxLines = 1) },
                                            modifier = Modifier.testTag(dest.testTag)
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (isWideScreen) {
                                NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                                    destinations.forEach { dest ->
                                        val selected = currentRoute == dest.route
                                        NavigationRailItem(
                                            selected = selected,
                                            onClick = { navigateTo(dest.route) },
                                            icon = {
                                                Icon(
                                                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                                    contentDescription = dest.label
                                                )
                                            },
                                            label = { Text(dest.label) },
                                            modifier = Modifier.testTag(dest.testTag)
                                        )
                                    }
                                }
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                NavHost(
                                    navController = navController,
                                    startDestination = Routes.DASHBOARD
                                ) {
                                    composable(Routes.DASHBOARD) {
                                        DashboardScreen(
                                            profile = profile,
                                            isDemoMode = isDemoMode,
                                            language = uiLanguage,
                                            usage = usage,
                                            adminSettings = adminSettings,
                                            learningProgress = learningProgress,
                                            practiceHistory = practiceHistory,
                                            isPlaceholderAi = viewModel.isPlaceholderAi,
                                            onOpenLiveAssistant = { navigateTo(Routes.LIVE_ASSISTANT) },
                                            onOpenPractice = { navigateTo(Routes.PRACTICE) },
                                            onOpenAcademy = { navigateTo(Routes.ACADEMY) },
                                            onOpenPlans = {
                                                accountSubTab = 1
                                                navigateTo(Routes.ACCOUNT)
                                            },
                                            onOpenSupport = {
                                                accountSubTab = 2
                                                navigateTo(Routes.ACCOUNT)
                                            }
                                        )
                                    }
                                    composable(Routes.LIVE_ASSISTANT) {
                                        LiveAssistantScreen(
                                            language = uiLanguage,
                                            uiState = liveState,
                                            preferences = preferences,
                                            rmsLevel = rmsLevel,
                                            partialSpeech = partialSpeech,
                                            isPlaceholderAi = viewModel.isPlaceholderAi,
                                            onAcceptPrivacy = viewModel::acceptAudioPrivacy,
                                            onSetDeviceMode = viewModel::setDeviceMode,
                                            onSetCallMode = viewModel::setCallMode,
                                            onStartMic = viewModel::startMicrophoneListening,
                                            onPauseMic = viewModel::pauseListening,
                                            onResumeMic = viewModel::resumeListening,
                                            onStopMic = viewModel::stopListening,
                                            onManualInputChange = viewModel::updateManualInput,
                                            onTranslateSubmit = { viewModel.processCustomerUtterance(it) },
                                            onRefineReply = viewModel::refineReply,
                                            onSpeakReply = viewModel::speakReplyExplicitly,
                                            onSaveSegment = viewModel::saveCurrentSegmentExplicitly,
                                            onSearchQueryChange = viewModel::updateHistorySearch,
                                            onClearHistory = viewModel::clearSessionHistory,
                                            onShowToast = viewModel::showToast
                                        )
                                    }
                                    composable(Routes.PRACTICE) {
                                        PracticeScreen(
                                            language = uiLanguage,
                                            uiState = practiceState,
                                            usage = usage,
                                            pastSessions = practiceHistory,
                                            voiceAssistManager = viewModel.voiceAssistManager,
                                            isPlaceholderAi = viewModel.isPlaceholderAi,
                                            onStartScenario = viewModel::startPracticeScenario,
                                            onUpdateDraft = viewModel::updatePracticeDraft,
                                            onSubmitReply = viewModel::submitPracticeReply,
                                            onFinishEarly = viewModel::finishPracticeEarly,
                                            onExitScenario = viewModel::exitPracticeScenario,
                                            onSpeakCustomer = { viewModel.speakReplyExplicitly(it, false) }
                                        )
                                    }
                                    composable(Routes.ACADEMY) {
                                        AcademyScreen(
                                            language = uiLanguage,
                                            progressList = learningProgress,
                                            onSpeakText = viewModel::speakReplyExplicitly,
                                            onCompleteLessonQuiz = viewModel::completeLessonQuiz
                                        )
                                    }
                                    composable(Routes.ACCOUNT) {
                                        AccountAndMoreScreen(
                                            initialSubTab = accountSubTab,
                                            language = uiLanguage,
                                            darkMode = darkMode,
                                            profile = profile,
                                            preferences = preferences,
                                            adminSettings = adminSettings,
                                            userTransactions = userTransactions,
                                            userTickets = userTickets,
                                            allProfiles = allProfilesForAdmin,
                                            allTransactions = allTransactionsForAdmin,
                                            allTickets = allTicketsForAdmin,
                                            onToggleLanguage = viewModel::toggleUiLanguage,
                                            onUpdatePreferences = viewModel::updatePreferences,
                                            onSubmitPayment = viewModel::submitPaymentRequest,
                                            onSubmitTicket = viewModel::submitSupportTicket,
                                            onApprovePayment = viewModel::approvePayment,
                                            onRejectPayment = viewModel::rejectPayment,
                                            onToggleUserStatus = viewModel::toggleUserAccountStatus,
                                            onSaveAdminSettings = viewModel::saveAdminSettings,
                                            onResolveTicket = viewModel::resolveTicket,
                                            onLogout = viewModel::logout,
                                            onDeleteAccount = viewModel::deleteCurrentAccount
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
}
