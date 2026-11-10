package ApunteIA.FrontEnd.presentation.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
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
import ApunteIA.FrontEnd.presentation.ui.theme.*
import ApunteIA.FrontEnd.presentation.ui.components.LocalCurrentUser
import ApunteIA.FrontEnd.util.AiLanguageOption
import ApunteIA.FrontEnd.util.LanguageManager

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    isDarkMode: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    aiLanguageOption: AiLanguageOption = AiLanguageOption.SAME_AS_APP,
    onAiLanguageChange: (AiLanguageOption) -> Unit = {},
    cameraModeStr: String = "NOT_CONFIGURED",
    onManageSubjects: () -> Unit = {},
    onStudyHistory: () -> Unit = {},
    onCameraAccess: () -> Unit = {},
    onPrivacy: () -> Unit = {},
    onExportData: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onLanguage: () -> Unit = {},
    notesCount: Int? = null,
    quizzesCount: Int? = null,
    isAdmin: Boolean = false,
    onAdminCourses: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isThemeDark = MaterialTheme.colorScheme.primary == SpaceCadet

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = if (isThemeDark) Color(0xFF252E3E) else Color.White,
            title = {
                Text(
                    text = stringResource(R.string.profile_delete_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.profile_delete_message),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isThemeDark) Color(0xFF3B2525) else StatusRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.confirm), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    border = BorderStroke(1.dp, if (isThemeDark) Color(0xFF394867) else Color(0xFFEEEEEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ProfileTopBar(onBack = onBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ProfileHeader()
            }

            if (!isAdmin) {
                item {
                    StatsSection(notesCount = notesCount, quizzesCount = quizzesCount)
                }
            }

            if (isAdmin) {
                item {
                    AdminSection(onAdminCourses = onAdminCourses)
                }
            }

            item {
                ConfigSection(
                    isAdmin = isAdmin,
                    isDarkMode = isDarkMode,
                    onThemeToggle = onThemeToggle,
                    aiLanguageOption = aiLanguageOption,
                    onAiLanguageChange = onAiLanguageChange,
                    cameraModeStr = cameraModeStr,
                    onManageSubjects = onManageSubjects,
                    onStudyHistory = onStudyHistory,
                    onCameraAccess = onCameraAccess,
                    onPrivacy = onPrivacy,
                    onExportData = onExportData,
                    onChangePassword = onChangePassword,
                    onLanguage = onLanguage
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                LogoutButton(onClick = onLogout)
            }

            item {
                Text(
                    text = stringResource(R.string.profile_delete_account),
                    color = if (MaterialTheme.colorScheme.primary == SpaceCadet) Color(0xFFC07B7B) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .clickable { showDeleteDialog = true }
                )
            }
        }
    }
}

