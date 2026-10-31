package ApunteIA.FrontEnd.domain.repository

import java.io.File

/** Reconoce el texto de las páginas escaneadas (OCR en el dispositivo). */
interface TextRecognizer {
    /** Devuelve el texto de todas las páginas, en orden. Lanza excepción si una imagen no se puede leer. */
    suspend fun recognize(pages: List<File>): String
}