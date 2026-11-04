package ApunteIA.FrontEnd.presentation.model

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.util.L10n
import java.io.File

/**
 * Tipo de material que el usuario está escaneando. Determina cómo la IA
 * interpreta las fotos capturadas (letra manuscrita, texto impreso, etc).
 */
enum class MaterialType {
    HANDWRITTEN,
    BOOK_PAGES,
    SLIDES,
    WHITEBOARD;

    val label: String
        get() = when (this) {
            HANDWRITTEN -> L10n.getString(R.string.mat_handwritten)
            BOOK_PAGES -> L10n.getString(R.string.mat_book_pages)
            SLIDES -> L10n.getString(R.string.mat_slides)
            WHITEBOARD -> L10n.getString(R.string.mat_whiteboard)
        }
}

/**
 * Una página capturada con la cámara dentro del flujo de escaneo.
 * [id] es estable (no cambia al reordenar), a diferencia de la posición
 * dentro de la lista, que sí define el número de página mostrado.
 * [imageFile] es el archivo JPEG real guardado por CameraX en el
 * almacenamiento privado de la app (usado tanto para la miniatura como
 * para poder borrarlo si la página se elimina).
 */
data class CapturedPage(
    val id: Int,
    val imageFile: File
)

/**
 * Un punto (bullet) editable dentro de una sección del texto reconocido.
 */
data class NoteBullet(
    val id: Int,
    val text: String
)

/**
 * Una sección del texto reconocido/generado por la IA (título + puntos).
 */
data class NoteSection(
    val id: Int,
    val title: String,
    val bullets: List<NoteBullet>
)

/**
 * Cada paso real del análisis que se muestra en la pantalla de procesamiento,
 * en el orden en que ocurren.
 */
enum class ProcessingStep {
    READING,
    UPLOADING,
    ORGANIZING,
    GENERATING;

    val label: String
        get() = when (this) {
            READING -> L10n.getString(R.string.step_reading)
            UPLOADING -> L10n.getString(R.string.step_uploading)
            ORGANIZING -> L10n.getString(R.string.step_organizing)
            GENERATING -> L10n.getString(R.string.step_generating)
        }
}

/**
 * Error durante el procesamiento del escaneo, con un título y un mensaje para el usuario.
 */
data class ProcessingError(val title: String, val message: String) {
    companion object {
        val NO_COURSE = ProcessingError(
            title = L10n.getString(R.string.error_no_course_title),
            message = L10n.getString(R.string.error_no_course_msg)
        )
        val NO_TEXT = ProcessingError(
            title = L10n.getString(R.string.error_no_text_title),
            message = L10n.getString(R.string.error_no_text_msg)
        )
        val UNREADABLE = ProcessingError(
            title = L10n.getString(R.string.error_unreadable_title),
            message = L10n.getString(R.string.error_unreadable_msg)
        )

        fun upload(message: String) = ProcessingError(title = L10n.getString(R.string.error_upload_title), message = message)

        fun ai(message: String) = ProcessingError(
            title = L10n.getString(R.string.error_ai_title),
            message = "$message " + L10n.getString(R.string.error_ai_msg)
        )
    }
}
