package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SubjectsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    var subject1 by remember {
        mutableStateOf("Química Orgánica")
    }

    var subject2 by remember {
        mutableStateOf("Biología Celular")
    }

    var subject3 by remember {
        mutableStateOf("Cálculo II")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {

        OnboardingHeader(
            progress = 0.40f,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Materias que estudias",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(22.dp))

        ApuntesTextField(
            value = subject1,
            onValueChange = {
                subject1 = it
            },
            placeholder = "Materia 1"
        )

        Spacer(modifier = Modifier.height(12.dp))

        ApuntesTextField(
            value = subject2,
            onValueChange = {
                subject2 = it
            },
            placeholder = "Materia 2"
        )

        Spacer(modifier = Modifier.height(12.dp))

        ApuntesTextField(
            value = subject3,
            onValueChange = {
                subject3 = it
            },
            placeholder = "Materia 3"
        )

        Spacer(modifier = Modifier.weight(1f))

        ApuntesPrimaryButton(
            text = "Siguiente",
            onClick = onNext
        )

        Spacer(modifier = Modifier.height(26.dp))
    }
}

