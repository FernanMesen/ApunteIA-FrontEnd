package ApunteIA.FrontEnd.util

enum class AiLanguageOption {
    SAME_AS_APP,
    ES,
    EN,
    PT,
    IT;

    companion object {
        fun fromString(value: String?): AiLanguageOption {
            return entries.firstOrNull { it.name == value } ?: SAME_AS_APP
        }
    }
}
