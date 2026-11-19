package com.example.uturn.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uturn.data.SessionManager
import com.example.uturn.ui.theme.UturnGreen
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.uturn.R

@Composable
fun SplashScreen(
    sessionManager: SessionManager,
    onFinished: (onboardingDone: Boolean) -> Unit
) {
    val onboardingDone by
    sessionManager.onboardingDone.collectAsStateWithLifecycle(initialValue = null)


    // Wait for the flag to load, show the logo briefly, then move on
    LaunchedEffect(onboardingDone) {
        if (onboardingDone != null) {
            delay(1600)
            onFinished(onboardingDone!!)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UturnGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ucoffee_logo),
                contentDescription = "U-Turn Coffee",
                modifier = Modifier.size(200.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Coffee with purpose",
                fontSize = 16.sp,
                color = Color(0xFF212121)
            )
        }
    }
}




