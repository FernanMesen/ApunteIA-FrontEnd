package ApunteIA.FrontEnd.screens

import androidx.compose.foundation.background
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
fun LanguageScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    var language by remember {
        mutableStateOf("ES")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApuntesBackground)
            .padding(horizontal = 24.dp)
    ) {

        OnboardingHeader(
            progress = 0.80f,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Idioma preferido",
            color = ApuntesGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(4.dp)
        ) {

            LanguageChoice(
                text = "ES",
                selected = language == "ES",
                onClick = {
                    language = "ES"
                }
            )

            LanguageChoice(
                text = "EN",
                selected = language == "EN",
                onClick = {
                    language = "EN"
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        ApuntesPrimaryButton(
            text = "Siguiente",
            onClick = onNext
        )

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
private fun LanguageChoice(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .background(
                color = if (selected) {
                    ApuntesGreen
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(24.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 22.dp,
                vertical = 10.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = if (selected) Color.White else ApuntesGreen,
            fontWeight = FontWeight.SemiBold
        )
    }
}
