package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet

enum class ChangePasswordStep {
    EMAIL_INPUT,
    CHECK_EMAIL,
    NEW_PASSWORD,
    SUCCESS
}

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit = {}
) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val primaryColor = MaterialTheme.colorScheme.primary
    
    var currentStep by remember { mutableStateOf(ChangePasswordStep.EMAIL_INPUT) }
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            when (currentStep) {
                ChangePasswordStep.EMAIL_INPUT -> {
                    EmailInputContent(
                        email = email,
                        onEmailChange = { email = it },
                        onSendClick = { currentStep = ChangePasswordStep.CHECK_EMAIL },
                        onBack = onBack,
                        isDark = isDark,
                        primaryColor = primaryColor
                    )
                }
                ChangePasswordStep.CHECK_EMAIL -> {
                    CheckEmailContent(
                        onContinue = { currentStep = ChangePasswordStep.NEW_PASSWORD },
                        onBack = onBack,
                        isDark = isDark,
                        primaryColor = primaryColor
                    )
                }
                ChangePasswordStep.NEW_PASSWORD -> {
                    NewPasswordContent(
                        newPassword = newPassword,
                        onNewPasswordChange = { newPassword = it },
                        confirmPassword = confirmPassword,
                        onConfirmPasswordChange = { confirmPassword = it },
                        onUpdateClick = { currentStep = ChangePasswordStep.SUCCESS },
                        isDark = isDark,
                        primaryColor = primaryColor
                    )
                }
                ChangePasswordStep.SUCCESS -> {
                    PasswordSuccessContent(
                        onContinue = onFinish,
                        isDark = isDark,
                        primaryColor = primaryColor
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordSuccessContent(
    onContinue: () -> Unit,
    isDark: Boolean,
    primaryColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF1E2532) else Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (isDark) Color(0xFF5D7B92) else Color(0xFF4CAF50),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Contraseña actualizada",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Tu contraseña se cambió correctamente.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) Color(0xFF394867) else primaryColor,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Continuar al inicio",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmailInputContent(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBack: () -> Unit,
    isDark: Boolean,
    primaryColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0xFF1E2532) else Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Restablece tu contraseña",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Ingresa el correo de tu cuenta y te enviaremos un enlace de recuperación.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ApuntesTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Correo electrónico"
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onSendClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) Color(0xFF394867) else primaryColor,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Enviar enlace",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Volver al inicio",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { onBack() }
        )
    }
}

@Composable
fun CheckEmailContent(
    onContinue: () -> Unit,
    onBack: () -> Unit,
    isDark: Boolean,
    primaryColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF1E2532) else Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (isDark) Color(0xFF5D7B92) else Color(0xFF4CAF50),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Revisa tu correo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Enviamos un enlace de recuperación. Síguelo para crear una nueva contraseña.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) Color(0xFF394867) else primaryColor,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Continuar al inicio",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Volver al inicio",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { onBack() }
        )
    }
}

@Composable
fun NewPasswordContent(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onUpdateClick: () -> Unit,
    isDark: Boolean,
    primaryColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Crea una nueva contraseña",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        ApuntesTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            placeholder = "Nueva contraseña"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ApuntesTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = "Confirmar contraseña"
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onUpdateClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) Color(0xFF394867) else primaryColor,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Actualizar contraseña",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
