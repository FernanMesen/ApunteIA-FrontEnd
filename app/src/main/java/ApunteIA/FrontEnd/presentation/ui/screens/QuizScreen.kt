package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.model.UserAnswer
import ApunteIA.FrontEnd.presentation.ui.theme.*
import ApunteIA.FrontEnd.presentation.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onClose: () -> Unit,
    onNavigateToMain: () -> Unit,
    onScanMore: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOptionIndex by viewModel.selectedOptionIndex.collectAsState()
    val isAnswered by viewModel.isAnswered.collectAsState()
    val showResults by viewModel.showResults.collectAsState()
    val userAnswers = viewModel.userAnswers

    val isDark = MaterialTheme.colorScheme.background != BackgroundBeige

    if (uiState.isLoading || uiState.isSubmitting) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    if (uiState.isSubmitting) "Calificando tus respuestas..." else "Preparando el cuestionario...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else if (uiState.error != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No se pudo completar el cuestionario: ${uiState.error}",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onClose) {
                            Text("Cerrar")
                        }
                        Button(
                            onClick = { viewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    } else if (showResults) {
        ResultsScreen(
            questions = questions,
            userAnswers = userAnswers.filterNotNull(),
            isDarkMode = isDark,
            onNavigateToMain = onNavigateToMain,
            onScanMore = onScanMore,
            onRepeatIncorrects = if (uiState.canRepeat) ({ viewModel.repeatIncorrects() }) else null
        )
    } else if (questions.isNotEmpty()) {
        val currentQuestion = questions[currentQuestionIndex.coerceIn(0, questions.lastIndex)]
        // Durante la evaluación real el backend no revela la respuesta correcta (-1):
        // se elige una opción y se califica al entregar.
        val revealsAnswer = currentQuestion.correctAnswerIndex >= 0
        val isLastQuestion = currentQuestionIndex >= questions.lastIndex

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (isAnswered) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                        ) {
                            Text(
                                when {
                                    isLastQuestion && !revealsAnswer -> "Entregar"
                                    isLastQuestion -> "Ver resultados"
                                    else -> "Siguiente"
                                },
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Header: Close button and Progress bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "${currentQuestionIndex + 1}/${questions.size}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (uiState.title.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.title,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Question
                Text(
                    text = currentQuestion.text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Options
                currentQuestion.options.forEachIndexed { index, option ->
                    val isCorrect = index == currentQuestion.correctAnswerIndex
                    val isSelected = selectedOptionIndex == index

                    QuizOptionCard(
                        text = option,
                        isSelected = isSelected,
                        isCorrect = isCorrect,
                        isAnswered = isAnswered && revealsAnswer,
                        isDark = isDark,
                        onClick = { viewModel.onOptionSelected(index) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isAnswered && revealsAnswer && currentQuestion.explanation.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currentQuestion.explanation,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ResultsScreen(
    questions: List<Question>,
    userAnswers: List<UserAnswer>,
    isDarkMode: Boolean,
    onNavigateToMain: () -> Unit,
    onScanMore: () -> Unit,
    onRepeatIncorrects: (() -> Unit)?
) {
    val correctCount = userAnswers.count { it.isCorrect }
    val incorrectCount = questions.size - correctCount
    val score = if (questions.isNotEmpty()) (correctCount.toFloat() / questions.size * 100).toInt() else 0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Repeat Incorrects Button
                    if (incorrectCount > 0 && onRepeatIncorrects != null) {
                        Button(
                            onClick = onRepeatIncorrects,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkMode) Color(0xFF3B2E2E) else LightRed
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Repetir incorrectas ($incorrectCount)",
                                color = if (isDarkMode) Color(0xFFF44336) else StatusRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Scan More Notes Button
                    Button(
                        onClick = onScanMore,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text(
                            text = "Escanear más notas",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Back to Start Button
                Button(
                    onClick = onNavigateToMain,
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        text = "Volver al inicio",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "¡Quiz Finalizado!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Score card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tu nota",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$score%",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "$correctCount de ${questions.size} correctas",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Revisión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(questions) { index, question ->
                val userAnswer = userAnswers.getOrNull(index)
                ReviewItem(
                    question = question,
                    userAnswer = userAnswer,
                    index = index + 1,
                    isDark = isDarkMode
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ReviewItem(
    question: Question,
    userAnswer: UserAnswer?,
    index: Int,
    isDark: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Text(
                    text = "$index.",
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = question.text,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            question.options.forEachIndexed { optIndex, option ->
                val isSelected = userAnswer?.selectedOptionIndex == optIndex
                // Si el backend no envió la opción correcta, solo se sabe si la elegida acertó.
                val isCorrect = if (question.correctAnswerIndex >= 0) {
                    optIndex == question.correctAnswerIndex
                } else {
                    isSelected && userAnswer?.isCorrect == true
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = when {
                            isCorrect -> if (isDark) LimeGreen else DarkGreen
                            isSelected -> if (isDark) Color(0xFFFF8A80) else StatusRed
                            else -> Color.Transparent
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        fontSize = 14.sp,
                        color = when {
                            isCorrect -> if (isDark) LimeGreen else DarkGreen
                            isSelected && !isCorrect -> if (isDark) Color(0xFFFF8A80) else StatusRed
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (isCorrect || isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun QuizOptionCard(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isAnswered: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !isAnswered && isSelected -> MaterialTheme.colorScheme.surfaceVariant
        isAnswered && isCorrect -> if (isDark) Color(0xFF1B311B) else LightGreen
        isAnswered && isSelected && !isCorrect -> if (isDark) Color(0xFF3B1F1F) else LightRed
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when {
        !isAnswered && isSelected -> MaterialTheme.colorScheme.primary
        isAnswered && isCorrect -> if (isDark) LimeGreen else DarkGreen
        isAnswered && isSelected && !isCorrect -> if (isDark) Color(0xFFFF8A80) else StatusRed
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    }

    val textColor = when {
        isAnswered && isCorrect -> if (isDark) LimeGreen else DarkGreen
        isAnswered && isSelected && !isCorrect -> if (isDark) Color(0xFFFF8A80) else StatusRed
        isSelected -> if (isDark) Color.White else MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isAnswered) { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected || (isAnswered && isCorrect)) 2.dp else 1.dp,
            color = borderColor
        ),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                color = textColor,
                fontWeight = if (isSelected || (isAnswered && isCorrect)) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}