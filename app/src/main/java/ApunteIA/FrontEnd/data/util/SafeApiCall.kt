package ApunteIA.FrontEnd.data.util

import ApunteIA.FrontEnd.domain.util.Resource
import kotlin.coroutines.cancellation.CancellationException

/**
 * Ejecuta una llamada a la API de forma segura, capturando excepciones y permitiendo
 * mapearlas a mensajes de dominio amigables. La cancelación de corrutinas (p. ej. al
 * salir de una pantalla) se vuelve a lanzar para no confundirla con un error.
 */
suspend fun <T> safeApiCall(
    errorMapper: (Throwable) -> String = { it.toFriendlyMessage() },
    apiCall: suspend () -> T
): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        Resource.Error(errorMapper(throwable), throwable)
    }
}