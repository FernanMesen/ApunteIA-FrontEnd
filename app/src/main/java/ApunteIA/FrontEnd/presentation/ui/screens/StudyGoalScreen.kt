package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StudyGoalScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    var selectedGoal by remember {
        mutableStateOf("Aprobar exámenes")
    }

    val goals = listOf(
        "Aprobar exámenes",
        "Mejorar calificaciones",
        "Mantenerme organizado",
        "Repasar constantemente"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApuntesBackground)
            .padding(horizontal = 24.dp)
    ) {

        OnboardingHeader(
            progress = 0.60f,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Meta de estudio",
            color = ApuntesGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        goals.forEach { goal ->

            val selected = selectedGoal == goal

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (selected) {
                            ApuntesGreen
                        } else {
                            ApuntesBorder
                        },
                        shape = RoundedCornerShape(15.dp)
                    )
                    .clickable {
                        selectedGoal = goal
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = goal,
                    modifier = Modifier.weight(1f),
                    color = ApuntesGreen,
                    fontSize = 14.sp
                )

                if (selected) {
                    Text(
                        text = "✓",
                        color = ApuntesGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        ApuntesPrimaryButton(
            text = "Siguiente",
            onClick = onNext
        )

        Spacer(modifier = Modifier.height(26.dp))
    }
}

