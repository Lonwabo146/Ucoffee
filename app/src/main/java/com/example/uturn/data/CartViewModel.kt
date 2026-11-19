package com.example.uturn.data


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

// All money values are in rands (Double), matching SizeOption.price
data class CartLine(
    val productId: Int,
    val name: String,
    val volumeMl: Int,
    val unitPrice: Double,
    val quantity: Int,
    val sizeId: Int = 0          // the API's id for this size; sent to the server when ordering
) {
    val lineTotal: Double get() = unitPrice * quantity
}

data class CartSummary(val subtotal: Double, val delivery: Double) {
    val total: Double get() = subtotal + delivery
}

class CartViewModel : ViewModel() {
    companion object { const val DELIVERY_FEE = 20.0 }

    private val _lines = MutableStateFlow<List<CartLine>>(emptyList())
    val lines = _lines.asStateFlow()

    val itemCount = lines
        .map { l -> l.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val summary = lines
        .map { l ->
            val subtotal = l.sumOf { it.lineTotal }
            CartSummary(subtotal, if (subtotal > 0) DELIVERY_FEE else 0.0)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, CartSummary(0.0, 0.0))

    fun add(line: CartLine) = _lines.update { current ->
        val match = current.find { it.productId == line.productId && it.volumeMl == line.volumeMl }
        if (match == null) current + line
        else current.map { if (it == match) it.copy(quantity = it.quantity + line.quantity) else it }
    }

    fun changeQuantity(line: CartLine, delta: Int) = _lines.update { current ->
        current.mapNotNull {
            if (it != line) it
            else if (it.quantity + delta <= 0) null
            else it.copy(quantity = it.quantity + delta)
        }
    }

    fun clear() = _lines.update { emptyList() }
}