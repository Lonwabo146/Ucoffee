package com.example.uturn.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogUi(
    val loading: Boolean = true,
    val error: String? = null,
    val products: List<Product> = emptyList()
)

class CatalogViewModel : ViewModel() {
    private val _ui = MutableStateFlow(CatalogUi())
    val ui: StateFlow<CatalogUi> = _ui.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }
            try {
                val list = ShopRepository.loadCatalog()
                ProductRepository.set(list)
                _ui.value = CatalogUi(loading = false, products = list)
            } catch (e: ApiException) {
                // Keep whatever we already had on screen, just report the problem
                _ui.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}