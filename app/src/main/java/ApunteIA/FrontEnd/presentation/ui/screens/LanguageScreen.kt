package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
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
            .background(ApuntesBackground)
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
                        .background(ApuntesGreen.copy(alpha = 0.1f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = ApuntesGreen
                    )
                }
                Text(
                    text = stringResource(R.string.language_screen_title),
                    modifier = Modifier.weight(1f).padding(end = 40.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApuntesGreen
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
                color = ApuntesGreen,
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (selected) ApuntesGreen else Color.White,
                shape = RoundedCornerShape(20.dp)
            )
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
                color = if (selected) Color.White else ApuntesGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = shortCode,
                color = if (selected) Color.White.copy(alpha = 0.8f) else Color.Gray,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}
