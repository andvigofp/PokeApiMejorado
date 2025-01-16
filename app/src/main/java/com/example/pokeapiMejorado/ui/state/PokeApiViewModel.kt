package com.example.pokeapiMejorado.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.pokeapiMejorado.model.Question
import com.example.pokeapiMejorado.ui.navigation.PokeApiSreen


class PokeViewModel : ViewModel() {

    // Estados del UI
    private val _pokeUiState: MutableState<PokeUiState> = mutableStateOf(PokeUiState.Loading)
    val pokeUiState: State<PokeUiState> get() = _pokeUiState

    private val _questions = mutableStateListOf<Question>()
    val questions: List<Question> get() = _questions

    private val _totalQuestions = mutableStateOf(0)
    val totalQuestions: Int get() = _totalQuestions.value

    private val _currentQuestionIndex = mutableStateOf(0)
    val currentQuestionIndex: Int get() = _currentQuestionIndex.value

    private val _score = mutableStateOf(0)
    val score: Int get() = _score.value

    private val _record = mutableStateOf(0)
    val record: Int get() = _record.value

    private val _gameOver = mutableStateOf(false)
    val gameOver: Boolean get() = _gameOver.value

    private val _answerShown = mutableStateOf(false)
    val answerShown: Boolean get() = _answerShown.value

    private val _currentGenerationId = mutableStateOf(1)
    val currentGenerationId: Int get() = _currentGenerationId.value

    private val _currentTypeName = mutableStateOf("normal")
    val currentTypeName: String get() = _currentTypeName.value

    // Nueva lista para manejar las imágenes de los Pokémon
    private val _images = mutableStateListOf<String>() // URLs de las imágenes
    val images: List<String> get() = _images

    // Añadimos el nuevo MutableState para el sprite actual
    // Estado para la imagen actual
    private val _currentSprite = mutableStateOf<String?>(null)
    val currentSprite: State<String?> get() = _currentSprite

    private val gameFunctions = GameFunctions(this)
    private val uiFunctions = UiFunctions(this)

    // Métodos accesibles para GameFunctions

    // Método de inicio de juego
    fun startGame(generationId: Int, typeName: String, questionCount: Int) {
        _currentGenerationId.value = generationId
        _currentTypeName.value = typeName
        gameFunctions.startGame(generationId, questionCount)
    }

    fun moveToNextQuestion(navController: NavController) {
        gameFunctions.moveToNextQuestion(navController)
    }

    fun submitAnswer(selectedIndex: Int, shuffledOptions: List<String>) {
        val currentQuestion = questions[currentQuestionIndex]
        if (!answerShown) {
            val correctAnswers = currentQuestion.correct_answer
            val selectedAnswer = shuffledOptions[selectedIndex]
            if (correctAnswers.contains(selectedAnswer)) {
                updateScore(score + 1)
            }
            setAnswerShown(true)
        }
    }

    fun setTotalQuestions(value: Int) {
        _totalQuestions.value = value
    }

    fun updateCurrentQuestionIndex(value: Int) {
        _currentQuestionIndex.value = value
    }

    fun updateScore(value: Int) {
        _score.value = value
    }

    fun setGameOver(value: Boolean) {
        _gameOver.value = value
    }

    fun setAnswerShown(value: Boolean) {
        _answerShown.value = value
    }

    fun clearQuestions() {
        _questions.clear()
        _images.clear() // Limpiar las imágenes al reiniciar
    }

    fun setPokeUiState(state: PokeUiState) {
        _pokeUiState.value = state
    }

    fun fetchQuestions(generationId: Int, questionCount: Int) {
        uiFunctions.fetchQuestions(generationId, questionCount)
    }

    fun addQuestions(newQuestions: List<Question>) {
        _questions.addAll(newQuestions)
    }

    // Nuevo método para agregar imágenes de Pokémon
    fun addImages(newImages: List<String>) {
        _images.addAll(newImages)
    }

    // Actualizar el sprite actual
    // Actualiza el sprite actual
    fun updateCurrentSprite(spriteUrl: String?) {
        _currentSprite.value = spriteUrl
    }

    fun checkAndUpdateRecord(percentage: Int) {
        if (percentage > _record.value) {
            _record.value = percentage
        }
    }

    fun updateRecord(percentage: Int) {
        checkAndUpdateRecord(percentage)
    }

    fun endGame() {
        _pokeUiState.value = PokeUiState.GameOver
    }

    fun updateRecordAndNavigate(navController: NavController) {
        val percentage = (score * 100) / totalQuestions
        if (percentage > record) {
            updateRecord(percentage)
        }
        if (_pokeUiState.value != PokeUiState.GameOver) {
            endGame()
            navController.navigate(PokeApiSreen.GameOver.name) {
                popUpTo(PokeApiSreen.Game.name) { inclusive = true }
                launchSingleTop = true
            }
        }
    }


}
