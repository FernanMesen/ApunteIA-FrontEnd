package ApunteIA.FrontEnd.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ApunteIA.FrontEnd.presentation.ui.screens.*

import androidx.lifecycle.viewmodel.compose.viewModel
import ApunteIA.FrontEnd.presentation.factory.QuizViewModelFactory
import ApunteIA.FrontEnd.presentation.factory.MainViewModelFactory
import ApunteIA.FrontEnd.presentation.viewmodel.MainViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.QuizViewModel

@Composable
fun AppNavigation() {

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
                onNoteClick = { title ->
                    navController.navigate("${Routes.NOTE_DETAIL}/$title")
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
                onReforzar = { navController.navigate(Routes.REFORZAR) }
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
                onStartQuiz = { navController.navigate(Routes.QUIZ) }
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
    }
}