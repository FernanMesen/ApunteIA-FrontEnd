package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.presentation.ui.layout.MainLayout
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.LightGreen
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray

/**
 * Pantalla "Reforzar": punto de entrada para practicar con quizzes generados
 * a partir de las notas más débiles del usuario.
 *
 * NOTA: esta es una versión mínima funcional (no estaba en el proyecto
 * original). Sirve para que la navegación compile y ya haya algo sobre lo
 * cual iterar el diseño real.
 */
@Composable
fun ReinforcementScreen(
    onInicio: () -> Unit = {},
    onNotas: () -> Unit = {},
    onAuditoria: () -> Unit = {},
    onReforzar: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onStartQuiz: () -> Unit = {}
) {
    MainLayout(
        currentRoute = "Reforzar",
        onInicio = onInicio,
        onNotas = onNotas,
        onAuditoria = onAuditoria,
        onReforzar = onReforzar,
        onCameraClick = onCameraClick
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Reforzar", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            Text(
                text = "Practica con preguntas generadas a partir de tus notas más débiles",
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(LightGreen, RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¿Listo para reforzar?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Un quiz corto basado en los temas donde más lo necesitas.",
                    fontSize = 14.sp,
                    color = TextGray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onStartQuiz,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text("Comenzar quiz", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}