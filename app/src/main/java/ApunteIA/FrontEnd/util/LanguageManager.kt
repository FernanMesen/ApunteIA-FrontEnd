package ApunteIA.FrontEnd.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

data class LanguageOption(
    val code: String,        // "es", "en", "pt", "it"
    val displayName: String, // "Español", "English", "Português", "Italiano"
    val shortCode: String   // "ES", "EN", "PT", "IT"
)

object LanguageManager {
    val SUPPORTED_LANGUAGES = listOf(
        LanguageOption("es", "Español", "ES"),
        LanguageOption("en", "English", "EN"),
        LanguageOption("pt", "Português", "PT"),
        LanguageOption("it", "Italiano", "IT")
    )

    /**
     * Sets the application locale using AndroidX AppCompatDelegate.
     * This automatically persists the locale setting across application launches.
     */
    fun setLanguage(code: String) {
        val appLocales = LocaleListCompat.forLanguageTags(code.lowercase())
        AppCompatDelegate.setApplicationLocales(appLocales)
    }

    /**
     * Returns the active Locale instance for the application.
     */
    fun getLocale(): Locale {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (!currentLocales.isEmpty) {
            val locale = currentLocales.get(0)
            if (locale != null) return locale
        }
        val currentCode = getCurrentLanguageCode()
        return Locale.forLanguageTag(currentCode)
    }

    /**
     * Returns the current language code ("es", "en", "pt", "it").
     */
    fun getCurrentLanguageCode(): String {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (!currentLocales.isEmpty) {
            val lang = currentLocales.get(0)?.language?.lowercase()
            if (lang != null && SUPPORTED_LANGUAGES.any { it.code == lang }) {
                return lang
            }
        }
        val systemLang = Locale.getDefault().language.lowercase()
        return if (SUPPORTED_LANGUAGES.any { it.code == systemLang }) systemLang else "es"
    }

    /**
     * Returns the uppercase short code ("ES", "EN", "PT", "IT") for the current language.
     */
    fun getCurrentLanguageShortCode(): String {
        val code = getCurrentLanguageCode()
        return SUPPORTED_LANGUAGES.firstOrNull { it.code == code }?.shortCode ?: "ES"
    }
}
