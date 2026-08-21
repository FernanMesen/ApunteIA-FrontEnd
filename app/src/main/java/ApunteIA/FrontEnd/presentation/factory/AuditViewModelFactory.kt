package ApunteIA.FrontEnd.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ApunteIA.FrontEnd.data.datasource.TopicDataSourceImpl
import ApunteIA.FrontEnd.data.repository.TopicRepositoryImpl
import ApunteIA.FrontEnd.domain.repository.TopicRepository
import ApunteIA.FrontEnd.domain.usecase.CalculateAuditSummaryUseCase
import ApunteIA.FrontEnd.domain.usecase.FilterTopicsUseCase
import ApunteIA.FrontEnd.domain.usecase.GetTopicsUseCase
import ApunteIA.FrontEnd.presentation.viewmodel.AuditViewModel

class AuditViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuditViewModel::class.java)) {
            val dataSource = TopicDataSourceImpl()
            val repository: TopicRepository = TopicRepositoryImpl(dataSource)
            @Suppress("UNCHECKED_CAST")
            return AuditViewModel(
                GetTopicsUseCase(repository),
                FilterTopicsUseCase(),
                CalculateAuditSummaryUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}