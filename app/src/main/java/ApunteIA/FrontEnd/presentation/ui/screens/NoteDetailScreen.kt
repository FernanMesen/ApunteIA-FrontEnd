package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.ProcessingStatus
import ApunteIA.FrontEnd.domain.util.IsoDates
import ApunteIA.FrontEnd.presentation.ui.components.StatusTag
import ApunteIA.FrontEnd.presentation.ui.theme.DarkAccent
import ApunteIA.FrontEnd.presentation.ui.theme.DarkThemeCard
import ApunteIA.FrontEnd.presentation.ui.theme.lightOr
import ApunteIA.FrontEnd.presentation.viewmodel.NoteDetailUiState
import ApunteIA.FrontEnd.presentation.viewmodel.NoteDetailViewModel
import ApunteIA.FrontEnd.presentation.ui.layout.ResponsiveContent

/**
 * Detalle de una nota guardada en el backend, con las acciones de IA:
 * resumir la nota y generar un quiz para practicar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    onBack: () -> Unit,
    onOpenQuiz: (Long) -> Unit,
    onDeleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.generatedQuizId) {
        uiState.generatedQuizId?.let { id ->
            viewModel.onQuizOpened()
            onOpenQuiz(id)
        }
    }
    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) onDeleted()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            // Oscuro: mismo fondo que los diálogos del Perfil; el botón Cancelar con texto legible.
            containerColor = lightOr(AlertDialogDefaults.containerColor, DarkThemeCard),
            title = { Text(stringResource(R.string.delete_note_title)) },
            text = { Text(stringResource(R.string.delete_note_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete()
                }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel), color = lightOr(MaterialTheme.colorScheme.primary, DarkAccent))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.note_detail_title),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    if (uiState.note != null) {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            enabled = !uiState.isBusy
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val note = uiState.note
        ResponsiveContent(
            modifier = Modifier.padding(paddingValues),
            maxWidth = 900.dp
        ) {
            when {
                uiState.isLoading && note == null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                note == null -> Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        uiState.error ?: stringResource(R.string.no_notes_found),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.load() }) { Text(stringResource(R.string.retry)) }
                }

                else -> NoteDetailContent(
                    note = note,
                    uiState = uiState,
                    modifier = Modifier,
                    onSummarize = { viewModel.summarize() },
                    onGenerateQuiz = { viewModel.generateQuiz() },
                    onDismissError = { viewModel.dismissActionError() }
                )
            }
        }
    }
}

@Composable
private fun NoteDetailContent(
    note: Note,
    uiState: NoteDetailUiState,
    modifier: Modifier,
    onSummarize: () -> Unit,
    onGenerateQuiz: () -> Unit,
    onDismissError: () -> Unit
) {
    val classNumText = if (note.classNumber > 0) stringResource(R.string.class_number, note.classNumber) else ""
    val formattedDate = IsoDates.shortDate(note.date)

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = note.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusTag(status = note.status)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = listOf(
                    classNumText,
                    note.className,
                    formattedDate
                ).filter { it.isNotBlank() }.joinToString("  ·  "),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        if (uiState.actionError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        uiState.actionError.orEmpty(),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 14.sp
                    )
                    TextButton(onClick = onDismissError, modifier = Modifier.align(Alignment.End)) {
                        Text(stringResource(R.string.understood))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle(stringResource(R.string.ai_summary_header))
        Spacer(modifier = Modifier.height(12.dp))

        val summary = note.summary
        when {
            uiState.isSummarizing -> InfoCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.ai_reading_note),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                }
            }

            !summary.isNullOrBlank() -> InfoCard { SummaryText(summary) }

            else -> InfoCard {
                Text(
                    text = when (note.processingStatus) {
                        ProcessingStatus.FAILED -> stringResource(R.string.ai_summary_failed)
                        ProcessingStatus.PROCESSING -> stringResource(R.string.ai_summary_processing)
                        else -> stringResource(R.string.ai_summary_none)
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                if (note.processingStatus != ProcessingStatus.PROCESSING) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onSummarize,
                        enabled = !uiState.isBusy,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.summarize_with_ai))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle(stringResource(R.string.original_note_header))
        Spacer(modifier = Modifier.height(12.dp))
        InfoCard {
            Text(
                text = note.content.ifBlank { stringResource(R.string.no_content) },
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onGenerateQuiz,
            enabled = !uiState.isBusy && note.content.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (uiState.isGeneratingQuiz) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(R.string.generating_questions), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(Icons.Default.Quiz, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.practice_quiz_button),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.quiz_description_hint),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

/** Muestra el resumen de la IA: encabezados en negrita y viñetas con punto. */
@Composable
fun SummaryText(summary: String) {
    val bulletPrefix = remember { Regex("^([-*•·–]|\\d+[.)])\\s+") }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        summary.lines().map { it.trim() }.filter { it.isNotEmpty() }.forEach { line ->
            val clean = line.replace("**", "")
            when {
                line.startsWith("#") -> Text(
                    text = clean.trimStart('#').trim(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 6.dp)
                )

                bulletPrefix.containsMatchIn(line) -> Row {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(6.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = clean.replace(bulletPrefix, ""),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }

                else -> Text(
                    text = clean,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
