package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import ApunteIA.FrontEnd.R

@OptIn(ExperimentalLayoutApi::class)
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
            .verticalScroll(rememberScrollState())
            .background(onboardingBackground())
            .padding(horizontal = 24.dp)
    ) {

        OnboardingHeader(
            progress = 0.20f,
            showBack = false
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.setup_profile_title),
            color = onboardingTitle(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.your_name_header),
            color = onboardingTitle(),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        ApuntesTextField(
            value = name,
            onValueChange = {
                name = it
            },
            placeholder = stringResource(R.string.full_name_placeholder)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.education_level_header),
            color = onboardingTitle(),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // FlowRow: si las 3 opciones no caben (PT/IT en pantallas angostas), pasan a otra línea.
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            EducationOption(
                text = stringResource(R.string.edu_high_school),
                selected = educationLevel == "Bachillerato",
                onClick = {
                    educationLevel = "Bachillerato"
                }
            )

            EducationOption(
                text = stringResource(R.string.edu_bachelor),
                selected = educationLevel == "Licenciatura",
                onClick = {
                    educationLevel = "Licenciatura"
                }
            )

            EducationOption(
                text = stringResource(R.string.edu_postgraduate),
                selected = educationLevel == "Posgrado",
                onClick = {
                    educationLevel = "Posgrado"
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        EducationOption(
            text = stringResource(R.string.edu_other),
            selected = educationLevel == "Otro",
            onClick = {
                educationLevel = "Otro"
            }
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = stringResource(R.string.semester_header),
            color = onboardingTitle(),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        ApuntesTextField(
            value = semester,
            onValueChange = {
                semester = it
            },
            placeholder = stringResource(R.string.semester_placeholder)
        )

        Spacer(modifier = Modifier.height(24.dp))

        ApuntesPrimaryButton(
            text = stringResource(R.string.next_button),
            onClick = onNext
        )

        Spacer(modifier = Modifier.height(26.dp))
    }
}
