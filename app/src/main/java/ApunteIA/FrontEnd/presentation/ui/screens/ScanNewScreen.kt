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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.model.MaterialType
import ApunteIA.FrontEnd.presentation.ui.theme.BackgroundBeige
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray
import ApunteIA.FrontEnd.presentation.ui.theme.StatusRed
import androidx.compose.foundation.horizontalScroll
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
        ScanTopBar(title = stringResource(R.string.new_scan_title), onClose = onClose)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
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
            if (state.isLoadingSubjects) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(color = DarkGreen, modifier = Modifier.fillMaxWidth())
            }
            state.subjectsError?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = message, color = StatusRed, fontSize = 13.sp)
                Text(
                    text = stringResource(R.string.retry),
                    color = DarkGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { viewModel.loadSubjects() }.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel(stringResource(R.string.class_name_number_header))
            Spacer(modifier = Modifier.height(10.dp))
            ScanTextField(
                value = state.className,
                onValueChange = viewModel::onClassNameChange,
                placeholder = stringResource(R.string.class_name_placeholder)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel(stringResource(R.string.date_header))
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

            SectionLabel(stringResource(R.string.material_type_header))
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MaterialTypeChip(
                    text = stringResource(R.string.mat_handwritten),
                    isSelected = state.materialType == MaterialType.HANDWRITTEN,
                    onClick = { viewModel.selectMaterialType(MaterialType.HANDWRITTEN) },
                    modifier = Modifier.weight(1f)
                )
                MaterialTypeChip(
                    text = stringResource(R.string.mat_book_pages),
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
                    text = stringResource(R.string.mat_slides),
                    isSelected = state.materialType == MaterialType.SLIDES,
                    onClick = { viewModel.selectMaterialType(MaterialType.SLIDES) },
                    modifier = Modifier.weight(1f)
                )
                MaterialTypeChip(
                    text = stringResource(R.string.mat_whiteboard),
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
                    text = stringResource(R.string.multipage_scan_option),
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
            Text(stringResource(R.string.continue_action), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
