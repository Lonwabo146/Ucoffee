package com.example.uturn.screens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uturn.R
import com.example.uturn.components.AuthTextField
import com.example.uturn.ui.theme.UturnGreen
import com.example.uturn.ui.theme.UturnTextGrey

@Composable
fun LoginScreen(
    isLoading: Boolean,
    serverError: String?,
    onLogin: (email: String, password: String) -> Unit,
    onSignUp: () -> Unit,
    onGuest: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    // Plain remember (not rememberSaveable) so the password is never written to saved instance state
    var password by remember { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    // Quick checks before anything is sent; the server does the real authentication.
    fun validate(): Boolean {
        emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            "Enter a valid email address"
        } else null
        passwordError = if (password.length < 6) {
            "Password must be at least 6 characters"
        } else null
        return emailError == null && passwordError == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Image(
            painter = painterResource(id = R.drawable.ucoffee_logo),
            contentDescription = "U-Turn Coffee",
            modifier = Modifier.height(72.dp)
        )
        Spacer(Modifier.height(36.dp))
        Text(
            text = "Welcome Back",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = UturnGreen
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Log in to order your coffee",
            fontSize = 15.sp,
            color = UturnTextGrey
        )
        Spacer(Modifier.height(36.dp))

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            error = emailError
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
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                text = "Forgot Password?",
                fontSize = 14.sp,
                color = UturnGreen,
                modifier = Modifier.clickable(onClick = onForgotPassword)
            )
        }
        Spacer(Modifier.height(20.dp))

        if (serverError != null) {
            Text(
                text = serverError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(12.dp))
        }

        Button(
            onClick = { if (validate()) onLogin(email.trim(), password) },
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
                Text("Log In", fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "or",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = UturnTextGrey
        )
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            onClick = onGuest,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continue as Guest", fontSize = 16.sp, color = UturnGreen)
        }
        Spacer(Modifier.height(28.dp))

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Don't have an Account? ", color = UturnTextGrey, fontSize = 14.sp)
            Text(
                text = "Sign Up",
                color = UturnGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onSignUp)
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}