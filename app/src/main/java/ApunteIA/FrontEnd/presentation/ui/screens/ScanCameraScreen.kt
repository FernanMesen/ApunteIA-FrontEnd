package ApunteIA.FrontEnd.presentation.ui.screens
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.viewmodel.ScanViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ScanCameraScreen(
    viewModel: ScanViewModel,
    onClose: () -> Unit,
    onDone: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val cameraMode by viewModel.cameraMode.collectAsState()
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            val scansDir = File(context.filesDir, "scans").apply { mkdirs() }
            val fileName = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(java.util.Date())
            val photoFile = File(scansDir, "GALLERY_$fileName.jpg")
            try {
                context.contentResolver.openInputStream(selectedUri)?.use { inputStream ->
                    photoFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                viewModel.capturePage(photoFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val isCameraAllowed = cameraMode != "DENIED" && (
            cameraMode == "WHILE_USING_APP" || cameraMode == "FULL_ACCESS" ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scanBackground())
            .padding(horizontal = 20.dp)
    ) {
        ScanTopBar(
            onClose = onClose,
            trailingContent = {
                if (state.hasCapturedPages) {
                    CountBadge(count = state.capturedPages.size)
                }
            }
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Surface(shape = RoundedCornerShape(20.dp), color = scanSoftFill()) {
                Text(
                    text = state.subject,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    color = scanText(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isCameraAllowed) {
            var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.72f)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        onImageCaptureReady = { imageCapture = it }
                    )
                    ViewfinderCorners()
                    Text(
                        text = stringResource(R.string.align_notes_frame),
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .background(Color(0x66000000), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .size(48.dp)
                        .background(scanSoftFill(), RoundedCornerShape(14.dp))
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Abrir galería", tint = scanText())
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.Transparent, CircleShape)
                        .border(3.dp, Color(0xFFD9D9D9), CircleShape)
                        .clickable {
                            takePhoto(context, imageCapture) { file ->
                                viewModel.capturePage(file)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (state.hasCapturedPages) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = scanButton(),
                        modifier = Modifier.clickable { onDone() }
                    ) {
                        Text(
                            text = stringResource(R.string.ready_badge, state.capturedPages.size),
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = scanAccent(), modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.camera_permission_needed),
                    color = scanText(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = scanButton())
                ) {
                    Text(stringResource(R.string.allow_camera_access), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Preview en vivo de CameraX, expone el [ImageCapture] ya vinculado al ciclo de vida. */
@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                    onImageCaptureReady(imageCapture)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

/** Dispara la captura de una foto y la guarda en el almacenamiento privado de la app. */
private fun takePhoto(
    context: android.content.Context,
    imageCapture: ImageCapture?,
    onSaved: (File) -> Unit
) {
    val capture = imageCapture ?: return

    val scansDir = File(context.filesDir, "scans").apply { mkdirs() }
    val fileName = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(java.util.Date())
    val photoFile = File(scansDir, "SCAN_$fileName.jpg")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    capture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onSaved(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}

/** Dibuja las 4 esquinas del marco guía del escáner, encima de la preview de la cámara. */
@Composable
private fun ViewfinderCorners() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cornerLength = 28.dp.toPx()
        val stroke = 4.dp.toPx()
        val color = Color.White

        drawLine(color, Offset(0f, cornerLength), Offset(0f, 0f), stroke, StrokeCap.Round)
        drawLine(color, Offset(0f, 0f), Offset(cornerLength, 0f), stroke, StrokeCap.Round)

        drawLine(color, Offset(size.width - cornerLength, 0f), Offset(size.width, 0f), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width, 0f), Offset(size.width, cornerLength), stroke, StrokeCap.Round)

        drawLine(color, Offset(0f, size.height - cornerLength), Offset(0f, size.height), stroke, StrokeCap.Round)
        drawLine(color, Offset(0f, size.height), Offset(cornerLength, size.height), stroke, StrokeCap.Round)

        drawLine(color, Offset(size.width - cornerLength, size.height), Offset(size.width, size.height), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - cornerLength), stroke, StrokeCap.Round)
    }
}

