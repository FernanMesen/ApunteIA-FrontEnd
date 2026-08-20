package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ApunteIA.FrontEnd.presentation.ui.theme.*
import ApunteIA.FrontEnd.presentation.ui.components.FilterChip
import ApunteIA.FrontEnd.presentation.ui.components.NoteCard
import ApunteIA.FrontEnd.presentation.ui.layout.MainLayout
import ApunteIA.FrontEnd.presentation.factory.NotesViewModelFactory
import ApunteIA.FrontEnd.presentation.viewmodel.NotesViewModel

@Composable
fun NotesScreen(
    onInicio: () -> Unit = {},
    onNotas: () -> Unit = {},
    onAuditoria: () -> Unit = {},
    onReforzar: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onNoteClick: (String) -> Unit = {},
    viewModel: NotesViewModel = viewModel(factory = NotesViewModelFactory())
) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredNotes by viewModel.filteredNotes.collectAsState()

    MainLayout(
        currentRoute = "Notas",
        onInicio = onInicio,
        onNotas = onNotas,
        onAuditoria = onAuditoria,
        onReforzar = onReforzar,
        onCameraClick = onCameraClick
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Notas",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Text(
                    text = "Estructuradas por IA a partir de tus escaneos",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Buscar notas", color = TextGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                    singleLine = true
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        FilterChip(
                            text = category,
                            isSelected = selectedCategory == category,
                            onClick = { viewModel.onCategorySelected(category) }
                        )
                    }
                }
            }

            if (filteredNotes.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No se encontraron notas", color = TextGray)
                    }
                }
            } else {
                items(filteredNotes) { note ->
                    NoteCard(note, onClick = { onNoteClick(note.title) })
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

