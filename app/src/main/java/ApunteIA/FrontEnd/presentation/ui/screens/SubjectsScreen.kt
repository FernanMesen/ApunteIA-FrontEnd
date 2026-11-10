package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.viewmodel.OnboardingSubjectsViewModel
import ApunteIA.FrontEnd.presentation.ui.theme.lightOr

@Composable
fun SubjectsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: OnboardingSubjectsViewModel = hiltViewModel()
) {

    val isSaving by viewModel.isSaving.collectAsState()

    // Vacíos: antes venían ejemplos fijos en español; ahora se ve el placeholder traducido.
    var subject1 by remember {
        mutableStateOf("")
    }

    var subject2 by remember {
        mutableStateOf("")
    }

    var subject3 by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {

        OnboardingHeader(
            progress = 0.40f,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.subjects_studied_title),
            // En oscuro el primary (SpaceCadet) casi no se ve sobre el fondo.
            color = lightOr(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onBackground),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(22.dp))

        ApuntesTextField(
            value = subject1,
            onValueChange = {
                subject1 = it
            },
            placeholder = stringResource(R.string.subject_placeholder, 1)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ApuntesTextField(
            value = subject2,
            onValueChange = {
                subject2 = it
            },
            placeholder = stringResource(R.string.subject_placeholder, 2)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ApuntesTextField(
            value = subject3,
            onValueChange = {
                subject3 = it
            },
            placeholder = stringResource(R.string.subject_placeholder, 3)
        )

        Spacer(modifier = Modifier.height(24.dp))

        ApuntesPrimaryButton(
            text = stringResource(R.string.next_button),
            onClick = {
                if (!isSaving) viewModel.save(listOf(subject1, subject2, subject3), onNext)
            }
        )

        Spacer(modifier = Modifier.height(26.dp))
    }
}