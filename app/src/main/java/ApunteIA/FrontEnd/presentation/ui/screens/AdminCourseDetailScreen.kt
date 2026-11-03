package ApunteIA.FrontEnd.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.border
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import ApunteIA.FrontEnd.domain.util.FormatUtils
import kotlin.math.roundToInt
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.domain.model.CourseStudent
import ApunteIA.FrontEnd.presentation.ui.theme.*
import ApunteIA.FrontEnd.presentation.viewmodel.AdminCourseDetailViewModel

/**
 * Administración de un curso: código, nombre, descripción, plan de estudios
 * (un tema por línea) y estudiantes inscritos, con la opción de quitarlos.
 */
@Composable
fun AdminCourseDetailScreen(
    viewModel: AdminCourseDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    var studentToRemove by remember { mutableStateOf<CourseStudent?>(null) }
    var showDeleteCourseDialog by remember { mutableStateOf(false) }

    var showCameraDialog by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPermission = granted
        if (granted) showCameraDialog = true
    }

    if (showCameraDialog) {
        Dialog(onDismissRequest = { showCameraDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val lifecycleOwner = LocalLifecycleOwner.current
                    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val capture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                                    .build()
                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, capture)
                                    imageCapture = capture
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        }
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .size(72.dp)
                            .background(Color.Transparent, CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                            .clickable {
                                imageCapture?.let { capture ->
                                    val scansDir = File(context.filesDir, "scans").apply { mkdirs() }
                                    val fileName = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(java.util.Date())
                                    val photoFile = File(scansDir, "SYLLABUS_$fileName.jpg")
                                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                                    capture.takePicture(
                                        outputOptions,
                                        ContextCompat.getMainExecutor(context),
                                        object : ImageCapture.OnImageSavedCallback {
                                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                                showCameraDialog = false
                                                viewModel.processSyllabusPhoto(photoFile)
                                            }
                                            override fun onError(exception: ImageCaptureException) {
                                                exception.printStackTrace()
                                            }
                                        }
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(56.dp).background(Color.White, CircleShape))
                    }
                }
            }
        }
    }

    LaunchedEffect(uiState.savedEvent) {
        if (uiState.savedEvent) {
            Toast.makeText(context, context.getString(R.string.admin_course_saved), Toast.LENGTH_SHORT).show()
            viewModel.onSavedEventHandled()
        }
    }

    studentToRemove?.let { student ->
        AlertDialog(
            onDismissRequest = { studentToRemove = null },
            title = {
                Text(
                    text = stringResource(R.string.admin_remove_student_title, student.fullName.ifBlank { student.email }),
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(stringResource(R.string.admin_remove_student_message, uiState.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeStudent(student)
                    studentToRemove = null
                }) {
                    Text(stringResource(R.string.admin_remove_student), color = StatusRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToRemove = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDeleteCourseDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteCourseDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.admin_delete_course_title, uiState.code.ifBlank { uiState.name }),
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(stringResource(R.string.admin_delete_course_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteCourseDialog = false
                    viewModel.deleteCourse(onSuccess = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        onBack()
                    })
                }) {
                    Text(stringResource(R.string.admin_delete_course), color = StatusRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCourseDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AdminTopBar(
                title = stringResource(if (uiState.isNew) R.string.admin_new_course else R.string.admin_edit_course_title),
                subtitle = uiState.code.takeIf { !uiState.isNew },
                onBack = onBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ---------- Datos del curso ----------
            item {
                OutlinedTextField(
                    value = uiState.code,
                    onValueChange = viewModel::onCodeChange,
                    label = { Text(stringResource(R.string.admin_course_code)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text(stringResource(R.string.admin_course_name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text(stringResource(R.string.admin_course_description)) },
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ---------- Plan de estudios ----------
            item {
                Column {
                    Text(
                        text = stringResource(R.string.admin_syllabus_header),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.admin_syllabus_hint),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = uiState.syllabus,
                        onValueChange = viewModel::onSyllabusChange,
                        placeholder = { Text(stringResource(R.string.admin_syllabus_placeholder)) },
                        minLines = 6,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            if (hasCameraPermission) {
                                showCameraDialog = true
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDark) Color.White else DarkGreen
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF394867) else DarkGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.admin_scan_syllabus_photo), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ---------- Errores + Guardar ----------
            uiState.validationError?.let { messageRes ->
                item { Text(text = stringResource(messageRes), color = StatusRed, fontSize = 14.sp) }
            }
            uiState.error?.let { message ->
                item { AdminErrorCard(message = message) }
            }
            item {
                Button(
                    onClick = { viewModel.save() },
                    enabled = !uiState.isSaving,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF394867) else DarkGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text(stringResource(R.string.admin_save_course), fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (!uiState.isNew) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { showDeleteCourseDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = StatusRed
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusRed.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.admin_delete_course), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ---------- Estudiantes ----------
            item {
                Text(
                    text = stringResource(R.string.admin_students_header, uiState.students.size),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            when {
                uiState.isNew -> item {
                    Text(
                        text = stringResource(R.string.admin_students_after_save),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                uiState.isLoading -> item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.students.isEmpty() -> item {
                    Text(
                        text = stringResource(R.string.admin_no_students),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> items(uiState.students, key = { it.enrollmentId }) { student ->
                    StudentRow(
                        student = student,
                        isRemoving = uiState.removingEnrollmentId == student.enrollmentId,
                        onRemove = { studentToRemove = student }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentRow(student: CourseStudent, isRemoving: Boolean, onRemove: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    val displayName = student.fullName.ifBlank { student.email }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF394867) else DarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayName.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (student.email.isNotBlank() && student.email != displayName) {
                    Text(
                        text = student.email,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val gradeText = student.averageGrade?.let {
                    stringResource(R.string.admin_student_grade_value, FormatUtils.formatPercentage(it.roundToInt(), maxFractionDigits = 0))
                } ?: stringResource(R.string.admin_student_no_grade)
                Text(
                    text = gradeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (student.averageGrade != null) (if (isDark) Color(0xFF81C784) else DarkGreen) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isRemoving) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
            } else {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.admin_remove_student),
                        tint = StatusRed
                    )
                }
            }
        }
    }
}
