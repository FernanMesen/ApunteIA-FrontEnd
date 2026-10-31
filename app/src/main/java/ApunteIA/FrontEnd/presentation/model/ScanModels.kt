package ApunteIA.FrontEnd.presentation.model

import java.io.File

/**
 * Tipo de material que el usuario está escaneando. Determina cómo la IA
 * interpreta las fotos capturadas (letra manuscrita, texto impreso, etc).
 */
enum class MaterialType(val label: String) {
    HANDWRITTEN("Notas manuscritas"),
    BOOK_PAGES("Páginas de libro"),
    SLIDES("Diapositivas"),
    WHITEBOARD("Notas de pizarra")
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
enum class ProcessingStep(val label: String) {
    READING("Leyendo el texto de tus páginas"),
    UPLOADING("Guardando la nota en la nube"),
    ORGANIZING("Resumiendo con IA"),
    GENERATING("Generando la nota estructurada")
}

/**
 * Error durante el procesamiento del escaneo, con un título y un mensaje para el usuario.
 */
data class ProcessingError(val title: String, val message: String) {
    companion object {
        val NO_COURSE = ProcessingError(
            title = "Elige una materia",
            message = "Selecciona la materia del apunte antes de procesarlo."
        )
        val NO_TEXT = ProcessingError(
            title = "No encontramos texto",
            message = "No se pudo leer texto en las fotos. Intenta con mejor luz, enfocando la hoja de frente y sin sombras."
        )
        val UNREADABLE = ProcessingError(
            title = "Archivo inválido",
            message = "Una de las fotos no se pudo abrir. Vuelve a tomarla e intenta de nuevo."
        )

        fun upload(message: String) = ProcessingError(title = "No se pudo guardar", message = message)

        fun ai(message: String) = ProcessingError(
            title = "La IA no respondió",
            message = "$message Tu apunte quedó guardado: puedes reintentar el resumen ahora o más tarde desde Notas."
        )
    }
}