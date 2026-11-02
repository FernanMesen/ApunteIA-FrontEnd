package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.domain.model.Assessment
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.QuizDifficulty
import ApunteIA.FrontEnd.presentation.ui.components.RefreshOnResume
import ApunteIA.FrontEnd.presentation.ui.components.StatusTag
import ApunteIA.FrontEnd.presentation.ui.components.TopHeader
import ApunteIA.FrontEnd.presentation.ui.layout.MainLayout
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.LightGreen
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet
import ApunteIA.FrontEnd.presentation.viewmodel.ReinforcementViewModel

/**
 * Pantalla "Reforzar": elegir una nota y configurar un quiz que genera la IA del
 * backend, o abrir uno de los quizzes ya existentes de tus materias.
 */
@Composable
fun ReinforcementScreen(
    viewModel: ReinforcementViewModel,
    onInicio: () -> Unit = {},
    onNotas: () -> Unit = {},
    onAuditoria: () -> Unit = {},
    onReforzar: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onStartQuiz: (Long) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    RefreshOnResume { viewModel.load() }

    LaunchedEffect(uiState.quizToOpen) {
        uiState.quizToOpen?.let { id ->
            viewModel.onQuizOpened()
            onStartQuiz(id)
        }
    }

    MainLayout(
        currentRoute = "Reforzar",
        onInicio = onInicio,
        onNotas = onNotas,
        onAuditoria = onAuditoria,
        onReforzar = onReforzar,
        onCameraClick = onCameraClick,
        topBar = { TopHeader(onProfileClick = onProfileClick) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF1E2532) else LightGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (isDark) Color.White else DarkGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.reinforce_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.reinforce_subtitle),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (uiState.error != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(uiState.error.orEmpty(), color = MaterialTheme.colorScheme.onErrorContainer)
                            Row(modifier = Modifier.align(Alignment.End)) {
                                TextButton(onClick = { viewModel.dismissError() }) { Text(stringResource(R.string.close)) }
                                TextButton(onClick = { viewModel.load() }) { Text(stringResource(R.string.retry)) }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                ReinforcementConfigSection(stringResource(R.string.select_note_label)) {
                    when {
                        uiState.isLoading && uiState.notes.isEmpty() -> Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(modifier = Modifier.size(28.dp)) }

                        uiState.visibleNotes.isEmpty() -> Text(
                            text = if (uiState.onlyWeak && uiState.notes.isNotEmpty()) {
                                stringResource(R.string.no_weak_topics)
                            } else {
                                stringResource(R.string.no_notes_for_quiz)
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )

                        else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.visibleNotes.take(MAX_NOTES).forEach { note ->
                                NoteOption(
                                    note = note,
                                    isSelected = note.id == uiState.selectedNoteId,
                                    onClick = { viewModel.selectNote(note.id) }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                ToggleOption(
                    text = stringResource(R.string.only_weak_topics),
                    checked = uiState.onlyWeak,
                    onToggle = { viewModel.toggleOnlyWeak() }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                ReinforcementConfigSection(stringResource(R.string.questions_count_label)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5, 10, 15).forEach { count ->
                            SegmentedOption(
                                text = "$count",
                                isSelected = uiState.questionCount == count,
                                onClick = { viewModel.selectQuestionCount(count) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                ReinforcementConfigSection(stringResource(R.string.difficulty_label)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuizDifficulty.entries.forEach { difficulty ->
                            val diffTextRes = when (difficulty) {
                                QuizDifficulty.EASY -> R.string.diff_easy
                                QuizDifficulty.MEDIUM -> R.string.diff_medium
                                QuizDifficulty.HARD -> R.string.diff_hard
                            }
                            SegmentedOption(
                                text = stringResource(diffTextRes),
                                isSelected = uiState.difficulty == difficulty,
                                onClick = { viewModel.selectDifficulty(difficulty) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.generate() },
                    enabled = uiState.canGenerate,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (uiState.isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.ai_creating_quiz), fontWeight = FontWeight.Bold)
                    } else {
                        Text(stringResource(R.string.generate_ai_quiz), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.quizzes.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.subject_quizzes_header),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                items(uiState.quizzes, key = { it.id }) { quiz ->
                    QuizRow(quiz = quiz, onClick = { viewModel.openQuiz(quiz.id.toLong()) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

private const val MAX_NOTES = 8

@Composable
private fun NoteOption(note: Note, isSelected: Boolean, onClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else if (isDark) Color(0xFF252B3B) else Color(0xFFEEEEEE)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = note.className,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusTag(status = note.status)
        }
    }
}

@Composable
private fun QuizRow(quiz: Assessment, onClick: () -> Unit) {
    val questionsText = stringResource(R.string.questions_plural, quiz.totalQuestions)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quiz.title,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = listOf(quiz.className, questionsText)
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (quiz.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ReinforcementConfigSection(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun SegmentedOption(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            if (isDark) Color(0xFF394867) else DarkGreen
        } else {
            if (isDark) Color(0xFF1E2532) else Color(0xFFF5F5F5)
        }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ToggleOption(text: String, checked: Boolean = false, onToggle: () -> Unit = {}) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF252B3B) else Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Switch(checked = checked, onCheckedChange = { onToggle() })
        }
    }
}
