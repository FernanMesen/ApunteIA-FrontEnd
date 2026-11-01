package ApunteIA.FrontEnd.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiLanguagePreferenceManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("apunteia_ai_language", Context.MODE_PRIVATE)

    var aiLanguageOption: AiLanguageOption
        get() {
            val name = prefs.getString(KEY_AI_LANGUAGE, AiLanguageOption.SAME_AS_APP.name)
            return AiLanguageOption.fromString(name)
        }
        set(value) {
            prefs.edit().putString(KEY_AI_LANGUAGE, value.name).apply()
        }

    fun getEffectiveLanguageCode(): String {
        return when (aiLanguageOption) {
            AiLanguageOption.SAME_AS_APP -> LanguageManager.getCurrentLanguageCode()
            AiLanguageOption.ES -> "es"
            AiLanguageOption.EN -> "en"
            AiLanguageOption.PT -> "pt"
            AiLanguageOption.IT -> "it"
        }
    }

    companion object {
        private const val KEY_AI_LANGUAGE = "ai_language_option"
    }
}
