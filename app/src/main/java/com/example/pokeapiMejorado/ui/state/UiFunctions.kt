package com.example.pokeapiMejorado.ui.state

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.pokeapiMejorado.model.Question
import com.example.pokeapiMejorado.model.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class UiFunctions(private val viewModel: PokeViewModel) {

    fun fetchQuestions(generationId: Int, questionCount: Int) {
        viewModel.viewModelScope.launch {
            viewModel.setPokeUiState(PokeUiState.Loading)
            try {
                // Llamada a la API para obtener los Pokémon por generación
                val generationResponse =
                    RetrofitClient.apiService.getPokemonByGeneration(generationId)

                // Extraer los nombres de los Pokémon de la respuesta
                val pokemonNames = generationResponse.pokemon_species.map { it.name }

                // Generar preguntas basadas en los Pokémon obtenidos
                val questions = generateQuestions(pokemonNames, questionCount)
                if (questions.isNotEmpty()) {
                    viewModel.clearQuestions()
                    viewModel.addQuestions(questions)
                    viewModel.setTotalQuestions(questions.size)
                    viewModel.setPokeUiState(PokeUiState.Success(viewModel.questions))
                } else {
                    viewModel.setPokeUiState(PokeUiState.Error("No questions generated"))
                }
            } catch (e: Exception) {
                // Manejo de errores generales en la llamada a la API
                viewModel.setPokeUiState(PokeUiState.Error("Error fetching questions: ${e.message}"))
            }
        }
    }

    private suspend fun generateQuestions(
        pokemonNames: List<String>,
        questionCount: Int
    ): List<Question> {
        val questions = mutableListOf<Question>()

        // Seleccionar Pokémon al azar
        val selectedPokemon = pokemonNames.shuffled().take(questionCount)

        // Obtener los detalles de cada Pokémon
        val deferredQuestions = selectedPokemon.map { pokemonName ->
            viewModel.viewModelScope.async {
                try {
                    // Obtener detalles del Pokémon
                    val pokemonDetails = RetrofitClient.apiService.getPokemonByName(pokemonName)

                    pokemonDetails?.let {
                        // URL de la imagen oficial del Pokémon
                        val spriteUrl =
                            it.sprites.front_default  // Usa spriteUrl en lugar de imageUrl

                        // Generar opciones incorrectas (otros nombres al azar)
                        val incorrectOptions = pokemonNames.filter { name -> name != pokemonName }
                            .shuffled()
                            .take(3)

                        // Crear una pregunta donde la respuesta correcta sea el nombre del Pokémon
                        return@async Question(
                            question = "¿Cuál es el nombre de este Pokémon?",
                            spriteUrl = spriteUrl,  // Usa spriteUrl aquí
                            correct_answer = pokemonName,
                            incorrect_answers = incorrectOptions
                        )
                    } ?: run { null }
                } catch (e: Exception) {
                    Log.e(
                        "generateQuestions",
                        "Error fetching details for $pokemonName: ${e.message}"
                    )
                    null
                }
            }
        }

        // Esperar todas las preguntas
        val generatedQuestions = deferredQuestions.awaitAll().filterNotNull()
        questions.addAll(generatedQuestions)

        return questions
    }
}