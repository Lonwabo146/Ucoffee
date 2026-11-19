package com.example.uturn.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uturn.components.AuthTextField
import com.example.uturn.data.CartLine
import com.example.uturn.data.CartSummary
import com.example.uturn.data.DeliveryDetails
import com.example.uturn.data.FulfilmentType
import com.example.uturn.data.PaymentMethod
import com.example.uturn.data.formatPrice
import com.example.uturn.data.toCents
import com.example.uturn.ui.theme.UturnCardBorder
import com.example.uturn.ui.theme.UturnGreen
import com.example.uturn.ui.theme.UturnTextDark
import com.example.uturn.ui.theme.UturnTextGrey

@Composable
fun CheckoutScreen(
    lines: List<CartLine>,
    summary: CartSummary,
    initialName: String,
    initialPhone: String,
    initialAddress: String,
    placing: Boolean,
    onBack: () -> Unit,
    onPay: (DeliveryDetails, PaymentMethod, FulfilmentType) -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf(initialName) }
    var phone by rememberSaveable { mutableStateOf(initialPhone) }
    var address by rememberSaveable { mutableStateOf(initialAddress) }
    var fullNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneError by rememberSaveable { mutableStateOf<String?>(null) }
    var addressError by rememberSaveable { mutableStateOf<String?>(null) }
    var fulfilment by rememberSaveable { mutableStateOf(FulfilmentType.DELIVERY) }

    val pickup = fulfilment == FulfilmentType.PICKUP
    val deliveryFee = if (pickup) 0.0 else summary.delivery
    val total = summary.subtotal + deliveryFee

    fun validate(): Boolean {
        fullNameError = if (fullName.isBlank()) "Enter your full name" else null
        val digits = phone.filter { it.isDigit() }
        phoneError = if (digits.length < 9 || digits.length > 15) "Enter a valid phone number" else null
        addressError = if (!pickup && address.trim().length < 8) "Enter your full delivery address" else null
        return fullNameError == null && phoneError == null && addressError == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
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
        Spacer(Modifier.height(24.dp))
        Text("Checkout", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = UturnGreen)
        Spacer(Modifier.height(24.dp))

        SectionTitle("How would you like it?")
        Spacer(Modifier.height(12.dp))
        ChoiceOption(
            label = "Delivery",
            selected = !pickup,
            onSelect = { fulfilment = FulfilmentType.DELIVERY }
        )
        Spacer(Modifier.height(10.dp))
        ChoiceOption(
            label = "Pickup in store (free)",
            selected = pickup,
            onSelect = { fulfilment = FulfilmentType.PICKUP }
        )
        Spacer(Modifier.height(28.dp))

        SectionTitle(if (pickup) "Your details" else "Delivery details")
        Spacer(Modifier.height(12.dp))
        AuthTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            error = fullNameError
        )
        Spacer(Modifier.height(16.dp))
        AuthTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            keyboardType = KeyboardType.Phone,
            error = phoneError
        )
        if (!pickup) {
            Spacer(Modifier.height(16.dp))
            AuthTextField(
                value = address,
                onValueChange = { address = it },
                label = "Delivery Address",
                error = addressError
            )
        } else {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "We'll have your order ready to collect in store.",
                fontSize = 13.sp,
                color = UturnTextGrey
            )
        }
        Spacer(Modifier.height(28.dp))

        SectionTitle("Payment method")
        Spacer(Modifier.height(12.dp))
        ChoiceOption(label = PaymentMethod.PAYFAST.label, selected = true, onSelect = {})
        Spacer(Modifier.height(10.dp))
        Text(
            text = "You'll pay on PayFast's secure page. We never see your card details.",
            fontSize = 13.sp,
            color = UturnTextGrey
        )
        Spacer(Modifier.height(28.dp))

        SectionTitle("Order summary")
        Spacer(Modifier.height(12.dp))
        lines.forEach { line ->
            SummaryRow(
                label = "${line.quantity} × ${line.name} (${line.volumeMl}ml)",
                value = formatPrice(line.lineTotal.toCents())
            )
            Spacer(Modifier.height(6.dp))
        }
        Spacer(Modifier.height(6.dp))
        HorizontalDivider(color = UturnCardBorder)
        Spacer(Modifier.height(10.dp))
        SummaryRow("Subtotal", formatPrice(summary.subtotal.toCents()))
        Spacer(Modifier.height(6.dp))
        SummaryRow(if (pickup) "Pickup" else "Delivery", formatPrice(deliveryFee.toCents()))
        Spacer(Modifier.height(10.dp))
        SummaryRow("Total", formatPrice(total.toCents()), bold = true)
        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                if (validate()) {
                    onPay(
                        DeliveryDetails(
                            fullName.trim(),
                            phone.trim(),
                            if (pickup) "Pickup in store" else address.trim()
                        ),
                        PaymentMethod.PAYFAST,
                        fulfilment
                    )
                }
            },
            enabled = lines.isNotEmpty() && !placing,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                if (placing) "Placing order…" else "Pay ${formatPrice(total.toCents())}",
                fontSize = 16.sp
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Sandbox mode: no real money is taken.",
            fontSize = 12.sp,
            color = UturnTextGrey,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UturnTextDark)
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = if (bold) 17.sp else 15.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold) UturnTextDark else UturnTextGrey
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = value,
            fontSize = if (bold) 17.sp else 15.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = UturnTextDark
        )
    }
}

@Composable
private fun ChoiceOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 1.dp,
                color = if (selected) UturnGreen else UturnCardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 16.sp, color = UturnTextDark)
    }
}