package ApunteIA.FrontEnd.presentation.viewmodel

import ApunteIA.FrontEnd.R
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.model.UserAnswer
import ApunteIA.FrontEnd.domain.usecase.StartQuizUseCase
import ApunteIA.FrontEnd.domain.usecase.SubmitQuizUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.util.L10n
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val title: String = "",
    /** true cuando se conocen las respuestas correctas y se pueden repetir las incorrectas. */
    val canRepeat: Boolean = false
)

/**
 * Resuelve una evaluación real del backend:
 *  1. GET /assessments/{id} + POST /assessments/{id}/attempts (abre el intento).
 *  2. El usuario elige una opción por pregunta (el backend no revela la correcta).
 *  3. PATCH /attempts/{id}/submit → el backend califica y devuelve el resultado.
 * Después, "Repetir incorrectas" es práctica local con retroalimentación inmediata.
 */
@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val startQuizUseCase: StartQuizUseCase,
    private val submitQuizUseCase: SubmitQuizUseCase
) : ViewModel() {

    private val assessmentId: Long = savedStateHandle.get<Long>(ASSESSMENT_ID_ARG) ?: 0L
    private var attemptId: Long? = null
    private var graded = false

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _isAnswered = MutableStateFlow(false)
    val isAnswered: StateFlow<Boolean> = _isAnswered.asStateFlow()

    private val _userAnswers = mutableStateListOf<UserAnswer?>()
    val userAnswers: List<UserAnswer?> get() = _userAnswers

    private val _showResults = MutableStateFlow(false)
    val showResults: StateFlow<Boolean> = _showResults.asStateFlow()

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = startQuizUseCase(assessmentId)) {
                is Resource.Success -> {
                    val session = result.data
                    attemptId = session.attemptId
                    graded = false
                    _questions.value = session.questions
                    _currentQuestionIndex.value = 0
                    _selectedOptionIndex.value = null
                    _isAnswered.value = false
                    _showResults.value = false
                    _userAnswers.clear()
                    repeat(session.questions.size) { _userAnswers.add(null) }
                    _uiState.value = QuizUiState(isLoading = false, title = session.title)
                }
                is Resource.Error -> {
                    _questions.value = emptyList()
                    _uiState.value = QuizUiState(isLoading = false, error = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    /** Reintenta lo que falló: entregar si ya se respondió todo, o cargar de nuevo. */
    fun refresh() {
        if (attemptId != null && !graded && _questions.value.isNotEmpty() && _userAnswers.all { it != null }) {
            submit()
        } else {
            loadQuestions()
        }
    }

    fun onOptionSelected(index: Int) {
        val questions = _questions.value
        if (questions.isEmpty()) return
        val current = _currentQuestionIndex.value
        val question = questions[current]
        val revealsAnswer = question.correctAnswerIndex >= 0

        if (revealsAnswer) {
            // Práctica local: se marca correcta/incorrecta al instante y no se puede cambiar.
            if (_isAnswered.value) return
            _selectedOptionIndex.value = index
            _isAnswered.value = true
            _userAnswers[current] = UserAnswer(
                questionId = question.id,
                selectedOptionIndex = index,
                isCorrect = index == question.correctAnswerIndex
            )
        } else {
            // Evaluación real: se puede cambiar de opción hasta pasar a la siguiente.
            _selectedOptionIndex.value = index
            _isAnswered.value = true
            _userAnswers[current] = UserAnswer(
                questionId = question.id,
                selectedOptionIndex = index,
                isCorrect = false
            )
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value++
            _selectedOptionIndex.value = null
            _isAnswered.value = false
        } else if (!graded) {
            submit()
        } else {
            _showResults.value = true
        }
    }

    private fun submit() {
        val id = attemptId ?: return
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, error = null)
            val answers = _userAnswers.map { it?.selectedOptionIndex }
            when (val result = submitQuizUseCase(id, answers)) {
                is Resource.Success -> {
                    graded = true
                    val gradedByIndex = result.data.answers.associateBy { it.questionIndex }
                    val updatedQuestions = _questions.value.mapIndexed { index, question ->
                        val correct = gradedByIndex[index]?.correctOptionIndex
                        question.copy(
                            correctAnswerIndex = correct ?: -1,
                            explanation = if (correct != null) {
                                L10n.get(R.string.quiz_correct_answer_prefix, question.options.getOrNull(correct).orEmpty())
                            } else {
                                ""
                            }
                        )
                    }
                    _questions.value = updatedQuestions
                    _questions.value.forEachIndexed { index, question ->
                        val answer = gradedByIndex[index]
                        _userAnswers[index] = UserAnswer(
                            questionId = question.id,
                            selectedOptionIndex = answer?.selectedOptionIndex ?: -1,
                            isCorrect = answer?.isCorrect ?: false
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        canRepeat = updatedQuestions.all { it.correctAnswerIndex >= 0 }
                    )
                    _showResults.value = true
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, error = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun repeatIncorrects() {
        if (!_uiState.value.canRepeat) return
        val incorrectQuestions = _questions.value.filterIndexed { index, _ ->
            _userAnswers.getOrNull(index)?.isCorrect == false
        }
        if (incorrectQuestions.isNotEmpty()) {
            _questions.value = incorrectQuestions
            _currentQuestionIndex.value = 0
            _selectedOptionIndex.value = null
            _isAnswered.value = false
            _showResults.value = false
            _userAnswers.clear()
            repeat(incorrectQuestions.size) { _userAnswers.add(null) }
        }
    }

    companion object {
        const val ASSESSMENT_ID_ARG = "assessmentId"
    }
}
