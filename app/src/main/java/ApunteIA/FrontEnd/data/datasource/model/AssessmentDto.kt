package ApunteIA.FrontEnd.data.datasource.model

import com.google.gson.annotations.SerializedName

/** AssessmentResponse del backend: nunca trae la opción correcta. */
data class AssessmentDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("difficulty")
    val difficulty: String?,
    @SerializedName("durationMinutes")
    val durationMinutes: Int?,
    @SerializedName("courseId")
    val courseId: Long?,
    @SerializedName("courseName")
    val courseName: String?,
    @SerializedName("totalQuestions")
    val totalQuestions: Int?,
    @SerializedName("questions")
    val questions: List<QuestionResponseDto>?
)

data class QuestionResponseDto(
    @SerializedName("index")
    val index: Int,
    @SerializedName("text")
    val text: String?,
    @SerializedName("options")
    val options: List<String>?
)

data class GenerateAssessmentRequestDto(
    @SerializedName("questionCount")
    val questionCount: Int,
    /** EASY | MEDIUM | HARD */
    @SerializedName("difficulty")
    val difficulty: String
)

data class StartAttemptRequestDto(
    @SerializedName("userId")
    val userId: Long
)

/** Opción elegida por pregunta en el orden de la evaluación (null = sin responder). */
data class SubmitAttemptRequestDto(
    @SerializedName("answers")
    val answers: List<Int?>
)

data class AttemptDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("assessmentId")
    val assessmentId: Long,
    @SerializedName("assessmentTitle")
    val assessmentTitle: String?,
    @SerializedName("userId")
    val userId: Long?,
    /** IN_PROGRESS | COMPLETED */
    @SerializedName("status")
    val status: String?,
    /** Porcentaje 0..100 (BigDecimal en el backend). */
    @SerializedName("score")
    val score: Double?,
    @SerializedName("startedAt")
    val startedAt: String?,
    @SerializedName("completedAt")
    val completedAt: String?,
    @SerializedName("durationSeconds")
    val durationSeconds: Long?,
    @SerializedName("answers")
    val answers: List<AttemptAnswerDto>?
)

data class AttemptAnswerDto(
    @SerializedName("questionIndex")
    val questionIndex: Int,
    @SerializedName("selectedOptionIndex")
    val selectedOptionIndex: Int?,
    @SerializedName("correct")
    val correct: Boolean,
    /** Solo viene cuando el intento ya fue entregado (backend >= versión final). */
    @SerializedName("correctOptionIndex")
    val correctOptionIndex: Int? = null
)