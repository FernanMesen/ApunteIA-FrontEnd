package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet
import ApunteIA.FrontEnd.presentation.viewmodel.JoinSubjectViewModel

@Composable
fun JoinSubjectScreen(
    onBack: () -> Unit,
    onJoinSuccess: () -> Unit = {},
    viewModel: JoinSubjectViewModel = hiltViewModel()
) {
    var courseCode by remember { mutableStateOf("") }
    val availableCourses by viewModel.availableCourses.collectAsState()
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            JoinSubjectTopBar(onBack = onBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                CodeJoinCard(
                    value = courseCode,
                    onValueChange = { courseCode = it },
                    onJoin = {
                        viewModel.joinByCode(courseCode)
                        onJoinSuccess()
                    }
                )
            }

            item {
                Text(
                    text = "CURSOS DISPONIBLES",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            items(availableCourses) { course ->
                AvailableCourseCard(
                    course = course, 
                    onJoin = {
                        viewModel.joinCourse(course)
                        onJoinSuccess()
                    }
                )
            }

            item {
                Text(
                    text = "Los cursos los crea tu administrador. Únete a uno para empezar a estudiar.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun JoinSubjectTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = "Unirse a un curso",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Ingresa un código o elige del catálogo",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CodeJoinCard(value: String, onValueChange: (String) -> Unit, onJoin: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Código del curso", fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDark) Color(0xFF1E2532) else Color.White,
                    unfocusedContainerColor = if (isDark) Color(0xFF1E2532) else Color.White,
                    focusedBorderColor = if (isDark) Color(0xFF394867) else Color(0xFFE2E2E2),
                    unfocusedBorderColor = if (isDark) Color(0xFF394867) else Color(0xFFE2E2E2),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Button(
                onClick = onJoin,
                modifier = Modifier.height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF5D7B92) else MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Unirse", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AvailableCourseCard(course: AvailableCourse, onJoin: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(android.graphics.Color.parseColor(course.colorHex))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = course.name.first().toString(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${course.professor} · ${course.term}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = course.id,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onJoin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF5D7B92) else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("Unirme al curso", fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class AvailableCourse(
    val name: String,
    val professor: String,
    val term: String,
    val id: String,
    val colorHex: String
)
