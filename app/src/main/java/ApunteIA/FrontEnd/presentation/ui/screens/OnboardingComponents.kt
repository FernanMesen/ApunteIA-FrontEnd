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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ApunteIA.FrontEnd.presentation.ui.theme.DarkAccent
import ApunteIA.FrontEnd.presentation.ui.theme.DarkBorder
import ApunteIA.FrontEnd.presentation.ui.theme.DarkButton
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet
import ApunteIA.FrontEnd.presentation.ui.theme.lightOr

val ApuntesBackground = Color(0xFFFBFAEF)
val ApuntesGreen = Color(0xFF0B3B27)
val ApuntesBorder = Color(0xFFE1E3DE)
val ApuntesGray = Color(0xFF68736D)

/*
 * Colores del onboarding: en claro son los de siempre (ApuntesBackground, ApuntesGreen...);
 * en oscuro, los del tema. Así ninguna pantalla del onboarding queda clara en modo oscuro.
 */

/** Fondo de pantalla del onboarding. */
@Composable
fun onboardingBackground(): Color = lightOr(ApuntesBackground, MaterialTheme.colorScheme.background)

/** Títulos y textos principales (ApuntesGreen en claro). */
@Composable
fun onboardingTitle(): Color = lightOr(ApuntesGreen, MaterialTheme.colorScheme.onBackground)

/** Íconos, marcas de selección y bordes activos (ApuntesGreen en claro). */
@Composable
fun onboardingAccent(): Color = lightOr(ApuntesGreen, DarkAccent)

/** Fondo de tarjetas y opciones no seleccionadas (blanco en claro). */
@Composable
fun onboardingCard(): Color = lightOr(Color.White, MaterialTheme.colorScheme.surfaceVariant)

/** Bordes de tarjetas sin seleccionar. */
@Composable
fun onboardingBorder(): Color = lightOr(ApuntesBorder, DarkBorder)

/** Texto secundario (descripciones). */
@Composable
fun onboardingMuted(): Color = lightOr(ApuntesGray, MaterialTheme.colorScheme.onSurfaceVariant)

@Composable
fun OnboardingHeader(
    progress: Float,
    showBack: Boolean = true,
    onBack: () -> Unit = {}
) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    // En oscuro, el primary (SpaceCadet) casi no se ve sobre el fondo: se usa el acento claro.
    val headerGreen = if (isDark) DarkAccent else ApuntesGreen
    
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
                    .background(if (isDark) Color(0xFF252B3B) else Color(0xFFE6E9DF))
                    .clickable {
                        onBack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    color = headerGreen,
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
                .background(if (isDark) Color(0xFF252B3B) else Color(0xFFE1E5DC))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .background(headerGreen)
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
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else ApuntesGray
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White,
            unfocusedContainerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isDark) Color(0xFF394867) else ApuntesBorder,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EducationOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Claro: igual que antes (primary). Oscuro: borde/texto con acento claro y relleno DarkButton.
    val color = lightOr(MaterialTheme.colorScheme.primary, DarkAccent)
    val selectedFill = lightOr(MaterialTheme.colorScheme.primary, DarkButton)

    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (selected) selectedFill else color,
                shape = RoundedCornerShape(25.dp)
            )
            .background(
                color = if (selected) selectedFill else Color.Transparent,
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
            color = if (selected) Color.White else color,
            fontSize = 13.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}

