package ApunteIA.FrontEnd.data.util

import ApunteIA.FrontEnd.domain.util.Resource
import retrofit2.HttpException
import java.io.IOException

/**
 * Ejecuta una llamada a la API de forma segura, capturando excepciones comunes de red.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> Resource.Error("No hay conexión a internet o tiempo de espera agotado", throwable)
            is HttpException -> {
                val code = throwable.code()
                val message = when (code) {
                    401 -> "Sesión expirada o no autorizada"
                    404 -> "Recurso no encontrado"
                    in 400..499 -> "Error en la solicitud ($code)"
                    in 500..599 -> "El servidor está experimentando problemas ($code)"
                    else -> "Error inesperado del servidor ($code)"
                }
                Resource.Error(message, throwable)
            }
            else -> Resource.Error("Ocurrió un error inesperado", throwable)
        }
    }
}
