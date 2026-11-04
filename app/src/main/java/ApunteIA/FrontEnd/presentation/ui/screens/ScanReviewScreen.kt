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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.model.NoteSection
import androidx.compose.ui.text.style.TextOverflow
import ApunteIA.FrontEnd.presentation.ui.theme.DarkDisabled
import ApunteIA.FrontEnd.presentation.ui.theme.StatusRed
import ApunteIA.FrontEnd.presentation.ui.theme.lightOr
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
            .background(scanBackground())
            .padding(horizontal = 20.dp)
    ) {
        ScanTopBar(title = stringResource(R.string.review_note_title), onClose = onClose)

        Spacer(modifier = Modifier.height(4.dp))

        // Tabs Generado / Original: las dos pestañas se marcan igual (antes "Original" no se rellenaba).
        Row(verticalAlignment = Alignment.CenterVertically) {
            ReviewTab(
                text = stringResource(R.string.generated_tab),
                selected = !state.showingOriginal,
                onClick = { viewModel.setShowingOriginal(false) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            ReviewTab(
                text = stringResource(R.string.original_tab),
                selected = state.showingOriginal,
                onClick = { viewModel.setShowingOriginal(true) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.isProcessing) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = scanAccent())
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(stringResource(R.string.generating_your_note), color = scanMuted(), fontSize = 14.sp)
                }
            }
        } else if (state.showingOriginal) {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = scanCard(),
                    border = BorderStroke(1.dp, scanBorder(Color(0xFFEEEEEE)))
                ) {
                    Text(
                        text = state.originalText,
                        modifier = Modifier.padding(16.dp),
                        color = scanText(),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                SectionLabel(stringResource(R.string.note_title_header))
                Spacer(modifier = Modifier.height(8.dp))
                ScanTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    placeholder = stringResource(R.string.note_title_placeholder)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.recognized_text_header),
                        fontWeight = FontWeight.Bold,
                        color = scanText(),
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.add_section_action),
                        color = scanAccent(),
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

                SectionLabel(stringResource(R.string.subject_header))
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

                SectionLabel(stringResource(R.string.tags_header))
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScanTextField(
                        value = state.tagInput,
                        onValueChange = viewModel::onTagInputChange,
                        placeholder = stringResource(R.string.add_tag_placeholder),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.addTag() },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, scanAccent()),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = scanAccent()),
                        modifier = Modifier.height(58.dp)
                    ) {
                        Text(stringResource(R.string.add_button), maxLines = 1)
                    }
                }

                if (state.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    // horizontalScroll: con muchas etiquetas ya no se salen de la pantalla.
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        state.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = scanCard(),
                                border = BorderStroke(1.dp, scanBorder())
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(tag, color = scanText(), fontSize = 13.sp, maxLines = 1)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = stringResource(R.string.delete),
                                        tint = scanMuted(),
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
                border = BorderStroke(1.dp, scanBorder()),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = scanText())
            ) {
                Text(
                    stringResource(R.string.process_again),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Button(
                onClick = { viewModel.saveNote() },
                enabled = !state.isProcessing && !state.isSaving,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = scanButton())
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        stringResource(R.string.save_note),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
        color = scanCard(),
        border = BorderStroke(1.dp, scanBorder(Color(0xFFF0F0F0)))
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
                    color = scanText(),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onRemoveSection,
                    modifier = Modifier.size(26.dp).background(lightOr(Color(0xFFFDECEC), Color(0xFF3B2525)), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.delete_section), tint = StatusRed, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            section.bullets.forEach { bullet ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("•  ", color = scanText(), fontSize = 15.sp)
                    InlineEditableText(
                        value = bullet.text,
                        onValueChange = { onBulletChange(bullet.id, it) },
                        fontSize = 14.sp,
                        color = scanMuted(),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.delete_bullet),
                        tint = lightOr(Color(0xFFCCCCCC), DarkDisabled),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onRemoveBullet(bullet.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.add_bullet_action),
                color = scanAccent(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onAddBullet() }
            )
        }
    }
}

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
        cursorBrush = androidx.compose.ui.graphics.SolidColor(scanAccent())
    )
}

/** Pestaña Generado / Original: rellena cuando está seleccionada, igual para las dos. */
@Composable
private fun ReviewTab(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) scanButton() else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
            color = if (selected) Color.White else scanMuted(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}
