package com.example.pokeapiMejorado.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokeapiMejorado.ui.screens.GameOverScreen
import com.example.pokeapiMejorado.ui.screens.GameScreen
import com.example.pokeapiMejorado.ui.screens.HomeScreen
import com.example.pokeapiMejorado.ui.state.PokeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PokeApiApp(viewModel: PokeViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PokeApiSreen.Home.name
    ) {
        composable(PokeApiSreen.Home.name) {
            HomeScreen(
                viewModel = viewModel,
                onStartGame = { generationId, typeName, questionCount ->
                    // Navegar a la pantalla de juego con los parámetros proporcionados
                    navController.navigate("${PokeApiSreen.Game.name}/$generationId/$typeName/$questionCount")
                },
                onNavigateToGameOver = {
                    // Navegar a GameOver cuando se llama
                    navController.navigate(PokeApiSreen.GameOver.name)
                }
            )
        }

        composable(
            route = "${PokeApiSreen.Game.name}/{generationId}/{typeName}/{questionCount}",
            arguments = listOf(
                navArgument("generationId") { type = NavType.IntType },
                navArgument("typeName") { type = NavType.StringType },
                navArgument("questionCount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val generationId = backStackEntry.arguments?.getInt("generationId") ?: 1
            val typeName = backStackEntry.arguments?.getString("typeName") ?: "normal"
            val questionCount = backStackEntry.arguments?.getInt("questionCount") ?: 10

            GameScreen(
                viewModel = viewModel,
                generationId = generationId,
                typeName = typeName,
                questionCount = questionCount,
                onGameOver = {
                    // Actualiza el récord cuando el juego termina
                    viewModel.updateRecordAndNavigate(navController)
                    // Navegar a la pantalla GameOver de forma inmediata
                    navController.navigate(PokeApiSreen.GameOver.name) {
                        popUpTo(PokeApiSreen.Game.name) { inclusive = true } // Limpiar la pila
                    }
                },
                navController = navController
            )
        }

        composable(PokeApiSreen.GameOver.name) {
            val generationId = viewModel.currentGenerationId
            val typeName = viewModel.currentTypeName
            val questionCount = viewModel.totalQuestions

            GameOverScreen(
                viewModel = viewModel,
                generationId = generationId,
                typeName = typeName,
                questionCount = questionCount,
                onHome = {
                    // Limpiar la pila y navegar a la pantalla de inicio
                    navController.navigate(PokeApiSreen.Home.name) {
                        popUpTo(PokeApiSreen.GameOver.name) { inclusive = true }
                        launchSingleTop = true // Asegura que no haya duplicados
                    }
                },
                onReplay = {
                    // Reiniciar el juego con los parámetros actuales
                    viewModel.startGame(generationId, typeName, questionCount)
                    navController.navigate("${PokeApiSreen.Game.name}/$generationId/$typeName/$questionCount") {
                        popUpTo(PokeApiSreen.GameOver.name) { inclusive = true }
                    }
                }
            )
        }
    }
}
