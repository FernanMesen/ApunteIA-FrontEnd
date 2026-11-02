package ApunteIA.FrontEnd.data.util

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.util.L10n
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

/**
 * Extrae el mensaje del `ErrorResponse` del backend:
 * `{ "status": 409, "message": "Ya existe un usuario...", "fieldErrors": { "email": "..." } }`.
 * Si hay errores por campo, se prefiere el primero (es más específico).
 */
fun Throwable.backendMessage(): String? {
    if (this !is HttpException) return null
    return try {
        val body = response()?.errorBody()?.string().orEmpty()
        if (body.isBlank()) return null
        val json = JSONObject(body)
        val fieldErrors = json.optJSONObject("fieldErrors")
        val firstFieldError = fieldErrors?.keys()?.asSequence()?.firstOrNull()?.let { fieldErrors.optString(it) }
        firstFieldError?.takeIf { it.isNotBlank() }
            ?: json.optString("message").takeIf { it.isNotBlank() }
    } catch (_: Exception) {
        null
    }
}

/** Mensaje genérico para errores de red/servidor, usando el del backend cuando existe. */
fun Throwable.toFriendlyMessage(defaultMessage: String = "Ocurrió un error inesperado"): String = when (this) {
    is IOException -> L10n.getString(R.string.auth_error_connection)
    is HttpException -> when (code()) {
        401 -> L10n.getString(R.string.api_error_session)
        403 -> backendMessage() ?: L10n.getString(R.string.api_error_permissions)
        else -> backendMessage() ?: L10n.getString(R.string.api_error_default, defaultMessage, code())
    }
    is NotLoggedInException -> L10n.getString(R.string.api_error_login_required)
    else -> message ?: defaultMessage
}

/** Se lanza cuando se intenta usar la API sin una sesión guardada. */
class NotLoggedInException : IllegalStateException("No hay una sesión iniciada")
