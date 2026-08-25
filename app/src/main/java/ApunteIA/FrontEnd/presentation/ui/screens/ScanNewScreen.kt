package ApunteIA.FrontEnd.presentation.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.presentation.model.MaterialType
import ApunteIA.FrontEnd.presentation.ui.theme.BackgroundBeige
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray
import ApunteIA.FrontEnd.presentation.viewmodel.SCAN_SUBJECTS
import ApunteIA.FrontEnd.presentation.viewmodel.ScanViewModel
import java.util.Calendar

@Composable
fun ScanNewScreen(
    viewModel: ScanViewModel,
    onClose: () -> Unit,
    onContinue: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .padding(horizontal = 20.dp)
    ) {
        ScanTopBar(title = "Nuevo escaneo", onClose = onClose)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            SectionLabel("MATERIA")
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SCAN_SUBJECTS.forEach { subject ->
                    SubjectChip(
                        text = subject,
                        isSelected = state.subject == subject,
                        onClick = { viewModel.selectSubject(subject) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel("NOMBRE O NÚMERO DE CLASE")
            Spacer(modifier = Modifier.height(10.dp))
            ScanTextField(
                value = state.className,
                onValueChange = viewModel::onClassNameChange,
                placeholder = "ej. Clase 7"
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel("FECHA")
            Spacer(modifier = Modifier.height(10.dp))
            ScanTextField(
                value = state.date,
                onValueChange = {},
                placeholder = "mm/dd/yyyy",
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextGray)
                },
                modifier = Modifier.clickable {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            viewModel.onDateChange(
                                "%02d/%02d/%04d".format(month + 1, dayOfMonth, year)
                            )
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel("TIPO DE MATERIAL")
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MaterialTypeChip(
                    text = MaterialType.HANDWRITTEN.label,
                    isSelected = state.materialType == MaterialType.HANDWRITTEN,
                    onClick = { viewModel.selectMaterialType(MaterialType.HANDWRITTEN) },
                    modifier = Modifier.weight(1f)
                )
                MaterialTypeChip(
                    text = MaterialType.BOOK_PAGES.label,
                    isSelected = state.materialType == MaterialType.BOOK_PAGES,
                    onClick = { viewModel.selectMaterialType(MaterialType.BOOK_PAGES) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MaterialTypeChip(
                    text = MaterialType.SLIDES.label,
                    isSelected = state.materialType == MaterialType.SLIDES,
                    onClick = { viewModel.selectMaterialType(MaterialType.SLIDES) },
                    modifier = Modifier.weight(1f)
                )
                MaterialTypeChip(
                    text = MaterialType.WHITEBOARD.label,
                    isSelected = state.materialType == MaterialType.WHITEBOARD,
                    onClick = { viewModel.selectMaterialType(MaterialType.WHITEBOARD) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Escaneo de varias páginas",
                    color = DarkGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = state.multiPageScan,
                    onCheckedChange = viewModel::toggleMultiPageScan,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = DarkGreen
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            onClick = onContinue,
            enabled = state.canContinueFromNewScan,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen,
                disabledContainerColor = DarkGreen.copy(alpha = 0.4f)
            )
        ) {
            Text("Continuar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextGray,
        letterSpacing = 0.5.sp
    )
}

@Composable
fun ScanTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextGray) },
        readOnly = readOnly,
        singleLine = true,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = DarkGreen,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            cursorColor = DarkGreen
        )
    )
}