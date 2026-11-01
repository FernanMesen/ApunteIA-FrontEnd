package ApunteIA.FrontEnd.util

enum class AiLanguageOption {
    SAME_AS_APP,
    ES,
    EN,
    PT,
    IT;

    companion object {
        fun fromString(value: String?): AiLanguageOption {
            if (value.isNullOrBlank()) return SAME_AS_APP
            val clean = value.trim()
            return entries.firstOrNull {
                it.name.equals(clean, ignoreCase = true)
            } ?: SAME_AS_APP
        }
    }
}
