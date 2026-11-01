package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import ApunteIA.FrontEnd.util.AiLanguageOption
import ApunteIA.FrontEnd.util.AiLanguagePreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val aiLanguagePreferenceManager: AiLanguagePreferenceManager
) : ViewModel() {
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _aiLanguageOption = MutableStateFlow(aiLanguagePreferenceManager.aiLanguageOption)
    val aiLanguageOption: StateFlow<AiLanguageOption> = _aiLanguageOption.asStateFlow()

    fun toggleTheme(isDark: Boolean) {
        _isDarkMode.value = isDark
    }

    fun setAiLanguageOption(option: AiLanguageOption) {
        aiLanguagePreferenceManager.aiLanguageOption = option
        _aiLanguageOption.value = option
    }
}
