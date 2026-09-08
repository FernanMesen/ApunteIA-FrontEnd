package ApunteIA.FrontEnd.data.util

import ApunteIA.FrontEnd.domain.util.Resource
import retrofit2.HttpException
import java.io.IOException

/**
 * Ejecuta una llamada a la API de forma segura, capturando excepciones y permitiendo
 * mapearlas a mensajes de dominio amigables.
 */
suspend fun <T> safeApiCall(
    errorMapper: (Throwable) -> String = { throwable ->
        when (throwable) {
            is IOException -> "No hay conexión a internet o tiempo de espera agotado"
            is HttpException -> {
                val code = throwable.code()
                when (code) {
                    401 -> "Sesión expirada o no autorizada"
                    404 -> "Recurso no encontrado"
                    in 400..499 -> "Error en la solicitud ($code)"
                    in 500..599 -> "El servidor está experimentando problemas ($code)"
                    else -> "Error inesperado del servidor ($code)"
                }
            }
            else -> throwable.message ?: "Ocurrió un error inesperado"
        }
    },
    apiCall: suspend () -> T
): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (throwable: Throwable) {
        Resource.Error(errorMapper(throwable), throwable)
    }
}
