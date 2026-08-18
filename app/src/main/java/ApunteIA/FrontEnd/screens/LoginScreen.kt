package ApunteIA.FrontEnd.screens

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

private val BackgroundColor = Color(0xFFFBFAEF)
private val DarkGreen = Color(0xFF0E3321)
private val BorderColor = Color(0xFFE2E2E2)
private val TextGray = Color(0xFF666666)

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
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
            .background(BackgroundColor)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(28.dp))

      // Seleccionar idioma

        Row(
            modifier = Modifier
                .background(
                    color = Color.White,
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
                    color = DarkGreen,
                    shape = RoundedCornerShape(22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "★",
                color = Color.White,
                fontSize = 34.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tittle

        Text(
            text = "ApuntesIA",
            color = DarkGreen,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu asistente de estudio con IA",
            color = TextGray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Seleccionar Rol

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(
                    Color.White,
                    RoundedCornerShape(30.dp)
                )
                .border(
                    width = 1.dp,
                    color = BorderColor,
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
                    color = TextGray
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = DarkGreen,
                unfocusedBorderColor = BorderColor,
                cursorColor = DarkGreen
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
                    color = TextGray
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = DarkGreen,
                unfocusedBorderColor = BorderColor,
                cursorColor = DarkGreen
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
                containerColor = DarkGreen
            )
        ) {
            Text(
                text = "Iniciar sesión",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Olvidó contraseña

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = DarkGreen,
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
                color = BorderColor
            )

            Text(
                text = "o",
                color = TextGray,
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = BorderColor
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
                containerColor = Color.White,
                contentColor = DarkGreen
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                BorderColor
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
                color = TextGray,
                fontSize = 14.sp
            )

            Text(
                text = "Regístrate",
                color = DarkGreen,
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

    Box(
        modifier = Modifier
            .background(
                color = if (selected) DarkGreen else Color.Transparent,
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
            color = if (selected) Color.White else TextGray,
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
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = if (selected) DarkGreen else Color.Transparent,
                shape = RoundedCornerShape(25.dp)
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else DarkGreen,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )
    }
}