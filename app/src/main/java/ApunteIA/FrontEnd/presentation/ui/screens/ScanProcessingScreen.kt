package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.presentation.model.ProcessingError
import ApunteIA.FrontEnd.presentation.model.ProcessingStep
import ApunteIA.FrontEnd.presentation.ui.theme.StatusRed
import ApunteIA.FrontEnd.presentation.ui.theme.lightOr
import ApunteIA.FrontEnd.presentation.viewmodel.ScanViewModel

@Composable
fun ScanProcessingScreen(
    viewModel: ScanViewModel,
    onClose: () -> Unit,
    onComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startProcessingIfNeeded()
    }

    LaunchedEffect(state.processingDone) {
        if (state.processingDone) onComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scanBackground())
            .padding(horizontal = 20.dp)
    ) {
        ScanTopBar(onClose = onClose)

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            val error = state.processingError
            if (error != null) {
                ProcessingErrorContent(
                    error = error,
                    onRetry = { viewModel.confirmAndProcess() },
                    onCancel = onClose
                )
            } else {
                ProcessingChecklist(completedSteps = state.completedSteps)
            }
        }
    }
}

@Composable
private fun ProcessingChecklist(completedSteps: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = scanAccent(), strokeWidth = 3.dp, modifier = Modifier.size(56.dp))

        Spacer(modifier = Modifier.height(40.dp))

        Column(horizontalAlignment = Alignment.Start) {
            ProcessingStep.entries.forEachIndexed { index, step ->
                StepRow(label = step.label, done = index < completedSteps)
                if (index != ProcessingStep.entries.lastIndex) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.ai_processing_notice),
            color = scanMuted(),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StepRow(label: String, done: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (done) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(scanButton(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(Color.Transparent, CircleShape)
                    .border(1.5.dp, scanBorder(Color(0xFFD9D9D9)), CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = if (done) scanText() else scanMuted(),
            fontSize = 15.sp,
            fontWeight = if (done) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun ProcessingErrorContent(
    error: ProcessingError,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(lightOr(Color(0xFFFDECEC), Color(0xFF3B2525)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = StatusRed, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = error.title,
            color = scanText(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error.message,
            color = scanMuted(),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = scanButton())
        ) {
            Text(stringResource(R.string.retry), color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.cancel),
            color = scanMuted(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onCancel() }
        )
    }
}
