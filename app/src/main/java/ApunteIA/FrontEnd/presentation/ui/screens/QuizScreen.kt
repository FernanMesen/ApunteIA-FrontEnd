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
    val questions by viewModel.questions.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOptionIndex by viewModel.selectedOptionIndex.collectAsState()
    val isAnswered by viewModel.isAnswered.collectAsState()
    val showResults by viewModel.showResults.collectAsState()
    val userAnswers = viewModel.userAnswers

    if (showResults) {
        ResultsScreen(
            questions = questions,
            userAnswers = userAnswers.filterNotNull(),
            onNavigateToMain = onNavigateToMain,
            onScanMore = onScanMore,
            onRepeatIncorrects = { viewModel.repeatIncorrects() }
        )
    } else if (questions.isNotEmpty()) {
        val currentQuestion = questions[currentQuestionIndex]

        Scaffold(
            containerColor = BackgroundBeige,
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
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                        ) {
                            Text("Siguiente", color = Color.White, fontWeight = FontWeight.Bold)
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
                            .background(Color(0xFFEBEBE4))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = DarkGreen)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = DarkGreen,
                        trackColor = Color(0xFFEBEBE4)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "${currentQuestionIndex + 1}/${questions.size}",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Question
                Text(
                    text = currentQuestion.text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
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
                        isAnswered = isAnswered,
                        onClick = { viewModel.onOptionSelected(index) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isAnswered) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currentQuestion.explanation,
                        fontSize = 14.sp,
                        color = TextGray,
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
    onNavigateToMain: () -> Unit,
    onScanMore: () -> Unit,
    onRepeatIncorrects: () -> Unit
) {
    val correctCount = userAnswers.count { it.isCorrect }
    val incorrectCount = questions.size - correctCount
    val score = (correctCount.toFloat() / questions.size * 100).toInt()

    Scaffold(
        containerColor = BackgroundBeige,
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
                    if (incorrectCount > 0) {
                        Button(
                            onClick = onRepeatIncorrects,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = LightRed),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Repetir incorrectas ($incorrectCount)",
                                color = StatusRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    // Scan More Notes Button
                    Button(
                        onClick = onScanMore,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text(
                            text = "Escanear más notas",
                            color = TextDark,
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
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        text = "Volver al inicio",
                        color = Color.White,
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
                    color = DarkGreen,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Score card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tu nota",
                            fontSize = 16.sp,
                            color = TextGray
                        )
                        Text(
                            text = "$score%",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                        Text(
                            text = "$correctCount de ${questions.size} correctas",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Revisión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(questions) { index, question ->
                val userAnswer = userAnswers.getOrNull(index)
                ReviewItem(
                    question = question,
                    userAnswer = userAnswer,
                    index = index + 1
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
    index: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Text(
                    text = "$index.",
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = question.text,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            question.options.forEachIndexed { optIndex, option ->
                val isCorrect = optIndex == question.correctAnswerIndex
                val isSelected = userAnswer?.selectedOptionIndex == optIndex
                
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
                            isCorrect -> DarkGreen
                            isSelected -> StatusRed
                            else -> Color.Transparent
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        fontSize = 14.sp,
                        color = when {
                            isCorrect -> DarkGreen
                            isSelected && !isCorrect -> StatusRed
                            else -> TextGray
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
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !isAnswered && isSelected -> Color.White
        isAnswered && isCorrect -> LightGreen
        isAnswered && isSelected && !isCorrect -> LightRed
        else -> Color.White
    }

    val borderColor = when {
        !isAnswered && isSelected -> DarkGreen
        isAnswered && isCorrect -> DarkGreen
        isAnswered && isSelected && !isCorrect -> StatusRed
        else -> Color(0xFFEEEEEE)
    }

    val textColor = when {
        isAnswered && isCorrect -> DarkGreen
        isAnswered && isSelected && !isCorrect -> StatusRed
        isSelected -> DarkGreen
        else -> TextDark
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
