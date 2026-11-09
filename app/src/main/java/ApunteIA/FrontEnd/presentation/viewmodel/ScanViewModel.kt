package ApunteIA.FrontEnd.presentation.viewmodel

import ApunteIA.FrontEnd.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ApunteIA.FrontEnd.data.di.ApplicationScope
import ApunteIA.FrontEnd.domain.model.CourseInfo
import ApunteIA.FrontEnd.domain.usecase.CreateNoteUseCase
import ApunteIA.FrontEnd.domain.usecase.DeleteNoteUseCase
import ApunteIA.FrontEnd.domain.usecase.GetMyCoursesUseCase
import ApunteIA.FrontEnd.domain.usecase.ProcessNoteWithAiUseCase
import ApunteIA.FrontEnd.domain.usecase.RecognizeTextUseCase
import ApunteIA.FrontEnd.domain.usecase.UpdateNoteUseCase
import ApunteIA.FrontEnd.util.AiLanguagePreferenceManager
import ApunteIA.FrontEnd.util.L10n
import ApunteIA.FrontEnd.util.PermissionManager
import ApunteIA.FrontEnd.domain.util.IsoDates
import ApunteIA.FrontEnd.domain.util.Resource
import ApunteIA.FrontEnd.presentation.model.CapturedPage
import ApunteIA.FrontEnd.presentation.model.MaterialType
import ApunteIA.FrontEnd.presentation.model.NoteBullet
import ApunteIA.FrontEnd.presentation.model.NoteSection
import ApunteIA.FrontEnd.presentation.model.ProcessingError
import ApunteIA.FrontEnd.presentation.model.ProcessingStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class ScanUiState(
    // --- Paso 1: Nuevo escaneo ---
    /** Materias (cursos inscritos) disponibles para clasificar el apunte. */
    val subjects: List<String> = emptyList(),
    val isLoadingSubjects: Boolean = false,
    val subjectsError: String? = null,
    val subject: String = "",
    val className: String = "",
    /** Fecha del apunte en ISO ("yyyy-MM-dd"); por defecto hoy. */
    val date: String = IsoDates.todayIsoDate(),
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
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val saved: Boolean = false
) {
    val canContinueFromNewScan: Boolean
        get() = className.isNotBlank() && subject in subjects

    val hasCapturedPages: Boolean
        get() = capturedPages.isNotEmpty()

    /** true una vez que todas las etapas de análisis terminaron sin error. */
    val processingDone: Boolean
        get() = !isProcessing && processingError == null && completedSteps >= ProcessingStep.entries.size
}

/**
 * Estado compartido por todo el flujo de escaneo (las 5 pantallas usan la misma
 * instancia porque se escopa a la ruta padre "scan_graph").
 *
 * Flujo real:
 *  1. OCR en el dispositivo (ML Kit) de las fotos tomadas con CameraX.
 *  2. POST /notes con el texto reconocido.
 *  3. POST /notes/{id}/process → la IA del backend (OpenAI) genera el resumen.
 *  4. El usuario revisa/edita y "Guardar nota" hace PUT /notes/{id}.
 * Si el usuario sale sin guardar, la nota borrador se elimina.
 */
