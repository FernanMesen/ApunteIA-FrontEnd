package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ApunteIA.FrontEnd.presentation.factory.AuditViewModelFactory
import ApunteIA.FrontEnd.presentation.ui.components.AuditSummaryCard
import ApunteIA.FrontEnd.presentation.ui.components.FilterChip
import ApunteIA.FrontEnd.presentation.ui.components.TopicCard
import ApunteIA.FrontEnd.presentation.ui.components.TopHeader
import ApunteIA.FrontEnd.presentation.ui.layout.MainLayout
import ApunteIA.FrontEnd.presentation.ui.theme.DarkGreen
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray
import ApunteIA.FrontEnd.presentation.viewmodel.AuditViewModel
import ApunteIA.FrontEnd.presentation.viewmodel.NotificationViewModel

@Composable
fun AuditScreen(
    onInicio: () -> Unit = {},
    onNotas: () -> Unit = {},
    onAuditoria: () -> Unit = {},
    onReforzar: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onPracticeClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    notificationViewModel: NotificationViewModel,
    viewModel: AuditViewModel = viewModel(factory = AuditViewModelFactory())
) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val filteredTopics by viewModel.filteredTopics.collectAsState()
    
    val notifications by notificationViewModel.notifications.collectAsState()
    val hasUnread = notifications.any { !it.isRead }

    MainLayout(
        currentRoute = "Auditoría",
        onInicio = onInicio, onNotas = onNotas, onAuditoria = onAuditoria, onReforzar = onReforzar,
        onCameraClick = onCameraClick,
        topBar = { 
            TopHeader(
                onProfileClick = onProfileClick, 
                onNotificationsClick = onNotificationsClick,
                hasNotifications = hasUnread
            ) 
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Auditoría", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                Text(text = "Comparado con el programa de tu curso", fontSize = 14.sp, color = TextGray)
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(categories) { category ->
                        FilterChip(
                            text = category,
                            isSelected = selectedCategory == category,
                            onClick = { viewModel.onCategorySelected(category) }
                        )
                    }
                }
            }
            item { AuditSummaryCard(summary = summary) }

            if (filteredTopics.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No hay temas para esta categoría", color = TextGray)
                    }
                }
            } else {
                items(filteredTopics) { topic ->
                    TopicCard(topic = topic, onPracticeClick = { onPracticeClick(topic.title) })
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}