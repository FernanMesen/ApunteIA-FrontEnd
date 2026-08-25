package ApunteIA.FrontEnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.domain.model.Note
import ApunteIA.FrontEnd.domain.model.NoteStatus
import ApunteIA.FrontEnd.domain.usecase.AddNoteUseCase
import ApunteIA.FrontEnd.presentation.model.CapturedPage
import ApunteIA.FrontEnd.presentation.model.MaterialType
import ApunteIA.FrontEnd.presentation.model.NoteBullet
import ApunteIA.FrontEnd.presentation.model.NoteSection
import ApunteIA.FrontEnd.presentation.model.ProcessingError
import ApunteIA.FrontEnd.presentation.model.ProcessingStep
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Materias disponibles para clasificar el escaneo. En un backend real esto
 * vendría de las materias que el usuario configuró en el onboarding.
 */
val SCAN_SUBJECTS = listOf("Química Orgánica", "Biología Celular", "Cálculo II")

data class ScanUiState(
    // --- Paso 1: Nuevo escaneo ---
    val subject: String = SCAN_SUBJECTS.first(),
    val className: String = "",
    val date: String = "",
    val materialType: MaterialType = MaterialType.HANDWRITTEN,
    val multiPageScan: Boolean = true,

    // --- Paso 2/3: Cámara y páginas capturadas ---
    val capturedPages: List<CapturedPage> = emptyList(),

    // --- Paso 4: Revisar nota ---
    val title: String = "",
    val sections: List<NoteSection> = emptyList(),
    val tags: List<String> = emptyList(),
    val tagInput: String = "",
    val showingOriginal: Boolean = false,
    val originalText: String = "",
    val isProcessing: Boolean = false,
    val completedSteps: Int = 0,
    val processingError: ProcessingError? = null,
    val saved: Boolean = false
) {
    val canContinueFromNewScan: Boolean
        get() = className.isNotBlank()

    val hasCapturedPages: Boolean
        get() = capturedPages.isNotEmpty()

    /** true una vez que las 5 etapas de análisis terminaron sin error. */
    val processingDone: Boolean
        get() = !isProcessing && processingError == null && completedSteps >= ProcessingStep.entries.size
}

/**
 * Maneja el estado compartido a lo largo de todo el flujo de escaneo. Al
 * escoparse a la ruta padre ("scan_graph") en la navegación, las 4 pantallas
 * del flujo comparten la misma instancia sin tener que pasarse datos por
 * argumentos de navegación.
 */
