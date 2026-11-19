package com.example.uturn.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Separate DataStore file from SessionManager's. Different name on purpose.
private val Context.ordersDataStore by preferencesDataStore(name = "orders")

/**
 * Local copy of the orders the server has confirmed. The server is the source of truth:
 * new orders are added here after the API accepts them, and for logged-in users the whole
 * list is replaced by what the API returns (that is how admin status changes arrive).
 */
class OrderRepository(context: Context) {

    private val store = context.applicationContext.ordersDataStore
    private val ordersKey = stringPreferencesKey("orders_json")

    val orders: Flow<List<Order>> = store.data.map { ordersFromJson(it[ordersKey]) }

    suspend fun add(order: Order) {
        store.edit { prefs ->
            val existing = ordersFromJson(prefs[ordersKey]).filter { it.number != order.number }
            prefs[ordersKey] = ordersToJson(listOf(order) + existing)
        }
    }

    suspend fun replaceAll(list: List<Order>) {
        store.edit { it[ordersKey] = ordersToJson(list) }
    }

    suspend fun clear() {
        store.edit { it.remove(ordersKey) }
    }
}