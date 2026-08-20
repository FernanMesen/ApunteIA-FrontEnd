package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ApuntesBackground = Color(0xFFFBFAEF)
val ApuntesGreen = Color(0xFF0B3B27)
val ApuntesBorder = Color(0xFFE1E3DE)
val ApuntesGray = Color(0xFF68736D)

@Composable
fun OnboardingHeader(
    progress: Float,
    showBack: Boolean = true,
    onBack: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (showBack) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFE6E9DF))
                    .clickable {
                        onBack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    color = ApuntesGreen,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Spacer(modifier = Modifier.width(34.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFE1E5DC))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .background(ApuntesGreen)
            )
        }
    }
}

@Composable
fun ApuntesTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = ApuntesGray
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = ApuntesGreen,
            unfocusedBorderColor = ApuntesBorder,
            cursorColor = ApuntesGreen
        )
    )
}

@Composable
fun ApuntesPrimaryButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ApuntesGreen
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

@Composable
fun EducationOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = ApuntesGreen,
                shape = RoundedCornerShape(25.dp)
            )
            .background(
                color = if (selected) ApuntesGreen else Color.Transparent,
                shape = RoundedCornerShape(25.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 15.dp,
                vertical = 9.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else ApuntesGreen,
            fontSize = 13.sp
        )
    }
}

