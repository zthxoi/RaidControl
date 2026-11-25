package ru.checkraid.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.checkraid.app.ui.screens.AddRaidScreen
import ru.checkraid.app.ui.screens.ChecklistsScreen
import ru.checkraid.app.ui.screens.MapScreen
import ru.checkraid.app.ui.screens.RaidsScreen
import ru.checkraid.app.ui.screens.SettingsScreen
import ru.checkraid.app.ui.viewmodel.RaidViewModel

@Composable
fun RaidControlApp(
    raidViewModel: RaidViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showTopLevelNav = Screen.bottomNavItems.any { it.route == currentDestination?.route }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        if (useNavigationRail) {
            Row(modifier = Modifier.fillMaxSize()) {
                if (showTopLevelNav) {
                    NavigationRail {
                        Screen.bottomNavItems.forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTopLevel(navController, screen.route) },
                                icon = {
                                    screen.icon?.let { icon ->
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = stringResource(screen.titleRes)
                                        )
                                    }
                                },
                                label = { Text(text = stringResource(screen.titleRes)) }
                            )
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    RaidNavHost(navController = navController, viewModel = raidViewModel)
                }
            }
        } else {
            Scaffold(
                bottomBar = {
                    if (showTopLevelNav) {
                        NavigationBar {
                            Screen.bottomNavItems.forEach { screen ->
                                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTopLevel(navController, screen.route) },
                                    icon = {
                                        screen.icon?.let { icon ->
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = stringResource(screen.titleRes)
                                            )
                                        }
                                    },
                                    label = { Text(text = stringResource(screen.titleRes)) }
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    RaidNavHost(navController = navController, viewModel = raidViewModel)
                }
            }
        }
    }
}

private fun navigateToTopLevel(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun RaidNavHost(
    navController: NavHostController,
    viewModel: RaidViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Raids.route
    ) {
        composable(Screen.Raids.route) {
            RaidsScreen(
                viewModel = viewModel,
                onAddRaidClick = { navController.navigate(Screen.AddRaid.createRoute()) },
                onRaidClick = { raidId -> navController.navigate(Screen.AddRaid.createRoute(raidId)) }
            )
        }
        composable(
            route = Screen.AddRaid.routeWithArg,
            arguments = listOf(
                navArgument(Screen.AddRaid.ARG_RAID_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val raidId = backStackEntry.arguments?.getLong(Screen.AddRaid.ARG_RAID_ID) ?: -1L
            AddRaidScreen(
                raidId = raidId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Checklists.route) {
            ChecklistsScreen(viewModel = viewModel)
        }
        composable(Screen.Map.route) {
            MapScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = viewModel)
        }
    }
}