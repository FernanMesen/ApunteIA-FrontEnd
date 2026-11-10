package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.presentation.ui.components.AuditSummaryCard
import ApunteIA.FrontEnd.presentation.ui.components.FilterChip
import ApunteIA.FrontEnd.presentation.ui.components.TopicCard
import ApunteIA.FrontEnd.presentation.ui.components.TopHeader
import ApunteIA.FrontEnd.presentation.ui.layout.MainLayout
import ApunteIA.FrontEnd.presentation.ui.theme.TextGray
import ApunteIA.FrontEnd.presentation.viewmodel.AuditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditScreen(
    onInicio: () -> Unit = {},
    onNotas: () -> Unit = {},
    onAuditoria: () -> Unit = {},
    onReforzar: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onPracticeClick: (Topic) -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: AuditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val filteredTopics by viewModel.filteredTopics.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val retryText = stringResource(R.string.retry)

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            val result = snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = retryText,
                duration = SnackbarDuration.Indefinite
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.refresh()
            }
        }
    }

    MainLayout(
        currentRoute = "Auditoría",
        onInicio = onInicio, onNotas = onNotas, onAuditoria = onAuditoria, onReforzar = onReforzar,
        onCameraClick = onCameraClick,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { 
            TopHeader(
                onProfileClick = onProfileClick
            ) 
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = stringResource(R.string.audit_title), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text(text = stringResource(R.string.audit_subtitle), fontSize = 14.sp, color = TextGray)
                }

                if (uiState.isLoading && uiState.topics.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
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
                                    text = stringResource(R.string.audit_error_loading, uiState.error.orEmpty()),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.loadTopics() },
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

                if (!uiState.isLoading && filteredTopics.isEmpty() && uiState.error == null) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.no_topics_for_category), color = TextGray)
                        }
                    }
                } else {
                    items(filteredTopics) { topic ->
                        TopicCard(topic = topic, onPracticeClick = { onPracticeClick(topic) })
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
