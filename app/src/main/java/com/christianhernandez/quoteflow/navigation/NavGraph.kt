package com.christianhernandez.quoteflow.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.christianhernandez.quoteflow.QuoteFlowApp
import com.christianhernandez.quoteflow.ui.challenge.ChallengeScreen
import com.christianhernandez.quoteflow.ui.challenge.ChallengeViewModel
import com.christianhernandez.quoteflow.ui.feed.FeedScreen
import com.christianhernandez.quoteflow.ui.feed.FeedViewModel
import com.christianhernandez.quoteflow.ui.onboarding.OnboardingScreen
import com.christianhernandez.quoteflow.ui.onboarding.OnboardingViewModel
import com.christianhernandez.quoteflow.ui.packs.PacksScreen
import com.christianhernandez.quoteflow.ui.packs.PacksViewModel
import com.christianhernandez.quoteflow.ui.profile.ProfileScreen
import com.christianhernandez.quoteflow.ui.profile.ProfileViewModel
import com.christianhernandez.quoteflow.ui.reflection.WeeklyReflectionScreen
import com.christianhernandez.quoteflow.ui.reflection.WeeklyReflectionViewModel
import com.christianhernandez.quoteflow.ui.tutorial.TutorialOverlay
import com.christianhernandez.quoteflow.ui.tutorial.TutorialViewModel
import com.christianhernandez.quoteflow.ui.vault.VaultScreen
import com.christianhernandez.quoteflow.ui.vault.VaultViewModel

sealed class Screen(val route: String, val label: String, val labelEs: String, val icon: ImageVector) {
    data object Feed : Screen("feed", "Feed", "Inicio", Icons.Default.Explore)
    data object Vault : Screen("vault", "Vault", "Guardadas", Icons.Outlined.Bookmarks)
    data object Challenge : Screen("challenge", "Challenge", "Reto", Icons.Default.LocalFireDepartment)
    data object Packs : Screen("packs", "Packs", "Packs", Icons.Default.GridView)
    data object Profile : Screen("profile", "Profile", "Perfil", Icons.Default.Person)
    data object Onboarding : Screen("onboarding", "Onboarding", "Onboarding", Icons.Default.Explore)
    data object Reflection : Screen("reflection", "Reflection", "Reflexion", Icons.Default.Explore)
}

val bottomNavItems = listOf(Screen.Feed, Screen.Vault, Screen.Challenge, Screen.Packs, Screen.Profile)

@Composable
fun QuoteFlowNavHost(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as QuoteFlowApp

    // Shared ViewModels
    val feedViewModel: FeedViewModel = viewModel(factory = FeedViewModel.Factory(app.repository))
    val vaultViewModel: VaultViewModel = viewModel(factory = VaultViewModel.Factory(app.repository))
    val challengeViewModel: ChallengeViewModel = viewModel(factory = ChallengeViewModel.Factory(app.repository))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(app.repository))
    val packsViewModel: PacksViewModel = viewModel(factory = PacksViewModel.Factory(app.repository))
    val onboardingViewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModel.Factory(app.repository))
    val reflectionViewModel: WeeklyReflectionViewModel = viewModel(factory = WeeklyReflectionViewModel.Factory(app.repository))
    val tutorialViewModel: TutorialViewModel = viewModel()

    val feedState by feedViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()
    val onboardingState by onboardingViewModel.uiState.collectAsState()

    // Initialize tutorial (check SharedPreferences)
    LaunchedEffect(Unit) {
        tutorialViewModel.init(context)
    }

    // Check onboarding on startup
    LaunchedEffect(Unit) {
        onboardingViewModel.checkIfOnboardingNeeded()
    }

    // Navigate to onboarding if needed
    LaunchedEffect(onboardingState.needsOnboarding) {
        if (onboardingState.needsOnboarding == true) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(Screen.Feed.route) { inclusive = true }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Screen.Onboarding.route && currentRoute != Screen.Reflection.route

    // Tutorial shows after onboarding, only on feed route
    val showTutorialOverlay by tutorialViewModel.showTutorial.collectAsState()
    val isOnFeed = currentRoute == Screen.Feed.route

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = {
                                Text(
                                    text = if (language == "es") screen.labelEs else screen.label,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    language = language,
                    onComplete = { selectedLang ->
                        onLanguageChange(selectedLang)
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.Feed.route) {
                FeedScreen(
                    viewModel = feedViewModel,
                    language = language,
                    isPremium = profileState.isPremium,
                )
            }

            composable(Screen.Vault.route) {
                VaultScreen(
                    viewModel = vaultViewModel,
                    language = language,
                )
            }

            composable(Screen.Challenge.route) {
                ChallengeScreen(
                    viewModel = challengeViewModel,
                    swipeCount = feedState.swipeCount,
                    language = language,
                )
            }

            composable(Screen.Packs.route) {
                PacksScreen(
                    viewModel = packsViewModel,
                    language = language,
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    swipeCount = feedState.swipeCount,
                    language = language,
                    onLanguageChange = onLanguageChange,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode,
                    onLogout = {
                        // Reset device ID and restart
                        val prefs = context.getSharedPreferences("quoteflow_device_prefs", Context.MODE_PRIVATE)
                        prefs.edit().clear().apply()
                        // Navigate back to feed (a restart would be ideal, but this resets the flow)
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToReflection = {
                        navController.navigate(Screen.Reflection.route)
                    },
                )
            }

            composable(Screen.Reflection.route) {
                WeeklyReflectionScreen(
                    viewModel = reflectionViewModel,
                    language = language,
                    onBack = { navController.popBackStack() },
                    onShare = { reflectionText ->
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "$reflectionText\n\nVia QuoteFlow"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(
                            Intent.createChooser(
                                sendIntent,
                                if (language == "es") "Compartir reflexion" else "Share reflection"
                            )
                        )
                    },
                )
            }
        }
    }

    // Tutorial overlay sits on top of everything (including bottom nav)
    if (showTutorialOverlay && isOnFeed) {
        TutorialOverlay(
            viewModel = tutorialViewModel,
            language = language,
        )
    }
    } // end Box
}
