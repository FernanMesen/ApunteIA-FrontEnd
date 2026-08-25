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
 * Cada paso del "análisis" que se muestra en la pantalla de procesamiento,
 * en el orden en que ocurren.
 */
enum class ProcessingStep(val label: String) {
    UPLOADING("Subiendo imágenes"),
    READING("Leyendo el contenido manuscrito"),
    ORGANIZING("Organizando la información"),
    COMPARING("Comparando con el programa"),
    GENERATING("Generando la nota estructurada")
}

/**
 * Errores simulables durante el procesamiento (botones de "Simular" en la
 * pantalla de análisis), para poder probar los estados de error del flujo
 * sin depender de un backend real.
 */
enum class ProcessingError(val title: String, val message: String) {
    NO_CONNECTION(
        title = "Sin conexión",
        message = "No pudimos conectarnos para analizar tu escaneo. Revisa tu conexión a internet e intenta de nuevo."
    ),
    BLURRY_PHOTO(
        title = "Foto borrosa",
        message = "Una de las páginas está demasiado borrosa para leerla. Vuelve a escanear esa página."
    ),
    INVALID_FILE(
        title = "Archivo inválido",
        message = "Uno de los archivos capturados no se pudo procesar. Inténtalo de nuevo."
    )
}