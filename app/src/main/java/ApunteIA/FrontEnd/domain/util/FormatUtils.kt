package ApunteIA.FrontEnd.domain.util

import ApunteIA.FrontEnd.util.LanguageManager
import java.text.NumberFormat

object FormatUtils {
    /** Formatea un porcentaje según la configuración regional del usuario (ej. 80.5% en EN, 80,5% en ES/PT/IT). */
    fun formatPercentage(value: Number, maxFractionDigits: Int = 1): String {
        val locale = LanguageManager.getLocale()
        val nf = NumberFormat.getPercentInstance(locale).apply {
            maximumFractionDigits = maxFractionDigits
        }
        return nf.format(value.toDouble() / 100.0)
    }

    /** Formatea un número decimal o entero según la configuración regional. */
    fun formatNumber(number: Number, maxFractionDigits: Int = 1): String {
        val locale = LanguageManager.getLocale()
        val nf = NumberFormat.getInstance(locale).apply {
            maximumFractionDigits = maxFractionDigits
        }
        return nf.format(number)
    }
}
