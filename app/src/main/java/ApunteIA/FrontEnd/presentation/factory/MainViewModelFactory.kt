package ApunteIA.FrontEnd.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ApunteIA.FrontEnd.data.datasource.MainDataSourceImpl
import ApunteIA.FrontEnd.data.repository.MainRepositoryImpl
import ApunteIA.FrontEnd.domain.usecase.GetMainDashboardUseCase
import ApunteIA.FrontEnd.presentation.viewmodel.MainViewModel

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val dataSource = MainDataSourceImpl()
            val repository = MainRepositoryImpl(dataSource)
            val useCase = GetMainDashboardUseCase(repository)
            
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
