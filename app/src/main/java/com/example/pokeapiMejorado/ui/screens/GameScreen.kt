package com.example.pokeapiMejorado.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.pokeapiMejorado.ui.state.PokeUiState
import com.example.pokeapiMejorado.ui.state.PokeViewModel

@Composable
fun GameScreen(
    viewModel: PokeViewModel,
    generationId: Int,
    typeName: String,
    questionCount: Int,
    onGameOver: () -> Unit,
    navController: NavController
) {
    LaunchedEffect(questionCount) {
        if (viewModel.questions.isEmpty()) {
            viewModel.startGame(generationId, typeName, questionCount)
        }
    }

    val pokeUiState = viewModel.pokeUiState.value

    when (pokeUiState) {
        is PokeUiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(text = "Cargando preguntas...", style = MaterialTheme.typography.bodyLarge)
            }
        }
        is PokeUiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error al cargar las preguntas. Por favor, intenta nuevamente.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }
        }
        is PokeUiState.Success -> {
            val questions = viewModel.questions
            val currentQuestionIndex = viewModel.currentQuestionIndex
            val question = questions.getOrNull(currentQuestionIndex)

            if (question == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error al cargar la pregunta. Por favor, reinicia el juego.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red
                    )
                }
                return
            }

            val currentImage = question.spriteUrl

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                // Mostrar el número de la pregunta
                Text(text = "Pregunta ${currentQuestionIndex + 1}/$questionCount")

                // Mostrar la pregunta
                Text(text = question.question, style = MaterialTheme.typography.bodyLarge)

                currentImage?.let {
                    Image(
                        painter = rememberImagePainter(
                            data = it,
                            builder = {
                                crossfade(true)
                            }
                        ),
                        contentDescription = "Imagen de Pokémon",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp) // Ajuste de altura de la imagen para hacerla más pequeña
                            .padding(bottom = 8.dp)
                    )
                }

                val shuffledOptions = remember(question) { (question.incorrect_answers + question.correct_answer).shuffled() }

                var selectedAnswerIndex by remember { mutableStateOf(-1) }
                var showResult by remember { mutableStateOf(false) }

                shuffledOptions.forEachIndexed { index, option ->

                    val backgroundColor = when {
                        !showResult -> MaterialTheme.colorScheme.surface
                        index == shuffledOptions.indexOf(question.correct_answer) -> Color.Green
                        index == selectedAnswerIndex -> Color.Red
                        else -> Color.Gray
                    }

                    val borderColor = when {
                        !showResult -> MaterialTheme.colorScheme.surface
                        index == shuffledOptions.indexOf(question.correct_answer) -> Color.Green
                        index == selectedAnswerIndex -> Color.Red
                        else -> Color.Black
                    }

                    val textColor = when {
                        index == selectedAnswerIndex && index != shuffledOptions.indexOf(question.correct_answer) -> Color.White
                        else -> Color.Black
                    }

                    Button(
                        onClick = {
                            if (!showResult) {
                                selectedAnswerIndex = index
                                showResult = true
                                viewModel.submitAnswer(index, shuffledOptions)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(backgroundColor, RoundedCornerShape(8.dp))
                            .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
                        enabled = !showResult
                    ) {
                        Text(text = option, color = textColor)
                    }
                }

                if (showResult) {
                    Text(
                        text = if (selectedAnswerIndex == shuffledOptions.indexOf(question.correct_answer))
                            "¡Correcto!"
                        else
                            "Incorrecto. La respuesta correcta es: ${question.correct_answer}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = {
                            if (currentQuestionIndex + 1 >= questionCount) {
                                viewModel.updateRecordAndNavigate(navController)
                                navController.navigate("gameOver") {
                                    popUpTo("gameScreen") { inclusive = true }
                                }
                                onGameOver()
                            } else {
                                viewModel.moveToNextQuestion(navController)
                                selectedAnswerIndex = -1
                                showResult = false
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp),
                        enabled = showResult
                    ) {
                        Text(text = "Siguiente")
                    }
                }

                Text(text = "Puntuación: ${viewModel.score}/$questionCount")
            }
        }
        else -> {
            Text(text = "Estado desconocido", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
