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
import org.umn.where2go.ui.screen.AuthScreen
import org.umn.where2go.ui.screen.BottomNavBar
import org.umn.where2go.ui.screen.CheckInScreen
import org.umn.where2go.ui.screen.ExploreScreen
import org.umn.where2go.ui.screen.LeaderboardScreen
import org.umn.where2go.ui.screen.PermissionScreen
import org.umn.where2go.ui.screen.PlaceDetailScreen
import org.umn.where2go.ui.screen.PlaceholderScreen
import org.umn.where2go.ui.screen.SplashScreen
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

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.EXPLORE) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNav(
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val exploreVm: ExploreViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val placeArg = listOf(
        navArgument(Routes.ARG_PLACE_ID) {
            type = NavType.IntType
        }
    )

    val bottomBar: @Composable () -> Unit = {
        BottomNavBar(
            currentRoute = currentRoute,
            onNavigate = { route ->
                navController.navigateToTab(route)
            },
            onCheckInClick = {
                exploreVm.places.firstOrNull()?.id?.let { id ->
                    navController.navigate(Routes.checkIn(id))
                }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.AUTH) {
            AuthScreen(
                onAuthenticated = {
                    navController.navigate(Routes.PERMISSION) {
                        popUpTo(Routes.AUTH) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.PERMISSION) {
            PermissionScreen(
                onContinue = {
                    navController.navigate(Routes.EXPLORE) {
                        popUpTo(Routes.PERMISSION) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.MAP) {
            PlaceholderScreen("Map", bottomBar)
        }

        composable(Routes.EXPLORE) {
            ExploreScreen(
                vm = exploreVm,
                onPlaceClick = { id ->
                    navController.navigate(Routes.detail(id))
                },
                bottomBar = bottomBar,
                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }

        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(bottomBar)
        }

        composable(Routes.PROFILE) {
            PlaceholderScreen("Profil", bottomBar)
        }

        composable(
            route = Routes.DETAIL,
            arguments = placeArg
        ) { entry ->
            val id = entry.arguments?.getInt(Routes.ARG_PLACE_ID) ?: -1

            PlaceDetailScreen(
                placeId = id,
                vm = exploreVm,
                onBack = {
                    navController.popBackStack()
                },
                onCheckIn = {
                    navController.navigate(Routes.checkIn(id))
                }
            )
        }

        composable(
            route = Routes.CHECKIN,
            arguments = placeArg
        ) { entry ->
            val id = entry.arguments?.getInt(Routes.ARG_PLACE_ID) ?: -1

            CheckInScreen(
                placeId = id,
                places = exploreVm.places,
                onBack = {
                    navController.popBackStack()
                },
                onDone = {
                    navController.popBackStack()
                }
            )
        }
    }
}
