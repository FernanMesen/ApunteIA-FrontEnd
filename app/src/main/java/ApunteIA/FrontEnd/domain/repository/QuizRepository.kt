package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.Assessment
import ApunteIA.FrontEnd.domain.model.AssessmentAttempt
import ApunteIA.FrontEnd.domain.model.QuizResult
import ApunteIA.FrontEnd.domain.model.QuizSession
import ApunteIA.FrontEnd.domain.util.Resource

interface QuizRepository {
    /** Evaluaciones de los cursos en los que está inscrito el usuario. */
    suspend fun getAvailableQuizzes(): Resource<List<Assessment>>

    /** Carga la evaluación y abre (o reutiliza) el intento del usuario. */
    suspend fun startQuiz(assessmentId: Long): Resource<QuizSession>

    /** Entrega las respuestas (una por pregunta, null = sin responder). */
    suspend fun submitQuiz(attemptId: Long, answers: List<Int?>): Resource<QuizResult>

    suspend fun getMyAttempts(): Resource<List<AssessmentAttempt>>

    // ---------- Vista del administrador ----------

    /** Ids de las evaluaciones de un curso (GET /courses/{id}/assessments). */
    suspend fun getCourseAssessmentIds(courseId: Long): Resource<Set<Long>>

    /** Intentos de cualquier usuario (GET /users/{id}/attempts). */
    suspend fun getUserAttempts(userId: Long): Resource<List<AssessmentAttempt>>
}