package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.AssessmentDto
import ApunteIA.FrontEnd.data.datasource.model.AttemptDto
import ApunteIA.FrontEnd.data.datasource.model.StartAttemptRequestDto
import ApunteIA.FrontEnd.data.datasource.model.SubmitAttemptRequestDto
import ApunteIA.FrontEnd.data.remote.ApiService
import ApunteIA.FrontEnd.util.LanguageManager
import javax.inject.Inject

class QuizDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : QuizDataSource {
    override suspend fun getAssessment(id: Long, language: String?): AssessmentDto =
        apiService.getAssessment(id, language ?: LanguageManager.getCurrentLanguageCode())

    override suspend fun getCourseAssessments(courseId: Long, language: String?): List<AssessmentDto> =
        apiService.getCourseAssessments(courseId, language ?: LanguageManager.getCurrentLanguageCode())

    override suspend fun startAttempt(assessmentId: Long, userId: Long): AttemptDto =
        apiService.startAttempt(assessmentId, StartAttemptRequestDto(userId))

    override suspend fun submitAttempt(attemptId: Long, answers: List<Int?>): AttemptDto =
        apiService.submitAttempt(attemptId, SubmitAttemptRequestDto(answers))

    override suspend fun getUserAttempts(userId: Long): List<AttemptDto> = apiService.getUserAttempts(userId)
}