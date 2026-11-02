package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.data.datasource.model.CourseDto
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.util.LanguageManager

fun CourseDto.toDomain(): CourseInfo {
    val lang = LanguageManager.getCurrentLanguageCode().lowercase()
    val upperCode = code?.uppercase() ?: ""

    val (localizedName, localizedDesc) = when (upperCode) {
        "EIF411" -> when (lang) {
            "en" -> "Mobile Platforms Backend" to "Persistence and backend services"
            "pt" -> "Backend para Plataformas Móveis" to "Persistência e serviços backend"
            "it" -> "Backend per Piattaforme Mobili" to "Persistenza e servizi backend"
            else -> "Backend para plataformas móviles" to "Persistencia y servicios backend"
        }
        "EIF412" -> when (lang) {
            "en" -> "Databases" to "Relational modeling and queries"
            "pt" -> "Bancos de Dados" to "Modelagem relacional e consultas"
            "it" -> "Basi di Dati" to "Modellazione relazionale e query"
            else -> "Bases de datos" to "Modelado relacional y consultas"
        }
        "EIF413" -> when (lang) {
            "en" -> "Distributed Programming" to "Distributed concepts and systems"
            "pt" -> "Programação Distribuída" to "Conceitos e sistemas distribuídos"
            "it" -> "Programmazione Distribuita" to "Concetti e sistemi distribuiti"
            else -> "Programación distribuida" to "Conceptos y sistemas distribuidos"
        }
        else -> {
            val fallbackName = name?.takeIf { it.isNotBlank() } ?: code.orEmpty().ifBlank { "Curso $id" }
            val fallbackDesc = description.orEmpty()
            fallbackName to fallbackDesc
        }
    }

    return CourseInfo(
        id = id,
        code = code.orEmpty(),
        name = localizedName,
        description = localizedDesc
    )
}

fun List<CourseDto>.toDomain(): List<CourseInfo> = map { it.toDomain() }