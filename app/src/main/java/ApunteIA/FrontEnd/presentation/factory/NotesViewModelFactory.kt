package ApunteIA.FrontEnd.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ApunteIA.FrontEnd.data.datasource.NoteDataSourceImpl
import ApunteIA.FrontEnd.data.repository.NoteRepositoryImpl
import ApunteIA.FrontEnd.domain.repository.NoteRepository
import ApunteIA.FrontEnd.domain.usecase.FilterNotesUseCase
import ApunteIA.FrontEnd.domain.usecase.GetNotesUseCase
import ApunteIA.FrontEnd.presentation.viewmodel.NotesViewModel

/**
 * Arma a mano el árbol de dependencias de [NotesViewModel]
 * (dataSource -> repository -> use cases -> ViewModel) ya que el proyecto
 * no usa un framework de inyección de dependencias (Hilt/Koin).
 *
 * Si el día de mañana se agrega Hilt, esta clase se puede borrar y
 * reemplazar por `hiltViewModel()` en las pantallas.
 */
class NotesViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            val dataSource = NoteDataSourceImpl()
            val repository: NoteRepository = NoteRepositoryImpl(dataSource)
            val getNotesUseCase = GetNotesUseCase(repository)
            val filterNotesUseCase = FilterNotesUseCase()

            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(getNotesUseCase, filterNotesUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