@HiltViewModel
class ScanViewModel @Inject constructor(
    private val getMyCourses: GetMyCoursesUseCase,
    private val recognizeText: RecognizeTextUseCase,
    private val createNote: CreateNoteUseCase,
    private val processNoteWithAi: ProcessNoteWithAiUseCase,
    private val updateNote: UpdateNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
    private val aiLanguagePreferenceManager: AiLanguagePreferenceManager,
    private val permissionManager: PermissionManager,
    @ApplicationScope private val appScope: CoroutineScope
) : ViewModel() {

    val photosMode: StateFlow<String> = permissionManager.photosMode
    val cameraMode: StateFlow<String> = permissionManager.cameraMode

    private var nextPageId = 1
    private var nextSectionId = 1
    private var nextBulletId = 1
    private var processingJob: Job? = null

    private var courses: List<CourseInfo> = emptyList()
    /** Nota creada en el backend durante el procesamiento (borrador hasta "Guardar"). */
    private var draftNoteId: Long? = null
    /** true si la nota debe conservarse aunque se salga del flujo (guardada o IA fallida). */
    private var keepDraft = false

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    init {
        loadSubjects()
    }

    /**
     * Al descartarse el ViewModel (el usuario cierra el flujo con la X, o guarda la
     * nota y sale) se borran las fotos del almacenamiento privado de la app y, si
     * no se guardó, la nota borrador del backend.
     */
    override fun onCleared() {
        super.onCleared()
        _uiState.value.capturedPages.forEach { page ->
            runCatching { page.imageFile.delete() }
        }
        val draft = draftNoteId
        if (draft != null && !keepDraft) {
            appScope.launch { deleteNote(draft) }
        }
    }

    fun loadSubjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSubjects = true, subjectsError = null) }
            when (val result = getMyCourses()) {
                is Resource.Success -> {
                    courses = result.data
                    val names = result.data.map { it.name }
                    _uiState.update { state ->
                        state.copy(
                            isLoadingSubjects = false,
                            subjects = names,
                            subject = state.subject.takeIf { it in names } ?: names.firstOrNull().orEmpty(),
                            subjectsError = if (names.isEmpty()) {
                                L10n.getString(R.string.scan_no_subjects)
                            } else {
                                null
                            }
                        )
                    }
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isLoadingSubjects = false, subjectsError = result.message)
                }
                is Resource.Loading -> Unit
            }
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

    // ---------- Procesamiento real (OCR + backend + IA) ----------

    /** Inicia el análisis solo si no está en curso ni terminado (p. ej. al rotar la pantalla). */
    fun startProcessingIfNeeded() {
        val state = _uiState.value
        if (state.isProcessing || state.processingDone || state.processingError != null) return
        confirmAndProcess()
    }

    /** Prepara un nuevo análisis (al confirmar páginas o al pedir "Procesar de nuevo"). */
    fun resetProcessing() {
        processingJob?.cancel()
        _uiState.update { it.copy(isProcessing = false, processingError = null, completedSteps = 0) }
    }

    fun confirmAndProcess() {
        processingJob?.cancel()
        _uiState.update { it.copy(isProcessing = true, processingError = null, completedSteps = 0, saveError = null) }
        processingJob = viewModelScope.launch {
            val state = _uiState.value
            val course = courses.firstOrNull { it.name == state.subject }
            if (course == null) {
                fail(ProcessingError.NO_COURSE)
                return@launch
            }

            // Si se está reprocesando, se elimina el borrador anterior para no duplicar notas.
            draftNoteId?.let { previous ->
                deleteNote(previous)
                draftNoteId = null
            }
            keepDraft = false

            // 1. OCR en el dispositivo
            val text = try {
                recognizeText(state.capturedPages.map { it.imageFile })
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                fail(ProcessingError.UNREADABLE)
                return@launch
            }
            if (text.isBlank()) {
                fail(ProcessingError.NO_TEXT)
                return@launch
            }
            step()

            // 2. Guardar la nota en el backend
            val title = state.className.trim().ifBlank { L10n.getString(R.string.scan_default_title, course.name) }
            val noteId = when (val created = createNote(title, text, course.id, state.date)) {
                is Resource.Success -> created.data.id.toLong()
                is Resource.Error -> {
                    fail(ProcessingError.upload(created.message))
                    return@launch
                }
                is Resource.Loading -> return@launch
            }
            draftNoteId = noteId
            step()

            // 3. Resumen con IA (OpenAI, desde el backend)
            val effectiveLang = aiLanguagePreferenceManager.getEffectiveLanguageCode()
            val summary = when (val processed = processNoteWithAi(noteId, effectiveLang)) {
                is Resource.Success -> processed.data.summary.orEmpty()
                is Resource.Error -> {
                    keepDraft = true // la nota queda guardada (FAILED) y se puede reintentar desde Notas
                    fail(ProcessingError.ai(processed.message))
                    return@launch
                }
                is Resource.Loading -> return@launch
            }
            step()

            // 4. Estructurar el resumen en secciones editables
            val sections = parseSummary(summary).ifEmpty {
                if (summary.isNotBlank()) {
                    listOf(NoteSection(id = nextSectionId++, title = L10n.get(R.string.ai_summary_header), bullets = listOf(NoteBullet(id = nextBulletId++, text = summary))))
                } else emptyList()
            }

            _uiState.update {
                it.copy(
                    isProcessing = false,
                    title = title,
                    sections = sections,
                    originalText = text,
                    completedSteps = it.completedSteps + 1
                )
            }
        }
    }

    private fun step() {
        _uiState.update { it.copy(completedSteps = it.completedSteps + 1) }
    }

    private fun fail(error: ProcessingError) {
        _uiState.update { it.copy(isProcessing = false, processingError = error) }
    }

    /**
     * Convierte el resumen de la IA (viñetas en texto plano o Markdown simple) en
     * secciones editables. Los encabezados ("## Tema", "Tema:" o "**Tema**") abren
     * una sección nueva; el resto de líneas son puntos.
     */
    private fun parseSummary(summary: String): List<NoteSection> {
        val sections = mutableListOf<NoteSection>()
        var currentTitle = L10n.get(R.string.ai_summary_header)
        var currentBullets = mutableListOf<NoteBullet>()

        fun flush() {
            if (currentBullets.isNotEmpty()) {
                sections += NoteSection(id = nextSectionId++, title = currentTitle, bullets = currentBullets)
            }
            currentBullets = mutableListOf()
        }

        summary.lines().map { it.trim() }.filter { it.isNotEmpty() }.forEach { line ->
            val isBullet = BULLET_PREFIX.containsMatchIn(line)
            val heading = when {
                line.startsWith("#") -> line.trimStart('#').trim()
                !isBullet && BOLD_HEADING.matches(line) -> BOLD_HEADING.find(line)?.groupValues?.get(1)
                !isBullet && line.endsWith(":") && line.length <= 80 -> line.dropLast(1)
                else -> null
            }?.replace("**", "")?.trim()

            if (!heading.isNullOrEmpty()) {
                flush()
                currentTitle = heading
            } else {
                val text = line.replace(BULLET_PREFIX, "").replace("**", "").trim()
                if (text.isNotEmpty()) currentBullets += NoteBullet(id = nextBulletId++, text = text)
            }
        }
        flush()
        return sections
    }

    /** Inverso de [parseSummary]: así se guarda el resumen revisado por el estudiante. */
    private fun sectionsToSummary(sections: List<NoteSection>): String? {
        val nonEmpty = sections
            .map { section -> section.copy(bullets = section.bullets.filter { it.text.isNotBlank() }) }
            .filter { it.bullets.isNotEmpty() }
        if (nonEmpty.isEmpty()) return null
        return nonEmpty.joinToString(separator = "\n\n") { section ->
            val bullets = section.bullets.joinToString(separator = "\n") { "- ${it.text.trim()}" }
            if (nonEmpty.size == 1) bullets else "## ${section.title.trim()}\n$bullets"
        }
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
            val newSection = NoteSection(id = nextSectionId++, title = L10n.get(R.string.new_section), bullets = emptyList())
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
     * Confirma la nota: PUT /notes/{id} con el título, la materia, el texto
     * reconocido (+ etiquetas) y el resumen revisado.
     */
    fun saveNote() {
        val state = _uiState.value
        val noteId = draftNoteId
        if (noteId == null) {
            _uiState.update { it.copy(saveError = L10n.getString(R.string.scan_process_before_save)) }
            return
        }
        val course = courses.firstOrNull { it.name == state.subject }
        if (course == null) {
            _uiState.update { it.copy(saveError = L10n.getString(R.string.scan_choose_subject)) }
            return
        }
        val content = buildString {
            append(state.originalText.trim())
            if (state.tags.isNotEmpty()) {
                append("\n\n")
                append(L10n.getString(R.string.scan_tags_label))
                append(": ")
                append(state.tags.joinToString(", "))
            }
        }
        val summary = sectionsToSummary(state.sections)
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }
            when (val result = updateNote(noteId, state.title, content, course.id, summary)) {
                is Resource.Success -> {
                    keepDraft = true
                    _uiState.update { it.copy(isSaving = false, saved = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false, saveError = result.message) }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private companion object {
        val BULLET_PREFIX = Regex("^([-*•·–]|\\d+[.)])\\s+")
        val BOLD_HEADING = Regex("^\\*\\*(.+)\\*\\*$")
    }
}
