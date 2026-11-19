package com.example.uturn.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uturn.data.FulfilmentType
import com.example.uturn.data.Order
import com.example.uturn.data.OrderStatus
import com.example.uturn.data.formatPrice
import com.example.uturn.ui.theme.UturnCardBorder
import com.example.uturn.ui.theme.UturnGreen
import com.example.uturn.ui.theme.UturnTextDark
import com.example.uturn.ui.theme.UturnTextGrey

@Composable
fun OrderConfirmationScreen(
    order: Order?,
    onTrackOrder: () -> Unit,
    onContinueShopping: () -> Unit
) {
    if (order == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = UturnGreen)
        }
        return
    }

    val cancelled = order.status == OrderStatus.CANCELLED
    val title = when {
        cancelled -> "Order cancelled"
        order.isPaid -> "Payment received!"
        else -> "Awaiting payment"
    }
    val subtitle = when {
        cancelled -> "This order was cancelled."
        order.isPaid -> "Thank you. Your cup helps fund U-Turn's stabilisation, " +
                "rehabilitation and reintegration programme."
        else -> "Finish paying on PayFast's page. This screen updates once your payment is confirmed."
    }
    val icon = when {
        cancelled -> Icons.Filled.Close
        order.isPaid -> Icons.Filled.CheckCircle
        else -> Icons.Filled.Info
    }
    val tint = when {
        cancelled -> Color(0xFFB3261E)
        order.isPaid -> UturnGreen
        else -> UturnTextGrey
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(96.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = tint)
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 15.sp,
            color = UturnTextGrey,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, UturnCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                DetailRow("Order number", order.displayNumber, bold = true)
                Spacer(Modifier.height(12.dp))
                DetailRow("Total", formatPrice(order.totalCents), bold = true)
                Spacer(Modifier.height(12.dp))
                DetailRow("Payment", "${order.paymentMethod.label} · ${if (order.isPaid) "Paid" else "Unpaid"}")
                Spacer(Modifier.height(12.dp))
                DetailRow("Status", order.status.label)
                Spacer(Modifier.height(12.dp))
                if (order.fulfilmentType == FulfilmentType.PICKUP) {
                    DetailRow("Pickup", "Collect in store")
                } else {
                    DetailRow("Estimated delivery", "30–45 minutes")
                }
            }
        }
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onTrackOrder,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Track order", fontSize = 16.sp)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onContinueShopping,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continue shopping", fontSize = 16.sp, color = UturnGreen)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 15.sp, color = UturnTextGrey)
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = UturnTextDark
        )
    }
}