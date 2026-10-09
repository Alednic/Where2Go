package org.umn.where2go.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.umn.where2go.ui.screen.*
import org.umn.where2go.viewmodel.ExploreViewModel

object Routes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val PERMISSION = "permission"
    const val EXPLORE = "explore"
    const val LEADERBOARD = "leaderboard"
    const val ARG_PLACE_ID = "placeId"
    const val DETAIL = "detail/{$ARG_PLACE_ID}"
    const val CHECKIN = "checkin/{$ARG_PLACE_ID}"

    fun detail(id: Int) = "detail/$id"
    fun checkIn(id: Int) = "checkin/$id"
}

private data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem(Routes.EXPLORE, "Jelajah", Icons.Filled.Explore),
    TabItem(Routes.LEADERBOARD, "Peringkat", Icons.Filled.EmojiEvents)
)

@Composable
private fun AppBottomBar(navController: NavHostController) {
    val entry by navController.currentBackStackEntryAsState()
    val current = entry?.destination?.route
    NavigationBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = current == tab.route,
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(Routes.EXPLORE) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(tab.icon, null) },
                label = { Text(tab.label) }
            )
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val exploreVm: ExploreViewModel = viewModel()   // dibagi ke Explore dan Detail (favorit)
    val bottomBar: @Composable () -> Unit = { AppBottomBar(navController) }
    val placeArg = listOf(navArgument(Routes.ARG_PLACE_ID) { type = NavType.IntType })

    NavHost(navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.AUTH) { popUpTo(Routes.SPLASH) { inclusive = true } }
            })
        }
        composable(Routes.AUTH) {
            AuthScreen(onAuthenticated = {
                navController.navigate(Routes.PERMISSION) { popUpTo(Routes.AUTH) { inclusive = true } }
            })
        }
        composable(Routes.PERMISSION) {
            PermissionScreen(onContinue = {
                navController.navigate(Routes.EXPLORE) { popUpTo(Routes.PERMISSION) { inclusive = true } }
            })
        }
        composable(Routes.EXPLORE) {
            ExploreScreen(exploreVm,
                onPlaceClick = { id -> navController.navigate(Routes.detail(id)) },
                bottomBar = bottomBar)
        }
        composable(Routes.LEADERBOARD) { LeaderboardScreen(bottomBar) }

        composable(Routes.DETAIL, arguments = placeArg) { entry ->
            val id = entry.arguments?.getInt(Routes.ARG_PLACE_ID) ?: -1
            PlaceDetailScreen(
                placeId = id, vm = exploreVm,
                onBack = { navController.popBackStack() },
                onCheckIn = { navController.navigate(Routes.checkIn(id)) }
            )
        }
        composable(Routes.CHECKIN, arguments = placeArg) { entry ->
            val id = entry.arguments?.getInt(Routes.ARG_PLACE_ID) ?: -1
            CheckInScreen(placeId = id, onBack = { navController.popBackStack() },
                onDone = { navController.popBackStack() })
        }
    }
}