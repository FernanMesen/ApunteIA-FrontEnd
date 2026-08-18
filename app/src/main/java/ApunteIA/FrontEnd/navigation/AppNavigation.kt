package ApunteIA.FrontEnd.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ApunteIA.FrontEnd.screens.CoursesScreen
import ApunteIA.FrontEnd.screens.LoginScreen
import ApunteIA.FrontEnd.screens.MainScreen
import ApunteIA.FrontEnd.screens.NotesScreen
import ApunteIA.FrontEnd.screens.RegisterScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {

            LoginScreen(
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

        composable(Routes.REGISTER) {

            RegisterScreen(
                onRegister = {

                    navController.navigate(Routes.MAIN) {

                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

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

        composable(Routes.COURSES) {
            CoursesScreen()
        }

        composable(Routes.NOTES) {
            NotesScreen()
        }
    }
}

