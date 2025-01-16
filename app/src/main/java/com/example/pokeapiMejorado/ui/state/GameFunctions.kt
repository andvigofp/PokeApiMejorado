package com.example.pokeapiMejorado.ui.state

import androidx.navigation.NavController
import com.example.pokeapiMejorado.model.Question

class GameFunctions(private val viewModel: PokeViewModel) {

    // Inicia el juego con un número específico de preguntas y un ID de generación
    fun startGame(generationId: Int, questionCount: Int) {
        viewModel.setTotalQuestions(questionCount)
        viewModel.fetchQuestions(
            generationId,
            questionCount
        )  // Carga preguntas (incluidas imágenes)
        viewModel.setGameOver(false)
        viewModel.setAnswerShown(false)
        viewModel.updateCurrentQuestionIndex(0)
        viewModel.updateScore(0)
        viewModel.setPokeUiState(PokeUiState.Playing)  // Cambiar el estado a Playing
    }

    // Obtiene el índice de la respuesta correcta
    fun getCorrectAnswerIndex(question: Question, shuffledOptions: List<String>): Int {
        return shuffledOptions.indexOf(question.correct_answer)
    }

    // Envía la respuesta seleccionada por el usuario
    fun submitAnswer(selectedIndex: Int, shuffledOptions: List<String>) {
        val currentQuestion = viewModel.questions[viewModel.currentQuestionIndex]

        if (!viewModel.answerShown) {
            val correctAnswerIndex = getCorrectAnswerIndex(currentQuestion, shuffledOptions)
            if (selectedIndex == correctAnswerIndex) {
                viewModel.updateScore(viewModel.score + 1)
            }
            viewModel.setAnswerShown(true)
        }
    }

    // Avanza a la siguiente pregunta o finaliza el juego
    fun moveToNextQuestion(navController: NavController) {
        if (viewModel.currentQuestionIndex < viewModel.questions.lastIndex) {
            viewModel.updateCurrentQuestionIndex(viewModel.currentQuestionIndex + 1)
        } else {
            viewModel.setGameOver(true)

            // Calcular el porcentaje final
            val percentage = (viewModel.score * 100) / viewModel.totalQuestions

            // Actualizar el récord y navegar a Game Over
            viewModel.checkAndUpdateRecord(percentage)
            viewModel.updateRecordAndNavigate(navController)  // Pasamos el navController aquí
        }

        // Actualizar la imagen para la nueva pregunta
        updateCurrentQuestionSprite()  // Llamamos directamente a esta función para actualizar la imagen

        viewModel.setAnswerShown(false)
    }

    // Actualiza la imagen de la pregunta actual
    private fun updateCurrentQuestionSprite() {
        val currentQuestion = viewModel.questions[viewModel.currentQuestionIndex]
        viewModel.updateCurrentSprite(currentQuestion.spriteUrl)  // Usamos imageUrl en lugar de sprites.front_default
    }
}