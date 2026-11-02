package ApunteIA.FrontEnd.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import ApunteIA.FrontEnd.R

enum class PermissionAccessMode {
    NOT_CONFIGURED,
    FULL_ACCESS,
    WHILE_USING_APP,
    DENIED
}

private enum class PermissionTarget {
    CAMERA,
    PHOTOS
}

@Composable
fun PermissionsScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val photosPermissionName = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    var cameraMode by remember {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        mutableStateOf(if (granted) PermissionAccessMode.WHILE_USING_APP else PermissionAccessMode.NOT_CONFIGURED)
    }

    var photosMode by remember {
        val granted = ContextCompat.checkSelfPermission(context, photosPermissionName) == PackageManager.PERMISSION_GRANTED
        mutableStateOf(if (granted) PermissionAccessMode.FULL_ACCESS else PermissionAccessMode.NOT_CONFIGURED)
    }

    var activeDialogTarget by remember { mutableStateOf<PermissionTarget?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && cameraMode == PermissionAccessMode.NOT_CONFIGURED) {
            cameraMode = PermissionAccessMode.WHILE_USING_APP
        }
    }

    val photosPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && photosMode == PermissionAccessMode.NOT_CONFIGURED) {
            photosMode = PermissionAccessMode.FULL_ACCESS
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ApuntesBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            OnboardingHeader(
                progress = 1f,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.finish_setup_title),
                color = ApuntesGreen,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(22.dp))

            PermissionCard(
                title = stringResource(R.string.camera_permission_title),
                description = stringResource(R.string.camera_permission_desc),
                accessMode = cameraMode,
                onClick = { activeDialogTarget = PermissionTarget.CAMERA }
            )

            Spacer(modifier = Modifier.height(18.dp))

            PermissionCard(
                title = stringResource(R.string.photos_permission_title),
                description = stringResource(R.string.photos_permission_desc),
                accessMode = photosMode,
                onClick = { activeDialogTarget = PermissionTarget.PHOTOS }
            )

            Spacer(modifier = Modifier.weight(1f))

            ApuntesPrimaryButton(
                text = stringResource(R.string.finish_setup_title),
                onClick = onFinish
            )

            Spacer(modifier = Modifier.height(26.dp))
        }
    }

    activeDialogTarget?.let { target ->
        val dialogTitle = if (target == PermissionTarget.CAMERA) {
            stringResource(R.string.camera_permission_dialog_title)
        } else {
            stringResource(R.string.photos_permission_dialog_title)
        }

        PermissionRequestDialog(
            title = dialogTitle,
            target = target,
            onDismissRequest = { activeDialogTarget = null },
            onSelectOption = { mode ->
                activeDialogTarget = null
                val messageRes: Int

                when (target) {
                    PermissionTarget.CAMERA -> {
                        cameraMode = mode
                        messageRes = when (mode) {
                            PermissionAccessMode.FULL_ACCESS -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                R.string.permission_msg_full_camera
                            }
                            PermissionAccessMode.WHILE_USING_APP -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                R.string.permission_msg_while_camera
                            }
                            PermissionAccessMode.DENIED -> R.string.permission_msg_denied_camera
                            PermissionAccessMode.NOT_CONFIGURED -> R.string.permission_msg_denied_camera
                        }
                    }
                    PermissionTarget.PHOTOS -> {
                        photosMode = mode
                        messageRes = when (mode) {
                            PermissionAccessMode.FULL_ACCESS -> {
                                photosPermissionLauncher.launch(photosPermissionName)
                                R.string.permission_msg_full_photos
                            }
                            PermissionAccessMode.WHILE_USING_APP -> {
                                photosPermissionLauncher.launch(photosPermissionName)
                                R.string.permission_msg_while_photos
                            }
                            PermissionAccessMode.DENIED -> R.string.permission_msg_denied_photos
                            PermissionAccessMode.NOT_CONFIGURED -> R.string.permission_msg_denied_photos
                        }
                    }
                }

                val message = context.getString(messageRes)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    accessMode: PermissionAccessMode,
    onClick: () -> Unit
) {
    val borderColor = when (accessMode) {
        PermissionAccessMode.FULL_ACCESS, PermissionAccessMode.WHILE_USING_APP -> ApuntesGreen
        PermissionAccessMode.DENIED -> Color(0xFFD32F2F)
        PermissionAccessMode.NOT_CONFIGURED -> ApuntesBorder
    }

    val cardBg = when (accessMode) {
        PermissionAccessMode.FULL_ACCESS, PermissionAccessMode.WHILE_USING_APP -> Color(0xFFF2F7F4)
        PermissionAccessMode.DENIED -> Color(0xFFFFEBEE)
        PermissionAccessMode.NOT_CONFIGURED -> Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(cardBg)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(15.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = ApuntesGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (accessMode == PermissionAccessMode.FULL_ACCESS || accessMode == PermissionAccessMode.WHILE_USING_APP) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ApuntesGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            color = ApuntesGray,
            fontSize = 13.sp
        )

        AnimatedVisibility(visible = accessMode != PermissionAccessMode.NOT_CONFIGURED) {
            val statusTextRes = when (accessMode) {
                PermissionAccessMode.FULL_ACCESS -> R.string.permission_status_full
                PermissionAccessMode.WHILE_USING_APP -> R.string.permission_status_while
                PermissionAccessMode.DENIED -> R.string.permission_status_denied
                PermissionAccessMode.NOT_CONFIGURED -> null
            }

            val statusColor = if (accessMode == PermissionAccessMode.DENIED) Color(0xFFD32F2F) else ApuntesGreen

            statusTextRes?.let { resId ->
                Text(
                    text = stringResource(resId),
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val buttonContainerColor = when (accessMode) {
            PermissionAccessMode.FULL_ACCESS, PermissionAccessMode.WHILE_USING_APP -> ApuntesGreen
            PermissionAccessMode.DENIED -> Color(0xFF757575)
            PermissionAccessMode.NOT_CONFIGURED -> ApuntesGreen
        }

        val buttonText = when (accessMode) {
            PermissionAccessMode.FULL_ACCESS -> stringResource(R.string.permission_full_access)
            PermissionAccessMode.WHILE_USING_APP -> stringResource(R.string.permission_while_using_app)
            PermissionAccessMode.DENIED -> stringResource(R.string.permission_deny)
            PermissionAccessMode.NOT_CONFIGURED -> stringResource(R.string.allow)
        }

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonContainerColor
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = buttonText,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PermissionRequestDialog(
    title: String,
    target: PermissionTarget,
    onDismissRequest: () -> Unit,
    onSelectOption: (PermissionAccessMode) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(ApuntesGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (target == PermissionTarget.CAMERA) Icons.Default.CameraAlt else Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = ApuntesGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    color = ApuntesGreen,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                DialogOptionButton(
                    text = stringResource(R.string.permission_full_access),
                    onClick = { onSelectOption(PermissionAccessMode.FULL_ACCESS) },
                    containerColor = ApuntesGreen,
                    contentColor = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                DialogOptionButton(
                    text = stringResource(R.string.permission_while_using_app),
                    onClick = { onSelectOption(PermissionAccessMode.WHILE_USING_APP) },
                    containerColor = Color(0xFFE8ECE9),
                    contentColor = ApuntesGreen
                )

                Spacer(modifier = Modifier.height(10.dp))

                DialogOptionButton(
                    text = stringResource(R.string.permission_deny),
                    onClick = { onSelectOption(PermissionAccessMode.DENIED) },
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFFD32F2F)
                )
            }
        }
    }
}

@Composable
private fun DialogOptionButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
