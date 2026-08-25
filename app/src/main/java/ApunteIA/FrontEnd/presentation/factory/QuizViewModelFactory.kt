package ApunteIA.FrontEnd.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ApunteIA.FrontEnd.data.datasource.QuizDataSourceImpl
import ApunteIA.FrontEnd.data.repository.QuizRepositoryImpl
import ApunteIA.FrontEnd.domain.usecase.GetQuizUseCase
import ApunteIA.FrontEnd.presentation.viewmodel.QuizViewModel

class QuizViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            val dataSource = QuizDataSourceImpl()
            val repository = QuizRepositoryImpl(dataSource)
            val getQuizUseCase = GetQuizUseCase(repository)
            
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(getQuizUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
