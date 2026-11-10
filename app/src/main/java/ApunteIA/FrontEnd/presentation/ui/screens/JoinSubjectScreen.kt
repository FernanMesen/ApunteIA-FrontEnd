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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.ui.theme.SpaceCadet
import ApunteIA.FrontEnd.presentation.viewmodel.JoinSubjectViewModel
import ApunteIA.FrontEnd.presentation.ui.layout.ResponsiveContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinSubjectScreen(
    onBack: () -> Unit,
    onJoinSuccess: () -> Unit = {},
    viewModel: JoinSubjectViewModel = hiltViewModel()
) {
    var courseCode by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            JoinSubjectTopBar(onBack = onBack)
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refreshAvailableCourses() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ResponsiveContent(maxWidth = 840.dp) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                item {
                    CodeJoinCard(
                        value = courseCode,
                        onValueChange = { courseCode = it },
                        isJoining = uiState.isJoining,
                        onJoin = {
                            viewModel.joinByCode(courseCode, onJoinSuccess)
                        }
                    )
                }

                if (uiState.error != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = uiState.error.orEmpty(),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.refreshAvailableCourses() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text(stringResource(R.string.retry))
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.available_courses_header),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                if (uiState.isLoading && uiState.availableCourses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (uiState.availableCourses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.already_enrolled_all),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(uiState.availableCourses) { course ->
                        AvailableCourseCard(
                            course = course,
                            isJoining = uiState.isJoining,
                            onJoin = {
                                viewModel.joinCourse(course, onJoinSuccess)
                            }
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.courses_info_notice),
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
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = stringResource(R.string.join_course_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.join_course_subtitle),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CodeJoinCard(
    value: String,
    onValueChange: (String) -> Unit,
    isJoining: Boolean = false,
    onJoin: () -> Unit
) {
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
                placeholder = { Text(stringResource(R.string.course_code_placeholder), fontSize = 14.sp) },
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
                enabled = !isJoining && value.isNotBlank(),
                modifier = Modifier.height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF5D7B92) else MaterialTheme.colorScheme.primary
                )
            ) {
                if (isJoining) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.join_course_button), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AvailableCourseCard(
    course: AvailableCourse,
    isJoining: Boolean = false,
    onJoin: () -> Unit
) {
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
                        text = course.name.firstOrNull()?.toString() ?: "?",
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
                        text = listOf(course.professor, course.term).filter { it.isNotBlank() }.joinToString(" · "),
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
                enabled = !isJoining,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF5D7B92) else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                if (isJoining) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.join_course_action), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class AvailableCourse(
    val name: String,
    val professor: String,
    val term: String,
    val id: String,
    val colorHex: String,
    val courseId: Long = 0L
)
