package ApunteIA.FrontEnd.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ApunteIA.FrontEnd.screens.*

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

            MainScreen(
                onCourses = {
                    navController.navigate(Routes.COURSES)
                },

                onNotes = {
                    navController.navigate(Routes.NOTES)
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
            NotesScreen()
        }
    }
}