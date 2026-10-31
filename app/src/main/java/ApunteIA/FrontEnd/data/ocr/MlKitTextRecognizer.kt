package ApunteIA.FrontEnd.data.ocr

import android.content.Context
import android.net.Uri
import ApunteIA.FrontEnd.domain.repository.TextRecognizer
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * OCR en el dispositivo con ML Kit (modelo latino incluido en el APK: funciona sin
 * internet y no manda las fotos a ningún servidor). Solo el texto reconocido se
 * envía al backend, que es quien llama a la IA para resumirlo.
 */
@Singleton
class MlKitTextRecognizer @Inject constructor(
    @ApplicationContext private val context: Context
) : TextRecognizer {

    private val recognizer by lazy { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    override suspend fun recognize(pages: List<File>): String {
        val texts = pages.map { file ->
            // fromFilePath respeta la orientación EXIF que guarda CameraX.
            val image = InputImage.fromFilePath(context, Uri.fromFile(file))
            recognizeImage(image)
        }
        return texts
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(separator = "\n\n")
    }

    private suspend fun recognizeImage(image: InputImage): String =
        suspendCancellableCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    if (continuation.isActive) continuation.resume(result.text)
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) continuation.resumeWithException(error)
                }
        }
}