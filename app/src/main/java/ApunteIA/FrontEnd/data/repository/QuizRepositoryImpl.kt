package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.QuizDataSource
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.mapper.toQuizError
import ApunteIA.FrontEnd.data.mapper.toResult
import ApunteIA.FrontEnd.data.mapper.toSession
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.domain.model.Assessment
import ApunteIA.FrontEnd.domain.model.AssessmentAttempt
import ApunteIA.FrontEnd.domain.model.QuizResult
import ApunteIA.FrontEnd.domain.model.QuizSession
import ApunteIA.FrontEnd.domain.repository.QuizRepository
import ApunteIA.FrontEnd.domain.usecase.StudyInsights
import ApunteIA.FrontEnd.domain.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val dataSource: QuizDataSource,
    private val loader: StudyDataLoader
) : QuizRepository {

    private val errorMapper: (Throwable) -> String = { it.toQuizError().toUserMessage() }

    override suspend fun getAvailableQuizzes(): Resource<List<Assessment>> = safeApiCall(errorMapper) {
        coroutineScope {
            val attempts = async { runCatching { loader.myAttempts() }.getOrDefault(emptyList()) }
            val courses = loader.myCourses()
            val assessments = courses
                .map { course -> async { dataSource.getCourseAssessments(course.id) } }
                .awaitAll()
                .flatten()
                .distinctBy { it.id }
            val completedIds = attempts.await()
                .filter { StudyInsights.isCompleted(it) }
                .map { it.assessmentId }
                .toSet()
            assessments
                .map { it.toDomain().copy(isCompleted = it.id.toString() in completedIds) }
                .sortedByDescending { it.id }
        }
    }

    override suspend fun startQuiz(assessmentId: Long): Resource<QuizSession> = safeApiCall(errorMapper) {
        val userId = loader.requireUserId()
        val assessment = dataSource.getAssessment(assessmentId)
        val attempt = try {
            dataSource.startAttempt(assessmentId, userId)
        } catch (e: HttpException) {
            // 409: ya hay un intento sin terminar de esta evaluación → se reutiliza.
            if (e.code() != 409) throw e
            dataSource.getUserAttempts(userId).firstOrNull {
                it.assessmentId == assessmentId && !it.status.equals("COMPLETED", ignoreCase = true)
            } ?: throw e
        }
        val session = assessment.toSession(attempt.id)
        if (session.questions.isEmpty()) throw IllegalStateException("La evaluación no tiene preguntas.")
        session
    }

    override suspend fun submitQuiz(attemptId: Long, answers: List<Int?>): Resource<QuizResult> =
        safeApiCall(errorMapper) {
            loader.invalidate()
            dataSource.submitAttempt(attemptId, answers).toResult()
        }

    override suspend fun getMyAttempts(): Resource<List<AssessmentAttempt>> = safeApiCall(errorMapper) {
        loader.myAttempts()
    }

    override suspend fun getCourseAssessmentIds(courseId: Long): Resource<Set<Long>> = safeApiCall(errorMapper) {
        dataSource.getCourseAssessments(courseId).map { it.id }.toSet()
    }

    override suspend fun getUserAttempts(userId: Long): Resource<List<AssessmentAttempt>> = safeApiCall(errorMapper) {
        dataSource.getUserAttempts(userId).map { it.toDomain() }
    }
}