/** Sección visible solo para ADMIN: acceso a la administración de cursos. */
@Composable
fun AdminSection(onAdminCourses: () -> Unit) {
    val isThemeDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Column {
        Text(
            text = stringResource(R.string.admin_section_header),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isThemeDark) Color(0xFF9BA4B5) else TextGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = if (isThemeDark) BorderStroke(1.dp, Color(0xFF394867)) else null
        ) {
            ConfigItem(
                label = stringResource(R.string.admin_courses_entry),
                onClick = onAdminCourses,
                trailing = {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun ProfileTopBar(onBack: () -> Unit) {
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
            text = stringResource(R.string.profile_title),
            modifier = Modifier.weight(1f).padding(end = 40.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ProfileHeader() {
    val user = LocalCurrentUser.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user?.initial ?: "?",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user?.name ?: stringResource(R.string.user_fallback_name),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = user?.email.orEmpty(),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatsSection(notesCount: Int?, quizzesCount: Int?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            StatItem(stringResource(R.string.notes_captured), notesCount?.toString() ?: "—")
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))
            StatItem(stringResource(R.string.quizzes_done), quizzesCount?.toString() ?: "—")
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    val isThemeDark = MaterialTheme.colorScheme.primary == SpaceCadet
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isThemeDark) DarkAccent else MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ConfigSection(
    isAdmin: Boolean = false,
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    aiLanguageOption: AiLanguageOption,
    onAiLanguageChange: (AiLanguageOption) -> Unit,
    cameraModeStr: String = "NOT_CONFIGURED",
    onManageSubjects: () -> Unit,
    onStudyHistory: () -> Unit,
    onCameraAccess: () -> Unit,
    onPrivacy: () -> Unit,
    onExportData: () -> Unit,
    onChangePassword: () -> Unit,
    onLanguage: () -> Unit
) {
    val isThemeDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Column {
        Text(
            text = stringResource(R.string.profile_config_header),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isThemeDark) Color(0xFF9BA4B5) else TextGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = if (isThemeDark) BorderStroke(1.dp, Color(0xFF394867)) else null
        ) {
            Column {
                if (!isAdmin) {
                    ConfigItem(
                        label = stringResource(R.string.profile_manage_subjects),
                        onClick = onManageSubjects,
                        trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                    ConfigItem(
                        label = stringResource(R.string.profile_study_history),
                        onClick = onStudyHistory,
                        trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                }
                ConfigItem(
                    label = stringResource(R.string.profile_appearance),
                    trailing = { AppearanceToggle(isDarkMode, onThemeToggle) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = stringResource(R.string.profile_language),
                    onClick = onLanguage,
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = LanguageManager.getCurrentLanguageShortCode(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isThemeDark) DarkAccent else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = stringResource(R.string.profile_ai_language),
                    trailing = { AiLanguageDropdown(aiLanguageOption, onAiLanguageChange) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                val context = LocalContext.current
                val cameraGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                val cameraStatus = when {
                    cameraModeStr == "DENIED" -> stringResource(R.string.permission_status_denied)
                    cameraModeStr == "FULL_ACCESS" -> stringResource(R.string.permission_status_full)
                    cameraModeStr == "WHILE_USING_APP" || cameraGranted -> stringResource(R.string.permission_status_while)
                    else -> stringResource(R.string.permission_status_while)
                }

                ConfigItem(
                    label = stringResource(R.string.profile_camera_access),
                    onClick = onCameraAccess,
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = cameraStatus,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isThemeDark) DarkAccent else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = stringResource(R.string.profile_privacy),
                    onClick = onPrivacy,
                    trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = stringResource(R.string.profile_export_data),
                    onClick = onExportData,
                    trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)
                ConfigItem(
                    label = stringResource(R.string.profile_change_password),
                    onClick = onChangePassword,
                    trailing = { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiLanguageDropdown(
    currentOption: AiLanguageOption,
    onOptionSelected: (AiLanguageOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isThemeDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val options = listOf(
        AiLanguageOption.SAME_AS_APP to stringResource(R.string.ai_lang_same_as_app),
        AiLanguageOption.ES to stringResource(R.string.ai_lang_spanish),
        AiLanguageOption.EN to stringResource(R.string.ai_lang_english),
        AiLanguageOption.PT to stringResource(R.string.ai_lang_portuguese),
        AiLanguageOption.IT to stringResource(R.string.ai_lang_italian)
    )
    // En el chip va el código corto (ES/EN/PT/IT o "App"); los nombres completos, en el menú.
    val chipLabel = when (currentOption) {
        AiLanguageOption.SAME_AS_APP -> stringResource(R.string.ai_lang_same_as_app_short)
        AiLanguageOption.ES -> "ES"
        AiLanguageOption.EN -> "EN"
        AiLanguageOption.PT -> "PT"
        AiLanguageOption.IT -> "IT"
    }

    Box {
        Surface(
            modifier = Modifier.clickable { expanded = true },
            shape = RoundedCornerShape(16.dp),
            color = if (isThemeDark) Color(0xFF1E2532) else Color(0xFFF5F5F5)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chipLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isThemeDark) DarkAccent else MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isThemeDark) DarkAccent else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = if (isThemeDark) MaterialTheme.colorScheme.surfaceVariant else Color.White
        ) {
            options.forEach { (option, label) ->
                val selected = option == currentOption
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            color = if (selected) (if (isThemeDark) DarkAccent else MaterialTheme.colorScheme.primary) else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    trailingIcon = if (selected) {
                        { Icon(Icons.Default.Check, null, tint = if (isThemeDark) DarkAccent else MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) }
                    } else null,
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ConfigItem(label: String, onClick: () -> Unit = {}, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // weight(1f): el label ocupa lo que sobra y el trailing nunca se aplasta ni se parte.
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        )
        trailing()
    }
}

@Composable
fun AppearanceToggle(isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val isThemeDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Row(
        modifier = Modifier
            .background(if (isThemeDark) Color(0xFF1E2532) else Color(0xFFF5F5F5), RoundedCornerShape(20.dp))
            .border(0.5.dp, if (isThemeDark) Color(0xFF394867) else Color.Transparent, RoundedCornerShape(20.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (!isDarkMode) (if (isThemeDark) Color(0xFF394867) else DarkGreen) else Color.Transparent)
                .clickable { onThemeToggle(false) }
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.light_theme),
                color = if (!isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDarkMode) (if (isThemeDark) Color(0xFF394867) else DarkGreen) else Color.Transparent)
                .clickable { onThemeToggle(true) }
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.dark_theme),
                color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDark) Color(0xFF3B2525) else LightRed,
            contentColor = if (isDark) Color(0xFFC07B7B) else StatusRed
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_logout),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
