package ApunteIA.FrontEnd.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PermissionsScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApuntesBackground)
            .padding(horizontal = 24.dp)
    ) {

        OnboardingHeader(
            progress = 1f,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Terminar configuración",
            color = ApuntesGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(22.dp))

        PermissionCard(
            title = "Acceso a la cámara",
            description = "Necesario para escanear tus notas manuscritas.",
            onClick = {
                // Después implementaremos el permiso real
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        PermissionCard(
            title = "Acceso a fotos",
            description = "Importa escaneos desde fotos existentes.",
            onClick = {
                // Después implementaremos el permiso real
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        ApuntesPrimaryButton(
            text = "Terminar configuración",
            onClick = onFinish
        )

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            )
            .border(
                width = 1.dp,
                color = ApuntesBorder,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(16.dp)
    ) {

        Text(
            text = title,
            color = ApuntesGreen,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            color = ApuntesGray,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = ApuntesGreen
            ),
            shape = RoundedCornerShape(25.dp)
        ) {

            Text(
                text = "Permitir",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

