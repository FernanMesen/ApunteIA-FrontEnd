package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.data.repository.SubjectEnroller
import ApunteIA.FrontEnd.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingSubjectsViewModel @Inject constructor(
    private val subjectEnroller: SubjectEnroller
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()
    private val _pendingNames = MutableStateFlow<List<String>>(emptyList())
    val pendingNames: StateFlow<List<String>> = _pendingNames.asStateFlow()

    fun save(names: List<String>, onDone: () -> Unit) {
        if (_isSaving.value) return
        viewModelScope.launch {
            _isSaving.value = true
            names.map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinctBy { it.lowercase() }
                .forEach { name ->
                    if (subjectEnroller.add(name, "", "") is Resource.Error) {
                        _pendingNames.update { it + name }
                    }
                }
            _isSaving.value = false
            onDone()
        }
    }
}