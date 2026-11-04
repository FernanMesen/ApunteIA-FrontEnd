package ApunteIA.FrontEnd.util

import android.content.Context
import ApunteIA.FrontEnd.R

object L10n {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun getString(resId: Int, vararg formatArgs: Any): String {
        val ctx = appContext
        if (ctx == null) {
            return getFallbackString(resId, *formatArgs)
        }
        return try {
            if (formatArgs.isNotEmpty()) {
                ctx.getString(resId, *formatArgs)
            } else {
                ctx.getString(resId)
            }
        } catch (_: Exception) {
            getFallbackString(resId, *formatArgs)
        }
    }

    fun get(resId: Int, vararg formatArgs: Any): String = getString(resId, *formatArgs)

    private fun getFallbackString(resId: Int, vararg formatArgs: Any): String {
        val raw = when (resId) {
            R.string.step_reading -> "Leyendo el texto de tus páginas"
            R.string.step_uploading -> "Guardando la nota en la nube"
            R.string.step_organizing -> "Resumiendo con IA"
            R.string.step_generating -> "Generando la nota estructurada"
            R.string.error_no_course_title -> "Elige una materia"
            R.string.error_no_course_msg -> "Selecciona la materia del apunte antes de procesarlo."
            R.string.error_no_text_title -> "No encontramos texto"
            R.string.error_no_text_msg -> "No se pudo leer texto en las fotos. Intenta con mejor luz, enfocando la hoja de frente y sin sombras."
            R.string.error_unreadable_title -> "Archivo inválido"
            R.string.error_unreadable_msg -> "Una de las fotos no se pudo abrir. Vuelve a tomarla e intenta de nuevo."
            R.string.error_upload_title -> "No se pudo guardar"
            R.string.error_ai_title -> "La IA no respondió"
            R.string.error_ai_msg -> "Tu apunte quedó guardado: puedes reintentar el resumen ahora o más tarde desde Notas."
            R.string.login_error_empty -> "Ingresa tu correo y contraseña."
            R.string.login_error_role_mismatch_student -> "Estas credenciales pertenecen a una cuenta de estudiante. Selecciona Estudiante para continuar."
            R.string.login_error_role_mismatch_admin -> "Estas credenciales pertenecen a una cuenta de administrador. Selecciona Administrador para continuar."
            R.string.register_error_name -> "Ingresa tu nombre completo."
            R.string.register_error_email -> "Ingresa un correo válido."
            R.string.register_error_password_len -> "La contraseña debe tener al menos 6 caracteres."
            R.string.register_error_password_max -> "La contraseña no puede tener más de 72 caracteres."
            R.string.auth_error_credentials -> "Correo o contraseña incorrectos."
            R.string.auth_error_connection -> "No hay conexión con el servidor. Si es la primera vez en un rato, espera ~1 min: el servidor se está despertando."
            R.string.auth_error_server -> "El servidor de autenticación falló (Error %1\$d)."
            R.string.auth_error_unknown -> "Ocurrió un error inesperado al iniciar sesión."
            R.string.note_error_connection -> "No hay conexión a internet. Por favor, verifica tu red."
            R.string.note_error_client -> "Hubo un problema con la solicitud (Error %1\$d)."
            R.string.note_error_502 -> "La IA no pudo procesar la nota. Intenta de nuevo."
            R.string.note_error_503 -> "La IA no está disponible en este momento."
            R.string.note_error_server -> "El servidor de notas está teniendo problemas (Error %1\$d)."
            R.string.note_error_invalid_data -> "Los datos recibidos no son válidos."
            R.string.note_error_unknown -> "Ocurrió un error inesperado al gestionar tus notas."
            R.string.main_error_connection -> "Sin conexión para actualizar el tablero."
            R.string.main_error_server -> "El servidor principal falló (Error %1\$d)."
            R.string.main_error_unknown -> "Error inesperado en el tablero."
            R.string.quiz_error_connection -> "No hay conexión para cargar el cuestionario."
            R.string.quiz_error_server -> "El servidor de cuestionarios falló (Error %1\$d)."
            R.string.quiz_error_unknown -> "Ocurrió un error al cargar las preguntas."
            R.string.topic_error_connection -> "Sin conexión para cargar tus temas."
            R.string.topic_error_data -> "No se pudieron cargar las materias en este momento."
            R.string.topic_error_unknown -> "Ocurrió un error inesperado con los temas de estudio."
            R.string.api_error_session -> "Tu sesión expiró. Inicia sesión de nuevo."
            R.string.api_error_permissions -> "No tienes permisos para realizar esta acción."
            R.string.api_error_default -> "%1\$s (Error %2\$d)"
            R.string.api_error_login_required -> "Inicia sesión para continuar."
            R.string.note_login_required -> "Inicia sesión para ver tus notas."
            R.string.time_just_now -> "Justo ahora"
            R.string.time_yesterday -> "Ayer"
            R.string.time_minutes_ago -> "Hace %1\$d min"
            R.string.time_hours_ago -> "Hace %1\$d h"
            R.string.time_days_ago -> "Hace %1\$d días"
            R.string.fallback_untitled_note -> "Nota sin título"
            R.string.fallback_no_course -> "Sin curso"
            R.string.new_section -> "Nueva sección"
            R.string.quiz_correct_answer_prefix -> "Respuesta correcta: %1\$s"
            R.string.study_time_minutes -> "%1\$d min"
            R.string.activity_saved_note -> "Apunte guardado · %1\$s"
            R.string.activity_completed_quiz -> "Quiz completado · %1\$s"
            R.string.activity_score_format -> "Nota: %1\$d%%"
            R.string.error_loading_courses -> "No se pudieron cargar los cursos"
            R.string.error_enroll_course -> "No se pudo inscribir en el curso"
            R.string.error_course_not_found -> "No existe un curso con el código \"%1\$s\"."
            R.string.fallback_course_id -> "Curso %1\$d"
            else -> ""
        }
        return if (formatArgs.isNotEmpty()) {
            try {
                String.format(java.util.Locale.getDefault(), raw, *formatArgs)
            } catch (_: Exception) {
                raw
            }
        } else {
            raw
        }
    }
}
