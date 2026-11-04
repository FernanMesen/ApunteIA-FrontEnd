package ApunteIA.FrontEnd.domain.util

import android.content.Context
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.util.LanguageManager
import ApunteIA.FrontEnd.util.L10n
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

    /**
     * Patrón de fecha con año completo según el idioma de la app:
     * inglés → MM/dd/yyyy (mes/día/año); español, portugués, italiano → dd/MM/yyyy (día/mes/año).
     */
    fun datePattern(locale: Locale = LanguageManager.getLocale()): String =
        if (locale.language == "en") "MM/dd/yyyy" else "dd/MM/yyyy"

    /**
     * Fecha corta en el formato del idioma de la app.
     * - Fecha sola ("2026-11-04"): es un día del calendario, se formatea en UTC para que no
     *   se corra un día por la zona horaria.
     * - Fecha con hora ("2026-11-04T23:18:01"): viene en UTC del servidor y se muestra en la
     *   zona horaria del teléfono.
     */
    fun shortDate(iso: String?): String {
        val millis = parse(iso) ?: return ""
        val dateOnly = iso!!.trim().length == 10
        val locale = LanguageManager.getLocale()
        return SimpleDateFormat(datePattern(locale), locale).apply {
            if (dateOnly) timeZone = TimeZone.getTimeZone("UTC")
        }.format(millis)
    }

    /** Hoy en formato ISO ("2026-11-04"), según la zona horaria del teléfono. */
    fun todayIsoDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(System.currentTimeMillis())

    /** Fecha ISO a partir de lo que devuelve el DatePickerDialog (mes base 0). */
    fun isoDateOf(year: Int, monthZeroBased: Int, day: Int): String =
        String.format(Locale.US, "%04d-%02d-%02d", year, monthZeroBased + 1, day)

    /** Clave de día local ("2026-10-29") para agrupar actividad por día. */
    fun dayKey(millis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(millis)

    /** "Hace 5 min", "Ayer" o fecha corta, adaptado al contexto y Locale. */
    fun relative(
        iso: String?,
        now: Long = System.currentTimeMillis(),
        context: Context? = null
    ): String {
        val millis = parse(iso) ?: return ""
        val diff = (now - millis).coerceAtLeast(0)
        val minutes = diff / 60_000
        val hours = minutes / 60
        val days = daysBetween(millis, now)

        if (context == null) {
            return when {
                minutes < 1 -> L10n.getString(R.string.time_just_now)
                minutes < 60 -> L10n.getString(R.string.time_minutes_ago, minutes.toInt())
                days == 0 -> L10n.getString(R.string.time_hours_ago, hours.toInt())
                days == 1 -> L10n.getString(R.string.time_yesterday)
                days < 7 -> L10n.getString(R.string.time_days_ago, days)
                else -> shortDate(iso)
            }
        }

        val res = context.resources
        return when {
            minutes < 1 -> res.getString(R.string.time_just_now)
            minutes < 60 -> res.getString(R.string.time_minutes_ago, minutes.toInt())
            days == 0 -> res.getString(R.string.time_hours_ago, hours.toInt())
            days == 1 -> res.getString(R.string.time_yesterday)
            days < 7 -> res.getString(R.string.time_days_ago, days)
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
