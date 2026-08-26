package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ApunteIA.FrontEnd.domain.model.Course
import ApunteIA.FrontEnd.presentation.factory.ManageSubjectsViewModelFactory
import ApunteIA.FrontEnd.presentation.ui.theme.*
import ApunteIA.FrontEnd.presentation.viewmodel.ManageSubjectsViewModel

@Composable
fun ManageSubjectsScreen(
    onBack: () -> Unit,
    onJoinCourse: () -> Unit = {},
    viewModel: ManageSubjectsViewModel = viewModel(factory = ManageSubjectsViewModelFactory())
) {
    val courses by viewModel.courses.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCourses()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ManageSubjectsTopBar(onBack = onBack, onJoinCourse = onJoinCourse)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            items(courses) { course ->
                CourseCard(course)
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun ManageSubjectsTopBar(onBack: () -> Unit, onJoinCourse: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.primary == SpaceCadet
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = "Materias",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Administra tus cursos",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Surface(
            modifier = Modifier.clickable { onJoinCourse() },
            color = if (isDark) Color(0xFF1E2532) else Color(0xFFE8F5E9),
            shape = RoundedCornerShape(20.dp),
            border = if (isDark) BorderStroke(0.5.dp, Color(0xFF394867)) else null
        ) {
            Text(
                text = "Unirse",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (isDark) Color.White else DarkGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CourseCard(course: Course) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con la inicial
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(android.graphics.Color.parseColor(course.colorHex))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = course.name.first().toString(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${course.professor} • ${course.term}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "${course.progress}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
