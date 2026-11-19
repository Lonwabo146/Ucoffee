package com.example.uturn.components




import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun QuantityStepper(
    quantity: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(onClick = onMinus, modifier = Modifier.size(36.dp)) { Text("−") }
        Text("$quantity", Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)
        OutlinedIconButton(onClick = onPlus, modifier = Modifier.size(36.dp)) { Text("+") }
    }
}