package com.example.uturn.screens

import android.util.Patterns
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uturn.components.AuthTextField
import com.example.uturn.ui.theme.UturnGreen
import com.example.uturn.ui.theme.UturnTextDark
import com.example.uturn.ui.theme.UturnTextGrey

@Composable
fun RegisterScreen(
    isLoading: Boolean,
    serverError: String?,
    onRegister: (fullName: String, email: String, phone: String, password: String) -> Unit,
    onBack: () -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    // Plain remember so the password is never written to saved instance state
    var password by remember { mutableStateOf("") }
    var fullNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        fullNameError = if (fullName.isBlank()) "Enter your full name" else null
        emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            "Enter a valid email address"
        } else null
        phoneError = if (phone.length < 9) "Enter a valid phone number" else null
        passwordError = if (password.length < 6) {
            "Password must be at least 6 characters"
        } else null
        return fullNameError == null && emailError == null &&
                phoneError == null && passwordError == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        // Circled back arrow (matches mockup)
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
        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = UturnGreen
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Save your details and track orders",
            fontSize = 15.sp,
            color = UturnTextGrey
        )
        Spacer(Modifier.height(36.dp))

        AuthTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            error = fullNameError
        )
        Spacer(Modifier.height(20.dp))
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            error = emailError
        )
        Spacer(Modifier.height(20.dp))
        AuthTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            keyboardType = KeyboardType.Phone,
            error = phoneError
        )
        Spacer(Modifier.height(20.dp))
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            keyboardType = KeyboardType.Password,
            error = passwordError
        )
        Spacer(Modifier.height(28.dp))

        if (serverError != null) {
            Text(
                text = serverError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(12.dp))
        }

        Button(
            onClick = {
                if (validate()) onRegister(fullName.trim(), email.trim(), phone.trim(), password)
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Create Account", fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Already have an Account? ", color = UturnTextGrey, fontSize = 14.sp)
            Text(
                text = "Log in",
                color = UturnGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onBack)
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}