package ApunteIA.FrontEnd.domain.util

/**
 * Representa los diferentes estados de una operación de datos.
 * [T] es el tipo de dato que esperamos recibir (ej. List<Note>).
 *
 * Success: La operación fue exitosa y contiene la data.
 * Error: La operación falló, contiene un mensaje amigable y opcionalmente el error técnico.
 * Loading: La operación está en curso.
 */
sealed interface Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}
