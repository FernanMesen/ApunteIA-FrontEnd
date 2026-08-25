package ApunteIA.FrontEnd.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ApunteIA.FrontEnd.data.datasource.NoteDataSourceImpl
import ApunteIA.FrontEnd.data.repository.NoteRepositoryImpl
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import ApunteIA.FrontEnd.domain.usecase.AddNoteUseCase
import ApunteIA.FrontEnd.presentation.viewmodel.ScanViewModel

class ScanViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            val dataSource = NoteDataSourceImpl()
            val repository: NoteRepository = NoteRepositoryImpl(dataSource)
            val addNoteUseCase = AddNoteUseCase(repository)

            @Suppress("UNCHECKED_CAST")
            return ScanViewModel(addNoteUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}