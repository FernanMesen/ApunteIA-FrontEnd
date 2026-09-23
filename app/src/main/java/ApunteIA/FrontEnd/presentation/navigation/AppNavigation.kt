package ApunteIA.FrontEnd.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import ApunteIA.FrontEnd.presentation.ui.screens.*
import ApunteIA.FrontEnd.presentation.viewmodel.AuditViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.JoinSubjectViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.LoginViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.MainViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.ManageSubjectsViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.NotesViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.QuizViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.ScanViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.StudyHistoryViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.ThemeViewModel

@Composable
fun AppNavigation(
    themeViewModel: ThemeViewModel = hiltViewModel()
) {

    val isDarkMode by themeViewModel.isDarkMode.collectAsState(false)
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // =========================
        // LOGIN
        // =========================

        composable(Routes.LOGIN) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLogin = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }


        // =========================
        // REGISTER
        // =========================

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegister = {
                    navController.navigate(Routes.PROFILE_SETUP)
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }


        // =========================
        // PROFILE SETUP
        // =========================

        composable(Routes.PROFILE_SETUP) {
            ProfileSetupScreen(
                onNext = {
                    navController.navigate(Routes.SUBJECTS)
                }
            )
        }


        // =========================
        // SUBJECTS
        // =========================

        composable(Routes.SUBJECTS) {
            SubjectsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNext = {
                    navController.navigate(Routes.STUDY_GOAL)
                }
            )
        }


        // =========================
        // STUDY GOAL
        // =========================

        composable(Routes.STUDY_GOAL) {
            StudyGoalScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNext = {
                    navController.navigate(Routes.LANGUAGE)
                }
            )
        }


        // =========================
        // LANGUAGE
        // =========================

        composable(Routes.LANGUAGE) {
            LanguageScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNext = {
                    navController.navigate(Routes.PERMISSIONS)
                }
            )
        }


        // =========================
        // PERMISSIONS
        // =========================

        composable(Routes.PERMISSIONS) {
            PermissionsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }


        // =========================
        // MAIN
        // =========================

        composable(Routes.MAIN) {
            val mainViewModel: MainViewModel = hiltViewModel()
            MainScreen(
                viewModel = mainViewModel,
                onInicio = { },
                onNotas = {
                    navController.navigate(Routes.NOTES)
                },
                onAuditoria = {
                    navController.navigate(Routes.AUDITORIA)
                },
                onReforzar = {
                    navController.navigate(Routes.REFORZAR)
                },
                onCameraClick = {
                    navController.navigate(Routes.SCAN_GRAPH)
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }


        // =========================
        // PROFILE
        // =========================

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
                isDarkMode = isDarkMode,
                onThemeToggle = { themeViewModel.toggleTheme(it) },
                onManageSubjects = {
                    navController.navigate(Routes.MANAGE_SUBJECTS)
                },
                onStudyHistory = {
                    navController.navigate(Routes.STUDY_HISTORY)
                },
                onCameraAccess = {
                    navController.navigate(Routes.CAMERA_ACCESS)
                },
                onPrivacy = {
                    navController.navigate(Routes.PRIVACY)
                },
                onExportData = {
                    navController.navigate(Routes.EXPORT_DATA)
                },
                onChangePassword = {
                    navController.navigate(Routes.CHANGE_PASSWORD)
                }
            )
        }


        // =========================
        // MANAGE SUBJECTS
        // =========================

        composable(Routes.MANAGE_SUBJECTS) {
            val manageSubjectsViewModel: ManageSubjectsViewModel = hiltViewModel()
            ManageSubjectsScreen(
                viewModel = manageSubjectsViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onJoinCourse = {
                    navController.navigate(Routes.JOIN_SUBJECT)
                }
            )
        }

        composable(Routes.JOIN_SUBJECT) {
            val joinSubjectViewModel: JoinSubjectViewModel = hiltViewModel()
            JoinSubjectScreen(
                viewModel = joinSubjectViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onJoinSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.STUDY_HISTORY) {
            val studyHistoryViewModel: StudyHistoryViewModel = hiltViewModel()
            StudyHistoryScreen(
                viewModel = studyHistoryViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
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
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }


        // =========================
        // COURSES
        // =========================

        composable(Routes.COURSES) {
            CoursesScreen()
        }


        // =========================
        // NOTES
        // =========================

        composable(Routes.NOTES) {
            val notesViewModel: NotesViewModel = hiltViewModel()
            NotesScreen(
                viewModel = notesViewModel,
                onInicio = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNotas = {},
                onAuditoria = {
                    navController.navigate(Routes.AUDITORIA)
                },
                onReforzar = {
                    navController.navigate(Routes.REFORZAR)
                },
                onCameraClick = {
                    navController.navigate(Routes.SCAN_GRAPH)
                },
                onNoteClick = { title ->
                    navController.navigate("${Routes.NOTE_DETAIL}/$title")
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        // =========================
        // NOTE DETAIL
        // =========================

        composable("${Routes.NOTE_DETAIL}/{title}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            NoteDetailScreen(
                noteTitle = title,
                onBack = { navController.popBackStack() }
            )
        }

        // =========================
        // AUDITORIA
        // =========================

        composable(Routes.AUDITORIA) {
            val auditViewModel: AuditViewModel = hiltViewModel()
            AuditScreen(
                viewModel = auditViewModel,
                onInicio = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNotas = { navController.navigate(Routes.NOTES) },
                onAuditoria = {},
                onReforzar = { navController.navigate(Routes.REFORZAR) },
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }

        // =========================
        // REFORZAR
        // =========================

        composable(Routes.REFORZAR) {
            ReinforcementScreen(
                onInicio = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNotas = { navController.navigate(Routes.NOTES) },
                onAuditoria = { navController.navigate(Routes.AUDITORIA) },
                onReforzar = {},
                onStartQuiz = { navController.navigate(Routes.QUIZ) },
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }

        // =========================
        // QUIZ
        // =========================

        composable(Routes.QUIZ) {
            val quizViewModel: QuizViewModel = hiltViewModel()
            QuizScreen(
                viewModel = quizViewModel,
                onClose = { navController.popBackStack() },
                onNavigateToMain = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onScanMore = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // =========================
        // ESCANEO (Nuevo escaneo -> Cámara -> Páginas -> Revisar nota)
        // =========================
        navigation(startDestination = Routes.SCAN_NEW, route = Routes.SCAN_GRAPH) {

            composable(Routes.SCAN_NEW) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.SCAN_GRAPH)
                }
                val scanViewModel: ScanViewModel = hiltViewModel(parentEntry)

                ScanNewScreen(
                    viewModel = scanViewModel,
                    onClose = {
                        navController.popBackStack(Routes.SCAN_GRAPH, true)
                    },
                    onContinue = {
                        navController.navigate(Routes.SCAN_CAMERA)
                    }
                )
            }

            composable(Routes.SCAN_CAMERA) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.SCAN_GRAPH)
                }
                val scanViewModel: ScanViewModel = hiltViewModel(parentEntry)

                ScanCameraScreen(
                    viewModel = scanViewModel,
                    onClose = {
                        navController.popBackStack(Routes.SCAN_GRAPH, true)
                    },
                    onDone = {
                        navController.navigate(Routes.SCAN_PAGES)
                    }
                )
            }

            composable(Routes.SCAN_PAGES) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.SCAN_GRAPH)
                }
                val scanViewModel: ScanViewModel = hiltViewModel(parentEntry)

                ScanPagesScreen(
                    viewModel = scanViewModel,
                    onClose = {
                        navController.popBackStack(Routes.SCAN_GRAPH, true)
                    },
                    onAddMorePages = {
                        navController.popBackStack()
                    },
                    onConfirm = {
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
                    onClose = {
                        navController.popBackStack(Routes.SCAN_GRAPH, true)
                    },
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
                    onClose = {
                        navController.popBackStack(Routes.SCAN_GRAPH, true)
                    },
                    onProcessAgain = {
                        navController.navigate(Routes.SCAN_PROCESSING) {
                            popUpTo(Routes.SCAN_REVIEW) { inclusive = true }
                        }
                    },
                    onSaved = {
                        navController.navigate(Routes.NOTES) {
                            popUpTo(Routes.SCAN_GRAPH) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
