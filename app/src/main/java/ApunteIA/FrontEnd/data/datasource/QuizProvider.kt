package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.domain.model.Question

object QuizProvider {
    fun getQuestions(): List<Question> = listOf(
        Question(
            1,
            "¿Qué término describe moléculas que son imágenes especulares entre sí pero no superponibles?",
            listOf("Enantiómeros", "Diastereómeros", "Confórmeros", "Isótopos"),
            0,
            "Los enantiómeros son isómeros ópticos que son imágenes especulares no superponibles entre sí."
        ),
        Question(
            2,
            "Una mezcla racémica contiene:",
            listOf(
                "Partes iguales de ambos enantiómeros",
                "Solo el enantiómero R",
                "Un solo compuesto aquiral",
                "Dos diastereómeros en proporción 3:1"
            ),
            0,
            "Una mezcla racémica es una combinación 50:50 de ambos enantiómeros, por lo que no muestra rotación óptica neta."
        ),
        Question(
            3,
            "¿Cuál es el ángulo de enlace aproximado en un átomo de carbono con hibridación sp3?",
            listOf("90°", "120°", "109.5°", "180°"),
            2,
            "La geometría tetraédrica de la hibridación sp3 resulta en ángulos de enlace de 109.5°."
        ),
        Question(
            4,
            "La regla de Markovnikov se aplica a:",
            listOf("Combustión", "Adición electrofílica", "Sustitución nucleofílica", "Eliminación"),
            1,
            "La regla de Markovnikov predice el resultado de las reacciones de adición electrofílica a alquenos asimétricos."
        ),
        Question(
            5,
            "¿Qué grupo funcional es característico de los alcoholes?",
            listOf("-CHO", "-COOH", "-OH", "-NH2"),
            2,
            "El grupo hidroxilo (-OH) unido a un carbono saturado define a los alcoholes."
        )
    )
}
