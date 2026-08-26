package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val primaryColor = MaterialTheme.colorScheme.primary
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var selectedRole by remember {
        mutableStateOf("Estudiante")
    }

    var selectedLanguage by remember {
        mutableStateOf("ES")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(28.dp))

      // Seleccionar idioma

        Row(
            modifier = Modifier
                .background(
                    color = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(4.dp)
        ) {

            LanguageButton(
                text = "ES",
                selected = selectedLanguage == "ES",
                onClick = {
                    selectedLanguage = "ES"
                }
            )

            LanguageButton(
                text = "EN",
                selected = selectedLanguage == "EN",
                onClick = {
                    selectedLanguage = "EN"
                }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Logo

        Box(
            modifier = Modifier
                .size(76.dp)
                .background(
                    color = primaryColor,
                    shape = RoundedCornerShape(22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "★",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 34.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tittle

        Text(
            text = "ApuntesIA",
            color = primaryColor,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu asistente de estudio con IA",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Seleccionar Rol

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(
                    if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White,
                    RoundedCornerShape(30.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF394867) else Color(0xFFE2E2E2),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(5.dp)
        ) {

            RoleButton(
                text = "Estudiante",
                selected = selectedRole == "Estudiante",
                onClick = {
                    selectedRole = "Estudiante"
                },
                modifier = Modifier.weight(1f)
            )

            RoleButton(
                text = "Administrador",
                selected = selectedRole == "Administrador",
                onClick = {
                    selectedRole = "Administrador"
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Email

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            placeholder = {
                Text(
                    text = "Correo electrónico",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White,
                unfocusedContainerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = if (isDark) Color(0xFF394867) else Color(0xFFE2E2E2),
                cursorColor = primaryColor,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contraseña

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            placeholder = {
                Text(
                    text = "Contraseña",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White,
                unfocusedContainerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = if (isDark) Color(0xFF394867) else Color(0xFFE2E2E2),
                cursorColor = primaryColor,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Botón de login

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            )
        ) {
            Text(
                text = "Iniciar sesión",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Olvidó contraseña

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = primaryColor,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                // Se implementará después
            }
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Divider

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = if (isDark) Color(0xFF394867) else Color(0xFFE2E2E2)
            )

            Text(
                text = "o",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = if (isDark) Color(0xFF394867) else Color(0xFFE2E2E2)
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Botón Google

        OutlinedButton(
            onClick = {
                // Google login se implementará después
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White,
                contentColor = primaryColor
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDark) Color(0xFF394867) else Color(0xFFE2E2E2)
            )
        ) {

            Text(
                text = "G",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Continuar con Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Register

        Row {

            Text(
                text = "¿No tienes cuenta? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            Text(
                text = "Regístrate",
                color = primaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    onRegister()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}




@Composable
private fun LanguageButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val isDark = primaryColor == SpaceCadet

    Box(
        modifier = Modifier
            .background(
                color = if (selected) primaryColor else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 18.dp,
                vertical = 10.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RoleButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val isDark = primaryColor == SpaceCadet

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = if (selected) primaryColor else Color.Transparent,
                shape = RoundedCornerShape(25.dp)
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else primaryColor,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )
    }

}
