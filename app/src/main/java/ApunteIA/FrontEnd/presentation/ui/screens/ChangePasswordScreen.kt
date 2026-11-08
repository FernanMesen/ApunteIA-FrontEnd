package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit = {}
) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val primaryColor = MaterialTheme.colorScheme.primary
    
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = stringResource(R.string.profile_change_password),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 40.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSuccess) {
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
                        text = stringResource(R.string.password_updated_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = stringResource(R.string.password_updated_message),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = onFinish,
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
                            text = stringResource(R.string.continue_to_start),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.create_new_password_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        placeholder = { Text("Contraseña actual") },
                        visualTransformation = if (currentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { currentVisible = !currentVisible }) {
                                Icon(
                                    imageVector = if (currentVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = { Text(stringResource(R.string.new_password_placeholder)) },
                        visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { newVisible = !newVisible }) {
                                Icon(
                                    imageVector = if (newVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text(stringResource(R.string.confirm_password_placeholder)) },
                        visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                Icon(
                                    imageVector = if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = errorMessage.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = {
                            if (currentPassword.isBlank()) {
                                errorMessage = "Ingresa tu contraseña actual"
                                return@Button
                            }
                            if (newPassword.length < 6) {
                                errorMessage = "La nueva contraseña debe tener al menos 6 caracteres"
                                return@Button
                            }
                            if (newPassword != confirmPassword) {
                                errorMessage = "Las contraseñas no coinciden"
                                return@Button
                            }
                            errorMessage = null
                            isSuccess = true
                        },
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
                            text = stringResource(R.string.update_password_button),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
