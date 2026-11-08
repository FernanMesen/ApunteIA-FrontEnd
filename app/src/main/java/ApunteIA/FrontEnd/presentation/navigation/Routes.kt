package ApunteIA.FrontEnd.presentation.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val COURSES = "courses"
    const val NOTES = "notes"
    const val NOTE_DETAIL = "note_detail"
    const val NOTE_DETAIL_PATTERN = "note_detail/{noteId}"
    const val AUDITORIA = "auditoria"
    const val REFORZAR = "reforzar"
    /** Reforzar con una nota preseleccionada (opcional: sin el argumento abre Reforzar normal). */
    const val REFORZAR_PATTERN = "reforzar?noteId={noteId}"
    const val QUIZ = "quiz"
    const val QUIZ_PATTERN = "quiz/{assessmentId}"
    const val PROFILE = "profile"
    const val MANAGE_SUBJECTS = "manage_subjects"
    const val STUDY_HISTORY = "study_history"
    const val CAMERA_ACCESS = "camera_access"
    const val PRIVACY = "privacy"
    const val EXPORT_DATA = "export_data"
    const val CHANGE_PASSWORD = "change_password"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password"
    const val RESET_PASSWORD_PATTERN = "reset_password?token={token}"
    const val DIRECT_RESET_PASSWORD = "direct_reset_password"
    const val JOIN_SUBJECT = "join_subject"

    // Administración (solo ADMIN)
    const val ADMIN_COURSES = "admin_courses"
    const val ADMIN_COURSE = "admin_course"
    const val ADMIN_COURSE_PATTERN = "admin_course/{courseId}"

    const val PROFILE_SETUP = "profile_setup"
    const val SUBJECTS = "subjects"
    const val STUDY_GOAL = "study_goal"
    const val LANGUAGE = "language"
    /** Idioma desde el Perfil: aplica el cambio y vuelve, sin pasar por el onboarding. */
    const val LANGUAGE_SETTINGS = "language_settings"
    const val PERMISSIONS = "permissions"

    // =========================
    // Flujo de escaneo
    // =========================
    const val SCAN_GRAPH = "scan_graph"
    const val SCAN_NEW = "scan_new"
    const val SCAN_CAMERA = "scan_camera"
    const val SCAN_PAGES = "scan_pages"
    const val SCAN_PROCESSING = "scan_processing"
    const val SCAN_REVIEW = "scan_review"

    fun noteDetail(noteId: Long) = "$NOTE_DETAIL/$noteId"
    fun quiz(assessmentId: Long) = "$QUIZ/$assessmentId"
    fun reforzar(noteId: Long) = "$REFORZAR?noteId=$noteId"
    /** courseId = 0 abre el formulario para crear un curso nuevo. */
    fun adminCourse(courseId: Long) = "$ADMIN_COURSE/$courseId"
    fun directResetPassword(email: String) = "$DIRECT_RESET_PASSWORD?email=$email"
}
