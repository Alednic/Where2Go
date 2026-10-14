package org.umn.where2go.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.umn.where2go.data.DummyData
import org.umn.where2go.ui.screen.BottomNavBar
import org.umn.where2go.ui.screen.*
import org.umn.where2go.viewmodel.ExploreViewModel

object Routes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val PERMISSION = "permission"
    const val MAP = "map"
    const val EXPLORE = "explore"
    const val LEADERBOARD = "leaderboard"
    const val PROFILE = "profile"
    const val ARG_PLACE_ID = "placeId"
    const val DETAIL = "detail/{$ARG_PLACE_ID}"
    const val CHECKIN = "checkin/{$ARG_PLACE_ID}"

    fun detail(id: Int) = "detail/$id"
    fun checkIn(id: Int) = "checkin/$id"
}

/** Pindah tab tanpa menumpuk back stack dan memulihkan state tab. */
private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.EXPLORE) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val exploreVm: ExploreViewModel = viewModel()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val placeArg = listOf(navArgument(Routes.ARG_PLACE_ID) { type = NavType.IntType })

    val bottomBar: @Composable () -> Unit = {
        BottomNavBar(
            currentRoute = currentRoute,
            onNavigate = { navController.navigateToTab(it) },
            onCheckInClick = {
                // tombol tengah: check-in ke tempat terdekat (data dummy)
                val nearest = DummyData.places.minByOrNull { it.distanceKm }?.id ?: 1
                navController.navigate(Routes.checkIn(nearest))
            }
        )
    }

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

        // ---- 5 tab ----
        composable(Routes.MAP) { PlaceholderScreen("Map", bottomBar) }
        composable(Routes.EXPLORE) {
            ExploreScreen(
                exploreVm,
                onPlaceClick = { id -> navController.navigate(Routes.detail(id)) },
                bottomBar = bottomBar
            )
        }
        composable(Routes.LEADERBOARD) { LeaderboardScreen(bottomBar) }
        composable(Routes.PROFILE) { PlaceholderScreen("Profil", bottomBar) }

        // ---- layar dengan argumen ----
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
            CheckInScreen(
                placeId = id,
                onBack = { navController.popBackStack() },
                onDone = { navController.popBackStack() }
            )
        }
    }
}
