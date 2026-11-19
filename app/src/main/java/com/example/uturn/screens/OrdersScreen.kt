package com.example.uturn.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import com.example.uturn.data.Order
import com.example.uturn.data.OrderStatus
import com.example.uturn.data.formatPrice
import com.example.uturn.ui.theme.UturnCardBorder
import com.example.uturn.ui.theme.UturnGreen
import com.example.uturn.ui.theme.UturnTextDark
import com.example.uturn.ui.theme.UturnTextGrey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    orders: List<Order>,
    onBrowse: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("My Orders", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = UturnGreen)
        Spacer(Modifier.height(16.dp))

        if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No orders yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = UturnTextDark
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Your orders will show up here.",
                        fontSize = 15.sp,
                        color = UturnTextGrey,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = onBrowse, shape = RoundedCornerShape(12.dp)) {
                        Text("Browse coffee")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(orders, key = { it.number }) { order ->
                    OrderCard(order)
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    val dateText = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault())
        .format(Date(order.createdAtMillis))

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, UturnCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.displayNumber,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = UturnTextDark
                )
                StatusBadge(order.status)
            }
            Spacer(Modifier.height(2.dp))
            Text("$dateText · ${order.fulfilmentType.label}", fontSize = 13.sp, color = UturnTextGrey)
            Spacer(Modifier.height(12.dp))
            order.lines.forEach { line ->
                Text(
                    text = "${line.quantity} × ${line.name} (${line.volumeMl}ml)",
                    fontSize = 14.sp,
                    color = UturnTextDark
                )
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = UturnCardBorder)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontSize = 15.sp, color = UturnTextGrey)
                Text(
                    text = formatPrice(order.totalCents),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = UturnTextDark
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: OrderStatus) {
    val (background, content) = when (status) {
        OrderStatus.DELIVERED -> Color(0xFFDCEFE0) to Color(0xFF1B5E20)
        OrderStatus.READY_FOR_PICKUP -> Color(0xFFDCEFE0) to Color(0xFF1B5E20)
        OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFDDEBFA) to Color(0xFF0D47A1)
        OrderStatus.CANCELLED -> Color(0xFFFADBD8) to Color(0xFFB3261E)
        OrderStatus.AWAITING_PAYMENT -> Color(0xFFEDEDED) to Color(0xFF555555)
        else -> Color(0xFFFFF0D1) to Color(0xFF8A5A00)
    }
    Surface(shape = RoundedCornerShape(50), color = background) {
        Text(
            text = status.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = content,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}