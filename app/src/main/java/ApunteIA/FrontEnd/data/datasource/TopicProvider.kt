package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.TopicDto

object TopicProvider {
    var topics: List<TopicDto> = listOf(
        TopicDto(1, "Estructura Atómica y Enlaces", "Química Orgánica", "Sólido", 100),
        TopicDto(2, "Alcanos y Nomenclatura", "Química Orgánica", "Sólido", 98),
        TopicDto(3, "Química Ácido-Base", "Química Orgánica", "Sólido", 96),
        TopicDto(4, "Estereoquímica", "Química Orgánica", "Débil", 50),
        TopicDto(5, "Mecanismos de Reacción (SN1/SN2)", "Química Orgánica", "Débil", 47),
        TopicDto(6, "Espectroscopía IR y RMN", "Química Orgánica", "Faltante", 0),
        TopicDto(7, "Ciclo Celular", "Biología Celular", "Sólido", 94),
        TopicDto(8, "Transporte en la Membrana Celular", "Biología Celular", "Débil", 45),
        TopicDto(9, "División Celular (Mitosis y Meiosis)", "Biología Celular", "Faltante", 0),
        TopicDto(10, "Límites e Infinito", "Cálculo", "Faltante", 0)
    )

    fun addCourse(name: String) {
        val nextId = (topics.maxOfOrNull { it.id } ?: 0) + 1
        topics = topics + TopicDto(nextId, "Introducción al curso", name, "Faltante", 0)
    }
}