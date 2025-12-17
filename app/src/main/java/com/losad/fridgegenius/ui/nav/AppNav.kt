package com.losad.fridgegenius.ui.nav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.losad.fridgegenius.ui.screens.*
import com.losad.fridgegenius.ui.viewmodel.IngredientViewModel

/* ---------- Routes ---------- */

object Routes {
    const val HOME = "home"
    const val FRIDGE = "fridge"
    const val RECIPE = "recipe"
    const val INSIGHTS = "insights"
    const val ADD_INGREDIENT = "add_ingredient"
    const val IMAGE_SCAN = "image_scan"
}

data class BottomTab(
    val route: String,
    val label: String,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav() {

    val navController = rememberNavController()

    val tabs = listOf(
        BottomTab(Routes.HOME, "홈", "🏠"),
        BottomTab(Routes.FRIDGE, "냉장고", "🧊"),
        BottomTab(Routes.RECIPE, "레시피", "🔍"),
        BottomTab(Routes.INSIGHTS, "인사이트", "📊")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == tab.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(tab.emoji) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            NavHost(
                navController = navController,
                startDestination = Routes.HOME
            ) {

                composable(Routes.HOME) {
                    HomeScreen(
                        onGoFridge = { navController.navigate(Routes.FRIDGE) },
                        onGoImageScan = { navController.navigate(Routes.IMAGE_SCAN) },
                        onGoRecipe = { navController.navigate(Routes.RECIPE) },
                        onGoInsights = { navController.navigate(Routes.INSIGHTS) }
                    )
                }

                composable(Routes.FRIDGE) {
                    FridgeScreen(
                        onGoAddIngredient = {
                            navController.navigate(Routes.ADD_INGREDIENT)
                        }
                    )
                }

                composable(Routes.RECIPE) {
                    RecipeHubScreen()
                }

                composable(Routes.INSIGHTS) {
                    InsightsScreen()
                }

                composable(Routes.IMAGE_SCAN) {
                    ImageScanScreen(onBack = { navController.popBackStack() })
                }

                composable(Routes.ADD_INGREDIENT) {
                    val vm: IngredientViewModel = hiltViewModel()

                    AddIngredientScreen(
                        initialName = null,
                        onSave = { name, quantity, unit, expiry ->
                            vm.addIngredient(name, quantity, unit, expiry)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
