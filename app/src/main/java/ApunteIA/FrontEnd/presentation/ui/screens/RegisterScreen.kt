package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

private val RegisterBackground = Color(0xFFFBFAEF)
private val RegisterGreen = Color(0xFF0E3321)
private val RegisterBorder = Color(0xFFE2E2E2)
private val RegisterGray = Color(0xFF666666)

@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    onBackToLogin: () -> Unit
) {

    var fullName by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RegisterBackground)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(150.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(
                    color = RegisterGreen,
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

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre de la aplicación
        Text(
            text = "ApuntesIA",
            color = RegisterGreen,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu asistente de estudio con IA",
            color = RegisterGray,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Nombre completo
        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
            },
            placeholder = {
                Text(
                    text = "Nombre completo",
                    color = RegisterGray
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
                focusedBorderColor = RegisterGreen,
                unfocusedBorderColor = RegisterBorder,
                cursorColor = RegisterGreen
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Correo
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            placeholder = {
                Text(
                    text = "Correo electrónico",
                    color = RegisterGray
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
                focusedBorderColor = RegisterGreen,
                unfocusedBorderColor = RegisterBorder,
                cursorColor = RegisterGreen
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            placeholder = {
                Text(
                    text = "Contraseña",
                    color = RegisterGray
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
                focusedBorderColor = RegisterGreen,
                unfocusedBorderColor = RegisterBorder,
                cursorColor = RegisterGreen
            )
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Crear cuenta
        Button(
            onClick = {
                onRegister()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RegisterGreen
            )
        ) {
            Text(
                text = "Crear cuenta",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Volver al login
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "¿Ya tienes cuenta? ",
                color = RegisterGray,
                fontSize = 14.sp
            )

            Text(
                text = "Inicia sesión",
                color = RegisterGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    onBackToLogin()
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}