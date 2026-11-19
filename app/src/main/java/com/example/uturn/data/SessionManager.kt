package com.example.uturn.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "uturn_session")

class SessionManager(private val context: Context) {

    private val keyOnboardingDone = booleanPreferencesKey("onboarding_done")
    private val keyDisplayName = stringPreferencesKey("display_name")
    private val keyLoggedIn = booleanPreferencesKey("logged_in")
    private val keyPhone = stringPreferencesKey("phone")
    private val keyAddress = stringPreferencesKey("delivery_address")
    private val keyPaymentMethod = stringPreferencesKey("payment_method")

    val onboardingDone: Flow<Boolean> =
        context.dataStore.data.map { it[keyOnboardingDone] ?: false }

    // The name shown on Home's "Hello, ..." — saved at registration.
    // Later, login will overwrite this with the name from the API profile.
    val displayName: Flow<String?> =
        context.dataStore.data.map { it[keyDisplayName] }

    val isLoggedIn: Flow<Boolean> =
        context.dataStore.data.map { it[keyLoggedIn] ?: false }

    // Saved delivery details (used to prefill Checkout) and the default payment method
    val phone: Flow<String?> = context.dataStore.data.map { it[keyPhone] }
    val address: Flow<String?> = context.dataStore.data.map { it[keyAddress] }
    val paymentMethod: Flow<String?> = context.dataStore.data.map { it[keyPaymentMethod] }

    suspend fun setOnboardingDone() {
        context.dataStore.edit { it[keyOnboardingDone] = true }
    }

    suspend fun saveDisplayName(name: String) {
        context.dataStore.edit { it[keyDisplayName] = name }
    }

    suspend fun setLoggedIn(name: String?) {
        context.dataStore.edit {
            it[keyLoggedIn] = true
            if (name != null) it[keyDisplayName] = name
        }
    }

    /** Only the non-null values are written. */
    suspend fun saveDeliveryDetails(phone: String?, address: String?) {
        context.dataStore.edit {
            if (phone != null) it[keyPhone] = phone
            if (address != null) it[keyAddress] = address
        }
    }

    suspend fun savePaymentMethod(methodName: String) {
        context.dataStore.edit { it[keyPaymentMethod] = methodName }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[keyLoggedIn] = false
            // Clear personal details, but keep onboarding_done so users don't see the intro again
            it.remove(keyDisplayName)
            it.remove(keyPhone)
            it.remove(keyAddress)
            it.remove(keyPaymentMethod)
        }
    }
}
