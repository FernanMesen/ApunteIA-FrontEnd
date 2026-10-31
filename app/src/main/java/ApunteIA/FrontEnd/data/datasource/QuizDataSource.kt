package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.AssessmentDto
import ApunteIA.FrontEnd.data.datasource.model.AttemptDto

interface QuizDataSource {
    suspend fun getAssessment(id: Long): AssessmentDto
    suspend fun getCourseAssessments(courseId: Long): List<AssessmentDto>
    suspend fun startAttempt(assessmentId: Long, userId: Long): AttemptDto
    suspend fun submitAttempt(attemptId: Long, answers: List<Int?>): AttemptDto
    suspend fun getUserAttempts(userId: Long): List<AttemptDto>
}