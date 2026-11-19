package com.example.uturn.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uturn.components.QuantityStepper
import com.example.uturn.data.CartViewModel
import com.example.uturn.data.ProductRepository
import com.example.uturn.data.formatPrice
import com.example.uturn.ui.theme.UturnTextGrey



@Composable
fun CartScreen(vm: CartViewModel, onCheckout: () -> Unit, onBrowse: () -> Unit) {
    val lines by vm.lines.collectAsState()
    val summary by vm.summary.collectAsState()

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Text(
            "Cart", style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
        )

        if (lines.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your cart is empty", color = UturnTextGrey)
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = onBrowse) { Text("Browse the menu") }
                }
            }
        } else {
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lines, key = { "${it.productId}-${it.volumeMl}" }) { line ->
                    OutlinedCard {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            ProductRepository.byId(line.productId)?.imageRes?.let {
                                Image(
                                    painterResource(it), line.name,
                                    Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(12.dp))
                            }
                            Column(Modifier.weight(1f)) {
                                Text(line.name, color = MaterialTheme.colorScheme.primary)
                                Text("${line.volumeMl}ml", color = UturnTextGrey, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(6.dp))
                                QuantityStepper(
                                    line.quantity,
                                    onMinus = { vm.changeQuantity(line, -1) },
                                    onPlus = { vm.changeQuantity(line, +1) }
                                )
                            }
                            Text(formatPrice(line.lineTotal), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            HorizontalDivider()
            Column(Modifier.padding(16.dp)) {
                SummaryRow("Subtotal", summary.subtotal, bold = true)
                Text("Delivery is added at checkout (pickup is free).", color = UturnTextGrey, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) { Text("Go to Checkout") }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Double, bold: Boolean = false) {
    val weight = if (bold) FontWeight.Bold else FontWeight.Normal
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = weight)
        Text(formatPrice(amount), fontWeight = weight)
    }
}