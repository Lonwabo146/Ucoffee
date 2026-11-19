package com.example.uturn.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uturn.components.AuthTextField
import com.example.uturn.data.PaymentMethod
import com.example.uturn.ui.theme.UturnCardBorder
import com.example.uturn.ui.theme.UturnGreen
import com.example.uturn.ui.theme.UturnTextDark
import com.example.uturn.ui.theme.UturnTextGrey

@Composable
fun ProfileScreen(
    displayName: String?,
    isLoggedIn: Boolean,
    phone: String?,
    address: String?,
    paymentMethod: PaymentMethod,
    onSaveDelivery: (phone: String, address: String) -> Unit,
    onSelectPayment: (PaymentMethod) -> Unit,
    onOrderHistory: () -> Unit,
    onLogin: () -> Unit,
    onLogout: () -> Unit
) {
    var showDelivery by rememberSaveable { mutableStateOf(false) }
    var showPayment by rememberSaveable { mutableStateOf(false) }
    var showAbout by rememberSaveable { mutableStateOf(false) }
    var showLogout by rememberSaveable { mutableStateOf(false) }

    val name = displayName?.trim()?.takeIf { it.isNotEmpty() } ?: "Guest"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = UturnGreen)
        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(UturnGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().uppercase(),
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = UturnTextDark)
                Text(
                    text = if (isLoggedIn) "Logged in" else "Browsing as guest",
                    fontSize = 14.sp,
                    color = UturnTextGrey
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        if (!isLoggedIn) {
            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Log in or create account", fontSize = 16.sp)
            }
            Spacer(Modifier.height(24.dp))
        }

        if (isLoggedIn) {
            val hasDelivery = !address.isNullOrBlank() || !phone.isNullOrBlank()
            ProfileRow(
                icon = Icons.Filled.LocationOn,
                title = "Delivery details",
                subtitle = if (hasDelivery) {
                    listOfNotNull(
                        address?.takeIf { it.isNotBlank() },
                        phone?.takeIf { it.isNotBlank() }
                    ).joinToString("\n")
                } else "Not set. Tap to add",
                onClick = { showDelivery = true }
            )
            Spacer(Modifier.height(12.dp))
            ProfileRow(
                icon = Icons.Filled.Payment,
                title = "Payment method",
                subtitle = paymentMethod.label,
                onClick = { showPayment = true }
            )
            Spacer(Modifier.height(12.dp))
        }
        ProfileRow(
            icon = Icons.Filled.Receipt,
            title = "Order history",
            subtitle = "See your past orders",
            onClick = onOrderHistory
        )
        Spacer(Modifier.height(12.dp))
        ProfileRow(
            icon = Icons.Filled.Info,
            title = "About U-Turn",
            subtitle = "Where your money goes",
            onClick = { showAbout = true }
        )
        if (isLoggedIn) {
            Spacer(Modifier.height(12.dp))
            ProfileRow(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Log out",
                subtitle = null,
                onClick = { showLogout = true }
            )
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showDelivery) {
        DeliveryDialog(
            initialPhone = phone.orEmpty(),
            initialAddress = address.orEmpty(),
            onDismiss = { showDelivery = false },
            onSave = { newPhone, newAddress ->
                onSaveDelivery(newPhone, newAddress)
                showDelivery = false
            }
        )
    }

    if (showPayment) {
        AlertDialog(
            onDismissRequest = { showPayment = false },
            title = { Text("Payment method") },
            text = {
                Column {
                    PaymentMethod.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = option == paymentMethod,
                                    role = Role.RadioButton,
                                    onClick = {
                                        onSelectPayment(option)
                                        showPayment = false
                                    }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = option == paymentMethod, onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(option.label, fontSize = 16.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "This is your default at checkout. You always pay on the provider's secure page.",
                        fontSize = 13.sp,
                        color = UturnTextGrey
                    )
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showPayment = false }) { Text("Close") } }
        )
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text("About U-Turn") },
            text = {
                Text(
                    "Every cup you buy funds U-Turn's homeless ministry: its stabilisation, " +
                            "rehabilitation and reintegration programme. Thank you for being part of it."
                )
            },
            confirmButton = { TextButton(onClick = { showAbout = false }) { Text("Close") } }
        )
    }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            title = { Text("Log out?") },
            text = { Text("Your cart will be emptied and your saved details removed from this phone.") },
            confirmButton = {
                TextButton(onClick = {
                    showLogout = false
                    onLogout()
                }) { Text("Log out") }
            },
            dismissButton = { TextButton(onClick = { showLogout = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun ProfileRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, UturnCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = UturnGreen)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = UturnTextDark)
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(subtitle, fontSize = 13.sp, color = UturnTextGrey)
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = UturnTextGrey
            )
        }
    }
}

@Composable
private fun DeliveryDialog(
    initialPhone: String,
    initialAddress: String,
    onDismiss: () -> Unit,
    onSave: (phone: String, address: String) -> Unit
) {
    var phone by remember { mutableStateOf(initialPhone) }
    var address by remember { mutableStateOf(initialAddress) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delivery details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AuthTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone Number",
                    keyboardType = KeyboardType.Phone,
                    error = phoneError
                )
                AuthTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Delivery Address",
                    error = addressError
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val digits = phone.filter { it.isDigit() }
                phoneError = if (digits.length < 9 || digits.length > 15) "Enter a valid phone number" else null
                addressError = if (address.trim().length < 8) "Enter your full delivery address" else null
                if (phoneError == null && addressError == null) {
                    onSave(phone.trim(), address.trim())
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
