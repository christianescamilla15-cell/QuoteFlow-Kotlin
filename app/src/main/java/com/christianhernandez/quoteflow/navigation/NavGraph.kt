package com.christianhernandez.quoteflow.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
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
import com.christianhernandez.quoteflow.ui.profile.ProfileScreen
import com.christianhernandez.quoteflow.ui.profile.ProfileViewModel
import com.christianhernandez.quoteflow.ui.vault.VaultScreen
import com.christianhernandez.quoteflow.ui.vault.VaultViewModel

sealed class Screen(val route: String, val label: String, val labelEs: String, val icon: ImageVector) {
    data object Feed : Screen("feed", "Feed", "Inicio", Icons.Default.Explore)
    data object Vault : Screen("vault", "Vault", "Guardadas", Icons.Outlined.Bookmarks)
    data object Challenge : Screen("challenge", "Challenge", "Reto", Icons.Default.LocalFireDepartment)
    data object Profile : Screen("profile", "Profile", "Perfil", Icons.Default.Person)
}

val bottomNavItems = listOf(Screen.Feed, Screen.Vault, Screen.Challenge, Screen.Profile)

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

    val feedState by feedViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Feed.route) {
                FeedScreen(
                    viewModel = feedViewModel,
                    language = language,
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

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    swipeCount = feedState.swipeCount,
                    language = language,
                    onLanguageChange = onLanguageChange,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode,
                )
            }
        }
    }
}
