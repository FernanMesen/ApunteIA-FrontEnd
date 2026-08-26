package ApunteIA.FrontEnd.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ApunteIA.FrontEnd.data.datasource.TopicDataSourceImpl
import ApunteIA.FrontEnd.data.repository.TopicRepositoryImpl
import ApunteIA.FrontEnd.domain.usecase.GetTopicsUseCase
import ApunteIA.FrontEnd.presentation.viewmodel.ManageSubjectsViewModel

class ManageSubjectsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageSubjectsViewModel::class.java)) {
            val dataSource = TopicDataSourceImpl()
            val repository = TopicRepositoryImpl(dataSource)
            val getTopicsUseCase = GetTopicsUseCase(repository)
            
            @Suppress("UNCHECKED_CAST")
            return ManageSubjectsViewModel(getTopicsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
