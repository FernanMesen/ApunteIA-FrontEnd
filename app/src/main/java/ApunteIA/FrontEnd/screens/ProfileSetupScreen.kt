package ApunteIA.FrontEnd.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background

@Composable
fun ProfileSetupScreen(
    onNext: () -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var educationLevel by remember {
        mutableStateOf("Bachillerato")
    }

    var semester by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApuntesBackground)
            .padding(horizontal = 24.dp)
    ) {

        OnboardingHeader(
            progress = 0.20f,
            showBack = false
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Configuremos tu perfil",
            color = ApuntesGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "TU NOMBRE",
            color = ApuntesGreen,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        ApuntesTextField(
            value = name,
            onValueChange = {
                name = it
            },
            placeholder = "Nombre completo"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "NIVEL EDUCATIVO",
            color = ApuntesGreen,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            EducationOption(
                text = "Bachillerato",
                selected = educationLevel == "Bachillerato",
                onClick = {
                    educationLevel = "Bachillerato"
                }
            )

            EducationOption(
                text = "Licenciatura",
                selected = educationLevel == "Licenciatura",
                onClick = {
                    educationLevel = "Licenciatura"
                }
            )

            EducationOption(
                text = "Posgrado",
                selected = educationLevel == "Posgrado",
                onClick = {
                    educationLevel = "Posgrado"
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        EducationOption(
            text = "Otro",
            selected = educationLevel == "Otro",
            onClick = {
                educationLevel = "Otro"
            }
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "SEMESTRE O PERÍODO",
            color = ApuntesGreen,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        ApuntesTextField(
            value = semester,
            onValueChange = {
                semester = it
            },
            placeholder = "ej. II Semestre 2026"
        )

        Spacer(modifier = Modifier.weight(1f))

        ApuntesPrimaryButton(
            text = "Siguiente",
            onClick = onNext
        )

        Spacer(modifier = Modifier.height(26.dp))
    }
}

