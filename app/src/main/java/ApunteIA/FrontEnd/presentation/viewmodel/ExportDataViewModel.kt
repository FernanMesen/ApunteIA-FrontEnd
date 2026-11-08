package ApunteIA.FrontEnd.presentation.viewmodel

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ExportDataViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun exportData(context: Context, format: String, successMessage: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            try {
                val responseBody = apiService.exportData(format)
                val fileName = "apunteia_export_${System.currentTimeMillis()}.$format"
                val mimeType = when (format) {
                    "json" -> "application/json"
                    "pdf" -> "application/pdf"
                    else -> "text/csv"
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                        put(MediaStore.Downloads.MIME_TYPE, mimeType)
                        put(MediaStore.Downloads.IS_PENDING, 1)
                    }

                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    uri?.let { destinationUri ->
                        resolver.openOutputStream(destinationUri)?.use { outputStream ->
                            outputStream.write(responseBody.bytes())
                        }
                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(destinationUri, contentValues, null, null)
                    }
                } else {
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!downloadsDir.exists()) {
                        downloadsDir.mkdirs()
                    }
                    val file = File(downloadsDir, fileName)
                    FileOutputStream(file).use { outputStream ->
                        outputStream.write(responseBody.bytes())
                    }
                }

                _message.value = "$successMessage (Guardado en Descargas)"
            } catch (e: Exception) {
                _message.value = "Error al exportar: ${e.localizedMessage ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
