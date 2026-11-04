package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.AssessmentAttempt

/**
 * Regla de la nota de un estudiante en un curso (vista del administrador):
 *
 *  - Solo cuentan los intentos COMPLETADOS de evaluaciones **de ese curso**.
 *    Un curso sin evaluaciones no tiene nota (nunca se usan intentos de otras materias).
 *  - Si repitió un quiz, cuenta su **mejor intento** en esa evaluación.
 *  - La nota final es el **promedio** de esos mejores intentos, uno por evaluación.
 *
 * Así, repetir un quiz varias veces no le da más peso que a los demás.
 */
object CourseGrades {

    /** Intentos completados del estudiante que pertenecen a las evaluaciones del curso. */
    fun courseAttempts(
        courseAssessmentIds: Set<Long>,
        attempts: List<AssessmentAttempt>
    ): List<AssessmentAttempt> {
        if (courseAssessmentIds.isEmpty()) return emptyList()
        return attempts.filter { attempt ->
            StudyInsights.isCompleted(attempt) &&
                attempt.assessmentId.toLongOrNull() in courseAssessmentIds
        }
    }

    /** Mejor puntaje por evaluación del curso, promediado. null si no completó ninguna. */
    fun finalGrade(
        courseAssessmentIds: Set<Long>,
        attempts: List<AssessmentAttempt>
    ): Double? {
        val bestPerAssessment = courseAttempts(courseAssessmentIds, attempts)
            .groupBy { it.assessmentId }
            .map { (_, sameQuiz) -> sameQuiz.maxOf { it.scorePercent } }
        return if (bestPerAssessment.isEmpty()) null else bestPerAssessment.average()
    }
}
