package com.example.uturn.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrdersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrderRepository(application)

    private val _placing = MutableStateFlow(false)
    val placing: StateFlow<Boolean> = _placing.asStateFlow()

    val orders: StateFlow<List<Order>> = repository.orders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    /**
     * The server creates the order (unpaid) and recomputes the prices. onPlaced receives the PayFast
     * page address; the order is marked paid only by PayFast's verified webhook on the server.
     */
    fun placeOrder(
        lines: List<CartLine>,
        method: PaymentMethod,
        delivery: DeliveryDetails,
        fulfilment: FulfilmentType,
        onPlaced: (Order, String?) -> Unit,
        onError: (String) -> Unit
    ) {
        if (_placing.value) return            // ignore double taps
        _placing.value = true
        viewModelScope.launch {
            try {
                val placed = ShopRepository.createOrder(lines, method, delivery, fulfilment)
                repository.add(placed.order)
                onPlaced(placed.order, placed.paymentUrl)
            } catch (e: ApiException) {
                onError(e.message ?: "Could not place your order. Please try again.")
            } finally {
                _placing.value = false
            }
        }
    }

    /** Logged-in users: pull their orders (and latest statuses) from the server. */
    fun refresh() {
        if (!TokenStore.hasToken) return
        viewModelScope.launch {
            try {
                repository.replaceAll(ShopRepository.myOrders())
            } catch (e: ApiException) {
                // Keep showing what we have; the next refresh will try again
            }
        }
    }

    fun clearLocal() {
        viewModelScope.launch { repository.clear() }
    }
}