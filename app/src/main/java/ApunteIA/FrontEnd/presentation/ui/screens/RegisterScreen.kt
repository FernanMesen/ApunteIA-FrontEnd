package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet
import ApunteIA.FrontEnd.presentation.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.registered) {
        if (uiState.registered) {
            onRegister()
            viewModel.onRegisterHandled()
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val isDark = primaryColor == SpaceCadet

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
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(60.dp)
        )

        // Logo

        Image(
            painter = painterResource(
                id = R.drawable.apuntesia_logo
            ),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Nombre de la aplicación

        Text(
            text = "ApuntesIA",
            color = primaryColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = stringResource(
                R.string.login_subtitle
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // Nombre completo

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
            },
            placeholder = {
                Text(
                    text = stringResource(
                        R.string.full_name_placeholder
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDark) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.White
                },
                unfocusedContainerColor = if (isDark) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.White
                },
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = if (isDark) {
                    Color(0xFF394867)
                } else {
                    Color(0xFFE2E2E2)
                },
                cursorColor = primaryColor,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Correo

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            placeholder = {
                Text(
                    text = stringResource(
                        R.string.email_placeholder
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDark) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.White
                },
                unfocusedContainerColor = if (isDark) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.White
                },
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = if (isDark) {
                    Color(0xFF394867)
                } else {
                    Color(0xFFE2E2E2)
                },
                cursorColor = primaryColor,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Contraseña

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            placeholder = {
                Text(
                    text = stringResource(
                        R.string.password_placeholder
                    ),
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
                focusedContainerColor = if (isDark) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.White
                },
                unfocusedContainerColor = if (isDark) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.White
                },
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = if (isDark) {
                    Color(0xFF394867)
                } else {
                    Color(0xFFE2E2E2)
                },
                cursorColor = primaryColor,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        if (uiState.errorMessage != null) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = uiState.errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        // Crear cuenta

        Button(
            onClick = {
                viewModel.register(
                    fullName,
                    email,
                    password
                )
            },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            )
        ) {

            if (uiState.isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )

            } else {

                Text(
                    text = stringResource(
                        R.string.register_title
                    ),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // Volver al login

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(
                    R.string.already_have_account
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = stringResource(
                    R.string.login_here
                ),
                color = primaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    onBackToLogin()
                }
            )
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )
    }
}