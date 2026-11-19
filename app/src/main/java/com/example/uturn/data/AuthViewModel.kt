package com.example.uturn.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUi(val loading: Boolean = false, val error: String? = null)

class AuthViewModel : ViewModel() {
    private val _ui = MutableStateFlow(AuthUi())
    val ui: StateFlow<AuthUi> = _ui.asStateFlow()

    fun clearError() = _ui.update { it.copy(error = null) }

    fun login(email: String, password: String, onSuccess: (ShopUser) -> Unit) {
        if (_ui.value.loading) return
        _ui.value = AuthUi(loading = true)
        viewModelScope.launch {
            try {
                val user = ShopRepository.login(email, password)
                _ui.value = AuthUi()
                onSuccess(user)
            } catch (e: ApiException) {
                _ui.value = AuthUi(error = e.message)
            }
        }
    }

    fun register(name: String, email: String, password: String, phone: String, onSuccess: (ShopUser) -> Unit) {
        if (_ui.value.loading) return
        _ui.value = AuthUi(loading = true)
        viewModelScope.launch {
            try {
                val user = ShopRepository.register(name, email, password, phone)
                _ui.value = AuthUi()
                onSuccess(user)
            } catch (e: ApiException) {
                _ui.value = AuthUi(error = e.message)
            }
        }
    }
}