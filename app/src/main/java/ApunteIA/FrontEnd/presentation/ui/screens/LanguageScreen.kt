package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.ui.theme.DarkButton
import ApunteIA.FrontEnd.presentation.ui.theme.DarkSoftFill
import ApunteIA.FrontEnd.presentation.ui.theme.lightOr
import ApunteIA.FrontEnd.util.LanguageManager

@Composable
fun LanguageScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    isFromProfile: Boolean = false
) {

    var currentLanguageCode by remember {
        mutableStateOf(LanguageManager.getCurrentLanguageCode())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(onboardingBackground())
            .padding(horizontal = 24.dp)
    ) {

        if (isFromProfile) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(lightOr(ApuntesGreen.copy(alpha = 0.1f), DarkSoftFill))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.back),
                        tint = onboardingTitle()
                    )
                }
                Text(
                    text = stringResource(R.string.language_screen_title),
                    modifier = Modifier.weight(1f).padding(end = 40.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = onboardingTitle(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            OnboardingHeader(
                progress = 0.80f,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.language_screen_title),
                color = onboardingTitle(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(22.dp))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LanguageManager.SUPPORTED_LANGUAGES.forEach { option ->
                LanguageCardChoice(
                    displayName = option.displayName,
                    shortCode = option.shortCode,
                    selected = currentLanguageCode == option.code,
                    onClick = {
                        currentLanguageCode = option.code
                        LanguageManager.setLanguage(option.code)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        ApuntesPrimaryButton(
            text = if (isFromProfile) stringResource(R.string.ok) else stringResource(R.string.next_button),
            onClick = onNext
        )

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
private fun LanguageCardChoice(
    displayName: String,
    shortCode: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    // Claro: igual que antes. Oscuro: seleccionado con DarkButton y el resto como tarjeta del tema.
    val selectedFill = lightOr(ApuntesGreen, DarkButton)
    val unselectedText = onboardingTitle()
    val unselectedCode = lightOr(Color.Gray, MaterialTheme.colorScheme.onSurfaceVariant)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) selectedFill else onboardingCard())
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayName,
                color = if (selected) Color.White else unselectedText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = shortCode,
                color = if (selected) Color.White.copy(alpha = 0.8f) else unselectedCode,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}
