package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.NoteDto

/**
 * Datos de ejemplo en memoria (equivalente a un "seed" o fixture) que
 * simulan lo que vendría de una base de datos o API real. Es lo único que
 * habría que reemplazar el día que exista un backend.
 */
object NoteProvider {

    val notes: List<NoteDto> = listOf(
        NoteDto(
            id = 1,
            title = "Estereoquímica",
            className = "Química Orgánica",
            classNumber = 5,
            date = "Escaneado 4 ago",
            preview = "Los centros quirales surgen cuando un carbono se une a cuatro grupos diferentes. Los enantiómeros son imágenes especulares no superponibles...",
            status = "Débil"
        ),
        NoteDto(
            id = 2,
            title = "Alcanos y Nomenclatura",
            className = "Química Orgánica",
            classNumber = 3,
            date = "Escaneado 28 jul",
            preview = "La nomenclatura IUPAC sigue la cadena de carbono más larga, numerada para dar a los sustituyentes las posiciones más bajas.",
            status = "Sólido"
        ),
        NoteDto(
            id = 3,
            title = "Química Ácido-Base",
            className = "Química Orgánica",
            classNumber = 2,
            date = "Escaneado 22 jul",
            preview = "Los ácidos de Bronsted-Lowry donan protones; la fuerza se correlaciona con la estabilidad de la base conjugada.",
            status = "Sólido"
        ),
        NoteDto(
            id = 4,
            title = "Ciclo Celular",
            className = "Biología Celular",
            classNumber = 4,
            date = "Escaneado 5 ago",
            preview = "La interfase es la fase más larga del ciclo celular, donde la célula crece y copia su ADN antes de la mitosis.",
            status = "Débil"
        ),
        NoteDto(
            id = 5,
            title = "Límites e Infinito",
            className = "Cálculo",
            classNumber = 1,
            date = "Escaneado 10 ago",
            preview = "El concepto de límite es fundamental para el cálculo diferencial e integral...",
            status = "Sólido"
        )
    )
}
