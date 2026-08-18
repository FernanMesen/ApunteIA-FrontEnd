package ApunteIA.FrontEnd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ApunteIA.FrontEnd.navigation.AppNavigation
import ApunteIA.FrontEnd.ui.theme.ApunteIAFrontendTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ApunteIAFrontendTheme {
                AppNavigation()
            }
        }
    }
}