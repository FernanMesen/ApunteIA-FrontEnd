package ApunteIA.FrontEnd.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ApunteIA.FrontEnd.R

@Composable
fun PermissionsScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApuntesBackground)
            .padding(horizontal = 24.dp)
    ) {

        OnboardingHeader(
            progress = 1f,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.finish_setup_title),
            color = ApuntesGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(22.dp))

        PermissionCard(
            title = stringResource(R.string.camera_permission_title),
            description = stringResource(R.string.camera_permission_desc),
            onClick = { }
        )

        Spacer(modifier = Modifier.height(18.dp))

        PermissionCard(
            title = stringResource(R.string.photos_permission_title),
            description = stringResource(R.string.photos_permission_desc),
            onClick = { }
        )

        Spacer(modifier = Modifier.weight(1f))

        ApuntesPrimaryButton(
            text = stringResource(R.string.finish_setup_title),
            onClick = onFinish
        )

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            )
            .border(
                width = 1.dp,
                color = ApuntesBorder,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(16.dp)
    ) {

        Text(
            text = title,
            color = ApuntesGreen,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            color = ApuntesGray,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = ApuntesGreen
            ),
            shape = RoundedCornerShape(25.dp)
        ) {

            Text(
                text = stringResource(R.string.allow),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
