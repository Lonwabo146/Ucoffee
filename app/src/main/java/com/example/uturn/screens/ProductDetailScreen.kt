package com.example.uturn.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.uturn.components.QuantityStepper
import com.example.uturn.data.CartLine
import com.example.uturn.data.Product
import com.example.uturn.data.formatPrice
import com.example.uturn.ui.theme.UturnTextGrey // use the package your components folder actually has
@Composable
fun ProductDetailScreen(
    product: Product,
    onBack: () -> Unit,
    onAddToCart: (CartLine) -> Unit
) {
    var selected by remember(product.id) { mutableStateOf(product.displaySize) }
    var quantity by remember(product.id) { mutableIntStateOf(1) }
    var favourite by remember(product.id) { mutableStateOf(false) }
    val totalCents = selected.price * quantity
    val green = MaterialTheme.colorScheme.primary   // swap for your brand green if you have one

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().height(260.dp).background(Color(0xFFE0E0E0))) {
            product.imageRes?.let {
                Image(painterResource(it), product.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            IconButton(onClick = { favourite = !favourite }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                Icon(if (favourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Favourite")
            }
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)) {
            Text(product.name, style = MaterialTheme.typography.headlineMedium, color = green)
            Text("Volume: ${selected.volumeMl} ml", color = UturnTextGrey)
            Spacer(Modifier.height(8.dp))
            Text(formatPrice(selected.price), style = MaterialTheme.typography.headlineSmall, color = green)
            Spacer(Modifier.height(12.dp))
            Text(product.description, color = UturnTextGrey)
            Spacer(Modifier.height(20.dp))
            Text("Size:")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                product.sizes.forEach { option ->
                    FilterChip(
                        selected = option == selected,
                        onClick = { selected = option },
                        label = { Text("${option.volumeMl}ml") }
                    )
                }
            }
        }

        HorizontalDivider()
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            QuantityStepper(
                quantity,
                onMinus = { if (quantity > 1) quantity-- },
                onPlus = { quantity++ }
            )
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = {
                    onAddToCart(
                        CartLine(
                            productId = product.id,
                            name = product.name,
                            volumeMl = selected.volumeMl,
                            unitPrice = selected.price,
                            quantity = quantity,
                            sizeId = selected.sizeId
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            ) { Text("Add to Cart - ${formatPrice(totalCents)}") }
        }
    }
}