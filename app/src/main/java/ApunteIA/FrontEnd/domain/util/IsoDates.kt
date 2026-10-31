package ApunteIA.FrontEnd.domain.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Utilidades de fecha sin java.time (minSdk 24). El backend manda fechas ISO-8601:
 * `LocalDateTime` ("2026-10-29T17:18:01.123456") e `Instant` ("2026-10-29T23:18:01Z").
 * El servidor de Render corre en UTC, así que ambas se interpretan como UTC.
 */
object IsoDates {

    private const val DAY_MS = 24L * 60 * 60 * 1000

    /** Milisegundos epoch, o null si el texto no es una fecha válida. */
    fun parse(iso: String?): Long? {
        if (iso.isNullOrBlank()) return null
        val trimmed = iso.trim()
        val (pattern, value) = when {
            trimmed.length >= 19 -> "yyyy-MM-dd'T'HH:mm:ss" to trimmed.substring(0, 19)
            trimmed.length >= 10 -> "yyyy-MM-dd" to trimmed.substring(0, 10)
            else -> return null
        }
        return try {
            SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
                isLenient = false
            }.parse(value)?.time
        } catch (_: Exception) {
            null
        }
    }

    /** "29/10/2026" en la zona horaria del teléfono. */
    fun shortDate(iso: String?): String {
        val millis = parse(iso) ?: return ""
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(millis)
    }

    /** Clave de día local ("2026-10-29") para agrupar actividad por día. */
    fun dayKey(millis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(millis)

    /** "Hace 5 min", "Hace 3 h", "Ayer", "Hace 4 días" o la fecha corta. */
    fun relative(iso: String?, now: Long = System.currentTimeMillis()): String {
        val millis = parse(iso) ?: return ""
        val diff = (now - millis).coerceAtLeast(0)
        val minutes = diff / 60_000
        val hours = minutes / 60
        val days = daysBetween(millis, now)
        return when {
            minutes < 1 -> "Justo ahora"
            minutes < 60 -> "Hace $minutes min"
            days == 0 -> "Hace $hours h"
            days == 1 -> "Ayer"
            days < 7 -> "Hace $days días"
            else -> shortDate(iso)
        }
    }

    /** Días calendario (zona local) entre dos instantes. */
    fun daysBetween(fromMillis: Long, toMillis: Long): Int {
        val from = startOfDay(fromMillis)
        val to = startOfDay(toMillis)
        return ((to - from + DAY_MS / 2) / DAY_MS).toInt()
    }

    fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    /** Inicio (00:00 local) del lunes de la semana de [millis]. */
    fun startOfWeek(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = startOfDay(millis)
        val dayOfWeek = get(Calendar.DAY_OF_WEEK)
        val offset = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        add(Calendar.DAY_OF_YEAR, -offset)
    }.timeInMillis
}