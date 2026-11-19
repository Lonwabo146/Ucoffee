package com.example.uturn.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.uturn.data.CatalogUi
import com.example.uturn.data.Product
import com.example.uturn.data.formatPrice
import com.example.uturn.ui.theme.UturnCardBorder
import com.example.uturn.ui.theme.UturnGreen
import com.example.uturn.ui.theme.UturnTextDark
import com.example.uturn.ui.theme.UturnTextGrey

private val chipLabels = listOf("All", "Coffees", "Tea", "Cold")

@Composable
fun HomeScreen(
    displayName: String?,
    catalog: CatalogUi,
    onRetry: () -> Unit,
    onProductClick: (Product) -> Unit = {}
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }

    val filteredProducts = catalog.products.filter { product ->
        val matchesCategory = selectedCategory == "All" ||
                product.category.label == selectedCategory
        val matchesSearch = query.isBlank() ||
                product.name.contains(query, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Greeting + notification bell
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Hello", fontSize = 15.sp, color = UturnTextGrey)
                // Shows the saved name; guests (no name) see "Guest"
                Text(
                    text = displayName?.takeIf { it.isNotBlank() } ?: "Guest",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = UturnGreen
                )
            }
            IconButton(onClick = { /* Notifications screen — coming step */ }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = UturnTextDark
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        // Search bar
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search coffee, beans", color = UturnTextGrey) },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search", tint = UturnTextGrey)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = UturnGreen,
                cursorColor = UturnGreen
            )
        )
        Spacer(Modifier.height(20.dp))

        // Category chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chipLabels.forEach { label ->
                val selected = selectedCategory == label
                FilterChip(
                    selected = selected,
                    onClick = { selectedCategory = label },
                    label = { Text(label) },
                    border = BorderStroke(1.dp, UturnGreen),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        labelColor = UturnGreen,
                        selectedContainerColor = UturnGreen,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        // Menu area: loading / error / empty / product grid (2 columns)
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            when {
                catalog.products.isEmpty() && catalog.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UturnGreen
                    )
                }

                catalog.products.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = catalog.error ?: "No drinks available right now.",
                            color = UturnTextGrey,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onRetry) { Text("Try again") }
                    }
                }

                filteredProducts.isEmpty() -> {
                    Text(
                        text = "No drinks match your search.",
                        color = UturnTextGrey,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            ProductCard(product = product, onClick = { onProductClick(product) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, UturnCardBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val imageRes = product.imageRes
            val imageUrl = product.imageUrl

            when {
                imageRes != null -> Image(
                    painter = painterResource(imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                imageUrl != null -> AsyncImage(
                    model = imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                else -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEDEDED)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Coffee,
                        contentDescription = null,
                        tint = UturnTextGrey,
                        modifier = Modifier.height(40.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = product.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = UturnGreen
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${product.displaySize.volumeMl}ml",
                fontSize = 12.sp,
                color = UturnTextGrey
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatPrice(product.displaySize.price),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = UturnGreen
            )
        }
    }
}