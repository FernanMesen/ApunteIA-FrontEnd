package ApunteIA.FrontEnd.presentation.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.ui.components.LocalCurrentUser
import ApunteIA.FrontEnd.presentation.ui.components.RefreshOnResume
import ApunteIA.FrontEnd.presentation.ui.screens.*
import ApunteIA.FrontEnd.presentation.viewmodel.AdminCourseDetailViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.AdminCoursesViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.AuditViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.JoinSubjectViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.LoginViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.MainViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.ManageSubjectsViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.NoteDetailViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.NotesViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.QuizViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.ReinforcementViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.ScanViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.SessionViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.StudyHistoryViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.ThemeViewModel

/** Limpia toda la pila y abre [route] (login, o inicio después de autenticarse). */
private fun NavHostController.resetTo(route: String) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

/** Pestañas de la barra inferior: siempre quedan encima de Inicio, sin apilar repetidas. */
private fun NavHostController.goToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.MAIN)
        launchSingleTop = true
    }
}

@Composable
fun AppNavigation(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState(false)
    val currentUser by sessionViewModel.currentUser.collectAsState()
    val navController = rememberNavController()
    val context = LocalContext.current
    val startDestination = remember { if (sessionViewModel.startLoggedIn) Routes.MAIN else Routes.LOGIN }

    // Si el backend rechaza el JWT (venció, cuenta bloqueada...), se vuelve al login.
    LaunchedEffect(Unit) {
        sessionViewModel.sessionExpired.collect {
            sessionViewModel.refreshUser()
            Toast.makeText(context, context.getString(R.string.api_error_session), Toast.LENGTH_LONG).show()
            navController.resetTo(Routes.LOGIN)
        }
    }

    CompositionLocalProvider(LocalCurrentUser provides currentUser) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {

            // =========================
            // LOGIN
            // =========================

            composable(Routes.LOGIN) {
                val loginViewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    viewModel = loginViewModel,
                    onLogin = {
                        sessionViewModel.refreshUser()
                        navController.resetTo(Routes.MAIN)
                    },
                    onRegister = {
                        navController.navigate(Routes.REGISTER)
                    }
                )
            }

            // =========================
            // REGISTER (+ onboarding)
            // =========================

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegister = {
                        sessionViewModel.refreshUser()
                        navController.navigate(Routes.PROFILE_SETUP) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBackToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.PROFILE_SETUP) {
                ProfileSetupScreen(
                    onNext = { navController.navigate(Routes.SUBJECTS) }
                )
            }

            composable(Routes.SUBJECTS) {
                SubjectsScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.STUDY_GOAL) }
                )
            }

            composable(Routes.STUDY_GOAL) {
                StudyGoalScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.LANGUAGE) }
                )
            }

            composable(Routes.LANGUAGE) {
                LanguageScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.PERMISSIONS) }
                )
            }

            // Idioma desde el Perfil: el cambio se aplica al tocar (la Activity se recrea y el
            // NavController restaura la pila en esta misma pantalla); OK/atrás vuelven al Perfil.
            composable(Routes.LANGUAGE_SETTINGS) {
                LanguageScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.popBackStack() },
                    isFromProfile = true
                )
            }

            composable(Routes.PERMISSIONS) {
                PermissionsScreen(
                    onBack = { navController.popBackStack() },
                    onFinish = { navController.resetTo(Routes.MAIN) }
                )
            }

            // =========================
            // MAIN
            // =========================

            composable(Routes.MAIN) {
                val mainViewModel: MainViewModel = hiltViewModel()
                RefreshOnResume { mainViewModel.refresh() }
                MainScreen(
                    viewModel = mainViewModel,
                    onInicio = { },
                    onNotas = { navController.goToTab(Routes.NOTES) },
                    onAuditoria = { navController.goToTab(Routes.AUDITORIA) },
                    onReforzar = { navController.goToTab(Routes.REFORZAR) },
                    onCursos = { navController.navigate(Routes.ADMIN_COURSES) },
                    onCameraClick = { navController.navigate(Routes.SCAN_GRAPH) },
                    onProfileClick = { navController.navigate(Routes.PROFILE) }
                )
            }

            // =========================
            // PROFILE
            // =========================

            composable(Routes.PROFILE) {
                val statsViewModel: MainViewModel = hiltViewModel()
                val statsState by statsViewModel.uiState.collectAsState()
                val aiLanguageOption by themeViewModel.aiLanguageOption.collectAsState()
                RefreshOnResume { statsViewModel.refresh() }
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        sessionViewModel.logout()
                        navController.resetTo(Routes.LOGIN)
                    },
                    isDarkMode = isDarkMode,
                    aiLanguageOption = aiLanguageOption,
                    onAiLanguageChange = { themeViewModel.setAiLanguageOption(it) },
                    onThemeToggle = { themeViewModel.toggleTheme(it) },
                    onManageSubjects = { navController.navigate(Routes.MANAGE_SUBJECTS) },
                    onStudyHistory = { navController.navigate(Routes.STUDY_HISTORY) },
                    onCameraAccess = { navController.navigate(Routes.CAMERA_ACCESS) },
                    onPrivacy = { navController.navigate(Routes.PRIVACY) },
                    onExportData = { navController.navigate(Routes.EXPORT_DATA) },
                    onChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                    onLanguage = { navController.navigate(Routes.LANGUAGE_SETTINGS) },
                    notesCount = statsState.userStats?.notesCount,
                    quizzesCount = statsState.userStats?.quizzesDoneCount,
                    isAdmin = currentUser?.isAdmin == true,
                    onAdminCourses = { navController.navigate(Routes.ADMIN_COURSES) }
                )
            }

            // =========================
            // MATERIAS
            // =========================

            composable(Routes.MANAGE_SUBJECTS) {
                if (currentUser?.isAdmin == true) {
                    LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                } else {
                    val manageSubjectsViewModel: ManageSubjectsViewModel = hiltViewModel()
                    RefreshOnResume { manageSubjectsViewModel.refresh() }
                    ManageSubjectsScreen(
                        viewModel = manageSubjectsViewModel,
                        onBack = { navController.popBackStack() },
                        onJoinCourse = { navController.navigate(Routes.JOIN_SUBJECT) }
                    )
                }
            }

            composable(Routes.JOIN_SUBJECT) {
                if (currentUser?.isAdmin == true) {
                    LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                } else {
                    val joinSubjectViewModel: JoinSubjectViewModel = hiltViewModel()
                    JoinSubjectScreen(
                        viewModel = joinSubjectViewModel,
                        onBack = { navController.popBackStack() },
                        onJoinSuccess = {
                            Toast.makeText(context, context.getString(R.string.joined_subject), Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    )
                }
            }

            // =========================
            // ADMINISTRACIÓN (solo ADMIN; el backend también lo valida)
            // =========================

            composable(Routes.ADMIN_COURSES) {
                if (currentUser?.isAdmin != true) {
                    LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                } else {
                    val adminCoursesViewModel: AdminCoursesViewModel = hiltViewModel()
                    RefreshOnResume { adminCoursesViewModel.load() }
                    AdminCoursesScreen(
                        viewModel = adminCoursesViewModel,
                        onBack = { navController.popBackStack() },
                        onOpenCourse = { courseId -> navController.navigate(Routes.adminCourse(courseId)) },
                        onNewCourse = { navController.navigate(Routes.adminCourse(0L)) }
                    )
                }
            }

            composable(
                route = Routes.ADMIN_COURSE_PATTERN,
                arguments = listOf(navArgument(AdminCourseDetailViewModel.COURSE_ID_ARG) { type = NavType.LongType })
            ) {
                if (currentUser?.isAdmin != true) {
                    LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                } else {
                    val adminCourseViewModel: AdminCourseDetailViewModel = hiltViewModel()
                    AdminCourseDetailScreen(
                        viewModel = adminCourseViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.STUDY_HISTORY) {
                if (currentUser?.isAdmin == true) {
                    LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                } else {
                    val studyHistoryViewModel: StudyHistoryViewModel = hiltViewModel()
                    StudyHistoryScreen(
                        viewModel = studyHistoryViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.CAMERA_ACCESS) {
                PlaceholderScreen(title = "Acceso a cámara", onBack = { navController.popBackStack() })
            }

            composable(Routes.PRIVACY) {
                PlaceholderScreen(title = "Privacidad", onBack = { navController.popBackStack() })
            }

            composable(Routes.EXPORT_DATA) {
                PlaceholderScreen(title = "Exportar mis datos", onBack = { navController.popBackStack() })
            }

            composable(Routes.CHANGE_PASSWORD) {
                ChangePasswordScreen(
                    onBack = { navController.popBackStack() },
                    onFinish = {
                        sessionViewModel.logout()
                        navController.resetTo(Routes.LOGIN)
                    }
                )
            }

            composable(Routes.COURSES) {
                CoursesScreen()
            }

            // =========================
            // NOTES
            // =========================

            composable(Routes.NOTES) {
                if (currentUser?.isAdmin == true) {
                    LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                } else {
                    val notesViewModel: NotesViewModel = hiltViewModel()
                    RefreshOnResume { notesViewModel.refresh() }
                    NotesScreen(
                        viewModel = notesViewModel,
                        onInicio = { navController.goToTab(Routes.MAIN) },
                        onNotas = {},
                        onAuditoria = { navController.goToTab(Routes.AUDITORIA) },
                        onReforzar = { navController.goToTab(Routes.REFORZAR) },
                        onCameraClick = { navController.navigate(Routes.SCAN_GRAPH) },
                        onNoteClick = { noteId -> navController.navigate(Routes.noteDetail(noteId.toLong())) },
                        onProfileClick = { navController.navigate(Routes.PROFILE) }
                    )
                }
            }

            composable(
                route = Routes.NOTE_DETAIL_PATTERN,
                arguments = listOf(navArgument(NoteDetailViewModel.NOTE_ID_ARG) { type = NavType.LongType })
            ) {
                if (currentUser?.isAdmin == true) {
                    LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                } else {
                    val noteDetailViewModel: NoteDetailViewModel = hiltViewModel()
                    NoteDetailScreen(
                        viewModel = noteDetailViewModel,
                        onBack = { navController.popBackStack() },
                        onOpenQuiz = { assessmentId -> navController.navigate(Routes.quiz(assessmentId)) },
                        onDeleted = { navController.popBackStack() }
                    )
                }
            }

            // =========================
            // AUDITORIA
            // =========================

            composable(Routes.AUDITORIA) {
                if (currentUser?.isAdmin == true) {
                    LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                } else {
                    val auditViewModel: AuditViewModel = hiltViewModel()
                    RefreshOnResume { auditViewModel.refresh() }
                    AuditScreen(
                        viewModel = auditViewModel,
                        onInicio = { navController.goToTab(Routes.MAIN) },
                        onNotas = { navController.goToTab(Routes.NOTES) },
                        onAuditoria = {},
                        onReforzar = { navController.goToTab(Routes.REFORZAR) },
                        onCameraClick = { navController.navigate(Routes.SCAN_GRAPH) },
                        onPracticeClick = { topic ->
                            // Topic.id es el id de la nota; negativo = materia sin apuntes → escanear.
                            if (topic.id > 0) navController.navigate(Routes.reforzar(topic.id.toLong()))
                            else navController.navigate(Routes.SCAN_GRAPH)
                        },
                        onProfileClick = { navController.navigate(Routes.PROFILE) }
                    )
                }
            }

            // =========================
            // REFORZAR
            // =========================

            // "reforzar" (pestaña) y "reforzar?noteId=X" (desde Auditoría) caen en este destino.
            composable(
                route = Routes.REFORZAR_PATTERN,
                arguments = listOf(navArgument(ReinforcementViewModel.NOTE_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) {
                if (currentUser?.isAdmin == true) {
                    LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                } else {
                    val reinforcementViewModel: ReinforcementViewModel = hiltViewModel()
                    ReinforcementScreen(
                        viewModel = reinforcementViewModel,
                        onInicio = { navController.goToTab(Routes.MAIN) },
                        onNotas = { navController.goToTab(Routes.NOTES) },
                        onAuditoria = { navController.goToTab(Routes.AUDITORIA) },
                        onReforzar = {},
                        onCameraClick = { navController.navigate(Routes.SCAN_GRAPH) },
                        onStartQuiz = { assessmentId -> navController.navigate(Routes.quiz(assessmentId)) },
                        onProfileClick = { navController.navigate(Routes.PROFILE) }
                    )
                }
            }

            // =========================
            // QUIZ
            // =========================

            composable(
                route = Routes.QUIZ_PATTERN,
                arguments = listOf(navArgument(QuizViewModel.ASSESSMENT_ID_ARG) { type = NavType.LongType })
            ) {
                if (currentUser?.isAdmin == true) {
                    LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                } else {
                    val quizViewModel: QuizViewModel = hiltViewModel()
                    QuizScreen(
                        viewModel = quizViewModel,
                        onClose = { navController.popBackStack() },
                        onNavigateToMain = { navController.goToTab(Routes.MAIN) },
                        onScanMore = {
                            navController.navigate(Routes.SCAN_GRAPH) {
                                popUpTo(Routes.QUIZ_PATTERN) { inclusive = true }
                            }
                        }
                    )
                }
            }

            // =========================
            // ESCANEO (Nuevo escaneo -> Cámara -> Páginas -> Procesar -> Revisar nota)
            // =========================
            navigation(startDestination = Routes.SCAN_NEW, route = Routes.SCAN_GRAPH) {

                composable(Routes.SCAN_NEW) { backStackEntry ->
                    if (currentUser?.isAdmin == true) {
                        LaunchedEffect(Unit) { navController.resetTo(Routes.MAIN) }
                    } else {
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry(Routes.SCAN_GRAPH)
                        }
                        val scanViewModel: ScanViewModel = hiltViewModel(parentEntry)

                        ScanNewScreen(
                            viewModel = scanViewModel,
                            onClose = { navController.popBackStack(Routes.SCAN_GRAPH, true) },
                            onContinue = { navController.navigate(Routes.SCAN_CAMERA) }
                        )
                    }
                }

                composable(Routes.SCAN_CAMERA) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Routes.SCAN_GRAPH)
                    }
                    val scanViewModel: ScanViewModel = hiltViewModel(parentEntry)

                    ScanCameraScreen(
                        viewModel = scanViewModel,
                        onClose = { navController.popBackStack(Routes.SCAN_GRAPH, true) },
                        onDone = { navController.navigate(Routes.SCAN_PAGES) }
                    )
                }

                composable(Routes.SCAN_PAGES) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Routes.SCAN_GRAPH)
                    }
                    val scanViewModel: ScanViewModel = hiltViewModel(parentEntry)

                    ScanPagesScreen(
                        viewModel = scanViewModel,
                        onClose = { navController.popBackStack(Routes.SCAN_GRAPH, true) },
                        onAddMorePages = { navController.popBackStack() },
                        onConfirm = {
                            scanViewModel.resetProcessing()
                            navController.navigate(Routes.SCAN_PROCESSING)
                        }
                    )
                }

                composable(Routes.SCAN_PROCESSING) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Routes.SCAN_GRAPH)
                    }
                    val scanViewModel: ScanViewModel = hiltViewModel(parentEntry)

                    ScanProcessingScreen(
                        viewModel = scanViewModel,
                        onClose = { navController.popBackStack(Routes.SCAN_GRAPH, true) },
                        onComplete = {
                            navController.navigate(Routes.SCAN_REVIEW) {
                                popUpTo(Routes.SCAN_PROCESSING) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.SCAN_REVIEW) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Routes.SCAN_GRAPH)
                    }
                    val scanViewModel: ScanViewModel = hiltViewModel(parentEntry)

                    ScanReviewScreen(
                        viewModel = scanViewModel,
                        onClose = { navController.popBackStack(Routes.SCAN_GRAPH, true) },
                        onProcessAgain = {
                            scanViewModel.resetProcessing()
                            navController.navigate(Routes.SCAN_PROCESSING) {
                                popUpTo(Routes.SCAN_REVIEW) { inclusive = true }
                            }
                        },
                        onSaved = {
                            navController.navigate(Routes.NOTES) {
                                popUpTo(Routes.SCAN_GRAPH) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}
