package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.presentation.model.NoteSection
import ApunteIA.FrontEnd.presentation.ui.theme.BackgroundBeige
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.StatusRed
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray
import androidx.compose.foundation.horizontalScroll
import ApunteIA.FrontEnd.presentation.viewmodel.ScanViewModel
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ScanReviewScreen(
    viewModel: ScanViewModel,
    onClose: () -> Unit,
    onProcessAgain: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .padding(horizontal = 20.dp)
    ) {
        ScanTopBar(title = "Revisar nota", onClose = onClose)

        Spacer(modifier = Modifier.height(4.dp))

        // Tabs Generado / Original
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (!state.showingOriginal) DarkGreen else Color.Transparent,
                modifier = Modifier.clickable { viewModel.setShowingOriginal(false) }
            ) {
                Text(
                    text = "Generado",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                    color = if (!state.showingOriginal) Color.White else TextGray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent,
                modifier = Modifier.clickable { viewModel.setShowingOriginal(true) }
            ) {
                Text(
                    text = "Original",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                    color = if (state.showingOriginal) DarkGreen else TextGray,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.isProcessing) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = DarkGreen)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Generando tu nota...", color = TextGray, fontSize = 14.sp)
                }
            }
        } else if (state.showingOriginal) {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Text(
                        text = state.originalText,
                        modifier = Modifier.padding(16.dp),
                        color = DarkGreen,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                SectionLabel("TÍTULO")
                Spacer(modifier = Modifier.height(8.dp))
                ScanTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    placeholder = "Título de la nota"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Texto reconocido", fontWeight = FontWeight.Bold, color = DarkGreen, fontSize = 16.sp)
                    Text(
                        text = "+ Agregar sección",
                        color = DarkGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.addSection() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                state.sections.forEach { section ->
                    SectionCard(
                        section = section,
                        onTitleChange = { viewModel.onSectionTitleChange(section.id, it) },
                        onRemoveSection = { viewModel.removeSection(section.id) },
                        onBulletChange = { bulletId, text -> viewModel.onBulletTextChange(section.id, bulletId, text) },
                        onRemoveBullet = { bulletId -> viewModel.removeBullet(section.id, bulletId) },
                        onAddBullet = { viewModel.addBullet(section.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                SectionLabel("MATERIA")
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    state.subjects.forEach { subject ->
                        SubjectChip(
                            text = subject,
                            isSelected = state.subject == subject,
                            onClick = { viewModel.selectSubject(subject) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionLabel("ETIQUETAS")
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScanTextField(
                        value = state.tagInput,
                        onValueChange = viewModel::onTagInputChange,
                        placeholder = "Agregar etiqueta...",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.addTag() },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, DarkGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                        modifier = Modifier.height(58.dp)
                    ) {
                        Text("Agregar")
                    }
                }

                if (state.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(tag, color = DarkGreen, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Quitar etiqueta",
                                        tint = TextGray,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { viewModel.removeTag(tag) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (state.saveError != null) {
            Text(
                text = state.saveError!!,
                color = StatusRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onProcessAgain,
                enabled = !state.isSaving,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(27.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen)
            ) {
                Text("Procesar de nuevo", fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
            Button(
                onClick = { viewModel.saveNote() },
                enabled = !state.isProcessing && !state.isSaving,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Guardar nota", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    section: NoteSection,
    onTitleChange: (String) -> Unit,
    onRemoveSection: () -> Unit,
    onBulletChange: (Int, String) -> Unit,
    onRemoveBullet: (Int) -> Unit,
    onAddBullet: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InlineEditableText(
                    value = section.title,
                    onValueChange = onTitleChange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DarkGreen,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onRemoveSection,
                    modifier = Modifier.size(26.dp).background(Color(0xFFFDECEC), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Eliminar sección", tint = StatusRed, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            section.bullets.forEach { bullet ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("•  ", color = DarkGreen, fontSize = 15.sp)
                    InlineEditableText(
                        value = bullet.text,
                        onValueChange = { onBulletChange(bullet.id, it) },
                        fontSize = 14.sp,
                        color = TextGray,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Eliminar punto",
                        tint = Color(0xFFCCCCCC),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onRemoveBullet(bullet.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "+ Agregar punto",
                color = DarkGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onAddBullet() }
            )
        }
    }
}

/** Texto editable "in place" sin el look de un campo de formulario clásico. */
@Composable
private fun InlineEditableText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color
) {
    BasicTextFieldLine(value, onValueChange, modifier, fontSize, fontWeight, color)
}

@Composable
private fun BasicTextFieldLine(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    color: Color
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight
        ),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(DarkGreen)
    )
}