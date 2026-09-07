package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.Question
import ApunteIA.FrontEnd.domain.model.UserAnswer
import ApunteIA.FrontEnd.domain.usecase.GetQuizUseCase
import ApunteIA.FrontEnd.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuizUseCase: GetQuizUseCase
) : ViewModel() {

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

    private fun loadQuestions() {
        viewModelScope.launch {
            when (val result = getQuizUseCase()) {
                is Resource.Success -> {
                    val questionsList = result.data
                    _questions.value = questionsList
                    _userAnswers.clear()
                    repeat(questionsList.size) { _userAnswers.add(null) }
                }
                is Resource.Error -> {
                    _questions.value = emptyList()
                }
                is Resource.Loading -> { }
            }
        }
    }

    fun onOptionSelected(index: Int) {
        if (!_isAnswered.value) {
            val currentQuestion = _questions.value[_currentQuestionIndex.value]
            _selectedOptionIndex.value = index
            _isAnswered.value = true
            _userAnswers[_currentQuestionIndex.value] = UserAnswer(
                questionId = currentQuestion.id,
                selectedOptionIndex = index,
                isCorrect = index == currentQuestion.correctAnswerIndex
            )
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value++
            _selectedOptionIndex.value = null
            _isAnswered.value = false
        } else {
            _showResults.value = true
        }
    }

    fun repeatIncorrects() {
        val incorrectQuestions = _questions.value.filterIndexed { index, _ ->
            _userAnswers[index]?.isCorrect == false
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
}