class ScanViewModel(
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

    private var nextPageId = 1
    private var nextSectionId = 1
    private var nextBulletId = 1
    private var processingJob: Job? = null

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    /**
     * Al descartarse el ViewModel (p.ej. el usuario cierra el flujo con la X,
     * o guarda la nota y sale) se eliminan los archivos de fotos que hayan
     * quedado en el almacenamiento privado de la app para no acumular basura.
     */
    override fun onCleared() {
        super.onCleared()
        _uiState.value.capturedPages.forEach { page ->
            runCatching { page.imageFile.delete() }
        }
    }

    // ---------- Paso 1: Nuevo escaneo ----------

    fun selectSubject(subject: String) {
        _uiState.update { it.copy(subject = subject) }
    }

    fun onClassNameChange(value: String) {
        _uiState.update { it.copy(className = value) }
    }

    fun onDateChange(value: String) {
        _uiState.update { it.copy(date = value) }
    }

    fun selectMaterialType(type: MaterialType) {
        _uiState.update { it.copy(materialType = type) }
    }

    fun toggleMultiPageScan(enabled: Boolean) {
        _uiState.update { it.copy(multiPageScan = enabled) }
    }

    // ---------- Paso 2: Cámara ----------

    fun capturePage(imageFile: java.io.File) {
        _uiState.update { state ->
            val newPage = CapturedPage(id = nextPageId++, imageFile = imageFile)
            state.copy(capturedPages = state.capturedPages + newPage)
        }
    }

    // ---------- Paso 3: Páginas capturadas ----------

    fun removePage(id: Int) {
        _uiState.update { state ->
            val page = state.capturedPages.firstOrNull { it.id == id }
            page?.let { runCatching { it.imageFile.delete() } }
            state.copy(capturedPages = state.capturedPages.filterNot { it.id == id })
        }
    }

    fun movePage(id: Int, offset: Int) {
        _uiState.update { state ->
            val pages = state.capturedPages.toMutableList()
            val index = pages.indexOfFirst { it.id == id }
            val newIndex = index + offset
            if (index == -1 || newIndex !in pages.indices) return@update state
            val page = pages.removeAt(index)
            pages.add(newIndex, page)
            state.copy(capturedPages = pages)
        }
    }

    /**
     * Simula el análisis por IA del escaneo, avanzando paso a paso por
     * [ProcessingStep] (subir, leer, organizar, comparar, generar) para que
     * la pantalla de procesamiento pueda ir marcando cada etapa como
     * completada, igual que lo haría una llamada real y progresiva al backend.
     */
    fun confirmAndProcess() {
        processingJob?.cancel()
        _uiState.update { it.copy(isProcessing = true, processingError = null, completedSteps = 0) }
        processingJob = viewModelScope.launch {
            for (step in ProcessingStep.entries) {
                delay(700)
                _uiState.update { it.copy(completedSteps = it.completedSteps + 1) }
            }
            val demo = demoContentFor(_uiState.value.subject, _uiState.value.className)
            _uiState.update { state ->
                state.copy(
                    isProcessing = false,
                    title = demo.title,
                    sections = demo.sections,
                    originalText = demo.originalText
                )
            }
        }
    }

    /**
     * Interrumpe el análisis en curso y muestra un error simulado. Usado por
     * los enlaces "Simular: ..." de la pantalla de procesamiento para poder
     * probar los estados de error sin depender de un backend real.
     */
    fun simulateError(error: ProcessingError) {
        processingJob?.cancel()
        _uiState.update { it.copy(isProcessing = false, processingError = error) }
    }

    // ---------- Paso 4: Revisar nota ----------

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun setShowingOriginal(showOriginal: Boolean) {
        _uiState.update { it.copy(showingOriginal = showOriginal) }
    }

    fun addSection() {
        _uiState.update { state ->
            val newSection = NoteSection(id = nextSectionId++, title = "Nueva sección", bullets = emptyList())
            state.copy(sections = state.sections + newSection)
        }
    }

    fun removeSection(sectionId: Int) {
        _uiState.update { state ->
            state.copy(sections = state.sections.filterNot { it.id == sectionId })
        }
    }

    fun onSectionTitleChange(sectionId: Int, title: String) {
        updateSection(sectionId) { it.copy(title = title) }
    }

    fun addBullet(sectionId: Int) {
        updateSection(sectionId) { section ->
            section.copy(bullets = section.bullets + NoteBullet(id = nextBulletId++, text = ""))
        }
    }

    fun removeBullet(sectionId: Int, bulletId: Int) {
        updateSection(sectionId) { section ->
            section.copy(bullets = section.bullets.filterNot { it.id == bulletId })
        }
    }

    fun onBulletTextChange(sectionId: Int, bulletId: Int, text: String) {
        updateSection(sectionId) { section ->
            section.copy(bullets = section.bullets.map { bullet ->
                if (bullet.id == bulletId) bullet.copy(text = text) else bullet
            })
        }
    }

    private inline fun updateSection(sectionId: Int, transform: (NoteSection) -> NoteSection) {
        _uiState.update { state ->
            state.copy(sections = state.sections.map { section ->
                if (section.id == sectionId) transform(section) else section
            })
        }
    }

    fun onTagInputChange(value: String) {
        _uiState.update { it.copy(tagInput = value) }
    }

    fun addTag() {
        _uiState.update { state ->
            val tag = state.tagInput.trim()
            if (tag.isEmpty() || state.tags.contains(tag)) return@update state
            state.copy(tags = state.tags + tag, tagInput = "")
        }
    }

    fun removeTag(tag: String) {
        _uiState.update { state -> state.copy(tags = state.tags.filterNot { it == tag }) }
    }

    /**
     * Guarda la nota generada en el repositorio de Notas y marca el flujo
     * como completado ([ScanUiState.saved]) para que la UI navegue de vuelta.
     */
    fun saveNote() {
        val state = _uiState.value
        val preview = state.sections
            .flatMap { it.bullets }
            .joinToString(separator = " ") { it.text }
            .ifBlank { "Nota generada a partir de tu escaneo." }

        val note = Note(
            id = System.currentTimeMillis().toInt(),
            title = state.title.ifBlank { "Nota sin título" },
            className = state.subject,
            classNumber = state.className.filter { it.isDigit() }.toIntOrNull() ?: 1,
            date = "Escaneado hoy",
            preview = preview,
            status = NoteStatus.WEAK
        )

        viewModelScope.launch {
            addNoteUseCase(note)
            _uiState.update { it.copy(saved = true) }
        }
    }

    private data class DemoContent(
        val title: String,
        val sections: List<NoteSection>,
        val originalText: String
    )

    private fun demoContentFor(subject: String, className: String): DemoContent {
        val label = className.ifBlank { "tus notas" }
        return when (subject) {
            "Química Orgánica" -> DemoContent(
                title = "Estereoquímica",
                sections = listOf(
                    NoteSection(
                        id = nextSectionId++,
                        title = "Quiralidad",
                        bullets = listOf(
                            NoteBullet(nextBulletId++, "Un centro quiral es un carbono unido a cuatro sustituyentes distintos."),
                            NoteBullet(nextBulletId++, "Los enantiómeros son imágenes especulares no superponibles.")
                        )
                    ),
                    NoteSection(
                        id = nextSectionId++,
                        title = "Actividad óptica",
                        bullets = listOf(
                            NoteBullet(nextBulletId++, "Las configuraciones R y S rotan la luz polarizada en direcciones opuestas."),
                            NoteBullet(nextBulletId++, "Una mezcla racémica —partes iguales de ambos enantiómeros— es ópticamente inactiva.")
                        )
                    )
                ),
                originalText = "Notas manuscritas de $label sobre quiralidad, enantiómeros y actividad óptica."
            )
            "Biología Celular" -> DemoContent(
                title = "Ciclo Celular",
                sections = listOf(
                    NoteSection(
                        id = nextSectionId++,
                        title = "Interfase",
                        bullets = listOf(
                            NoteBullet(nextBulletId++, "Es la fase más larga del ciclo celular."),
                            NoteBullet(nextBulletId++, "La célula crece y copia su ADN antes de la mitosis.")
                        )
                    ),
                    NoteSection(
                        id = nextSectionId++,
                        title = "Mitosis",
                        bullets = listOf(
                            NoteBullet(nextBulletId++, "Se divide en profase, metafase, anafase y telofase."),
                            NoteBullet(nextBulletId++, "Termina con la citocinesis, que separa la célula en dos.")
                        )
                    )
                ),
                originalText = "Notas manuscritas de $label sobre las fases del ciclo celular."
            )
            else -> DemoContent(
                title = "Resumen de $label",
                sections = listOf(
                    NoteSection(
                        id = nextSectionId++,
                        title = "Conceptos clave",
                        bullets = listOf(
                            NoteBullet(nextBulletId++, "Primer punto identificado a partir del escaneo."),
                            NoteBullet(nextBulletId++, "Segundo punto identificado a partir del escaneo.")
                        )
                    )
                ),
                originalText = "Texto reconocido a partir de $label."
            )
        }
    }
}