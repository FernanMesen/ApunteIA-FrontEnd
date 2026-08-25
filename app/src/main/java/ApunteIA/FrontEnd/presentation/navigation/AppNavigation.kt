package ApunteIA.FrontEnd.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import ApunteIA.FrontEnd.presentation.ui.screens.*

import androidx.lifecycle.viewmodel.compose.viewModel
import ApunteIA.FrontEnd.presentation.factory.QuizViewModelFactory
import ApunteIA.FrontEnd.presentation.factory.MainViewModelFactory
import ApunteIA.FrontEnd.presentation.factory.ScanViewModelFactory
import ApunteIA.FrontEnd.presentation.viewmodel.MainViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.QuizViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.ScanViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.ThemeViewModel

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel = viewModel()) {

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

            LoginScreen(
                onLogin = {

                    // Usuario que YA tiene cuenta
                    navController.navigate(Routes.MAIN) {

                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                },

                onRegister = {

                    // Usuario nuevo
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

                    // Después de crear la cuenta
                    // empieza la configuración inicial
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

                    // Termina onboarding
                    // y entra a la aplicación
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
            val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory())
            MainScreen(
                viewModel = mainViewModel,
                onInicio = {

                },
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
                onThemeToggle = { themeViewModel.toggleTheme(it) }
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
            NotesScreen(
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
            AuditScreen(
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
            val quizViewModel: QuizViewModel = viewModel(factory = QuizViewModelFactory())
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
        // Las 4 pantallas comparten una misma instancia de ScanViewModel
        // "escopada" a este grafo anidado, para no perder el estado del
        // formulario/fotos al navegar entre pasos.
        navigation(startDestination = Routes.SCAN_NEW, route = Routes.SCAN_GRAPH) {

            composable(Routes.SCAN_NEW) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.SCAN_GRAPH)
                }
                val scanViewModel: ScanViewModel = viewModel(parentEntry, factory = ScanViewModelFactory())

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
                val scanViewModel: ScanViewModel = viewModel(parentEntry, factory = ScanViewModelFactory())

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
                val scanViewModel: ScanViewModel = viewModel(parentEntry, factory = ScanViewModelFactory())

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
                val scanViewModel: ScanViewModel = viewModel(parentEntry, factory = ScanViewModelFactory())

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
                val scanViewModel: ScanViewModel = viewModel(parentEntry, factory = ScanViewModelFactory())

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