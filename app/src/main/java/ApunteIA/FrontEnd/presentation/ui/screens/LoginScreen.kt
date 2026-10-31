package ApunteIA.FrontEnd.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
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
import ApunteIA.FrontEnd.presentation.viewmodel.LoginViewModel
import ApunteIA.FrontEnd.util.LanguageManager

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val primaryColor = MaterialTheme.colorScheme.primary

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val comingSoonMessage = stringResource(R.string.coming_soon)

    val comingSoon = {
        Toast.makeText(
            context,
            comingSoonMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var selectedRole by remember {
        mutableStateOf("Estudiante")
    }

    var selectedLanguage by remember {
        mutableStateOf(
            LanguageManager.getCurrentLanguageShortCode()
        )
    }

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLogin()
            viewModel.onLoginHandled()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // Idioma

        Row(
            modifier = Modifier
                .background(
                    color = if (isDark) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        Color.White
                    },
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(4.dp)
        ) {
            LanguageManager.SUPPORTED_LANGUAGES.forEach { option ->

                LanguageButton(
                    text = option.shortCode,
                    selected = selectedLanguage == option.shortCode,
                    onClick = {
                        selectedLanguage = option.shortCode
                        LanguageManager.setLanguage(option.code)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
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
            modifier = Modifier.height(12.dp)
        )

        // Nombre

        Text(
            text = "ApuntesIA",
            color = primaryColor,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = stringResource(
                R.string.login_subtitle
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        // Rol

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(
                    color = if (isDark) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        Color.White
                    },
                    shape = RoundedCornerShape(30.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isDark) {
                        Color(0xFF394867)
                    } else {
                        Color(0xFFE2E2E2)
                    },
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(5.dp)
        ) {

            RoleButton(
                text = stringResource(
                    R.string.role_student
                ),
                selected = selectedRole == "Estudiante",
                onClick = {
                    selectedRole = "Estudiante"
                },
                modifier = Modifier.weight(1f)
            )

            RoleButton(
                text = stringResource(
                    R.string.role_admin
                ),
                selected = selectedRole == "Administrador",
                onClick = {
                    selectedRole = "Administrador"
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // Email

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
            modifier = Modifier.height(16.dp)
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

        // Error login

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
            modifier = Modifier.height(24.dp)
        )

        // Login

        Button(
            onClick = {
                viewModel.login(
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
                        R.string.login_button
                    ),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }

        if (uiState.isLoading) {

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = stringResource(
                    R.string.connecting_server_notice
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // Recuperar contraseña

        Text(
            text = stringResource(
                R.string.forgot_password
            ),
            color = primaryColor,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                comingSoon()
            }
        )

        /*
        ---------------------------------------------------------
        LOGIN CON GOOGLE
        Temporalmente deshabilitado.

        Cuando se implemente Google Sign-In, este bloque puede
        volver a habilitarse.
        ---------------------------------------------------------

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = if (isDark) {
                    Color(0xFF394867)
                } else {
                    Color(0xFFE2E2E2)
                }
            )

            Text(
                text = stringResource(
                    R.string.divider_or
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = 14.dp
                )
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = if (isDark) {
                    Color(0xFF394867)
                } else {
                    Color(0xFFE2E2E2)
                }
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedButton(
            onClick = {
                comingSoon()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isDark) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.White
                },
                contentColor = primaryColor
            )
        ) {

            Text(
                text = "G",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Text(
                text = stringResource(
                    R.string.continue_with_google
                ),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        */

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        // Registro

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(
                    R.string.no_account
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = stringResource(
                    R.string.register_here
                ),
                color = primaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    onRegister()
                }
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

@Composable
private fun LanguageButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .background(
                color = if (selected) {
                    primaryColor
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(24.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
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

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = if (selected) {
                    primaryColor
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(25.dp)
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                primaryColor
            },
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )
    }
}