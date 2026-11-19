package com.example.uturn.screens

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uturn.components.AuthTextField
import com.example.uturn.ui.theme.UturnCardBorder
import com.example.uturn.ui.theme.UturnGreen
import com.example.uturn.ui.theme.UturnTextDark
import com.example.uturn.ui.theme.UturnTextGrey

@Composable
fun ForgotPasswordScreen(
    onSubmit: (email: String) -> Unit,
    onBack: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var sent by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, UturnTextDark, CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = UturnTextDark
            )
        }
        Spacer(Modifier.height(32.dp))
        Text("Forgot Password?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = UturnGreen)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Enter your email and we'll send you a link to reset your password.",
            fontSize = 15.sp,
            color = UturnTextGrey
        )
        Spacer(Modifier.height(36.dp))

        if (!sent) {
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardType = KeyboardType.Email,
                error = emailError
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    if (Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        emailError = null
                        // Backend phase: POST /auth/forgot-password. The server must reply the
                        // same way whether or not the email exists (no account enumeration).
                        onSubmit(email.trim())
                        sent = true
                    } else {
                        emailError = "Enter a valid email address"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Send reset link", fontSize = 16.sp)
            }
        } else {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, UturnCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "If an account exists for ${email.trim()}, we've sent a reset link. " +
                            "Check your inbox (and spam folder).",
                    fontSize = 15.sp,
                    color = UturnTextDark,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back to login", fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
