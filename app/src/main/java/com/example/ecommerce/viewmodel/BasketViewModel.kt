package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ecommerce.screens.FoodItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BasketViewModel : ViewModel() {

    private val _basketItems = MutableStateFlow<Map<FoodItem, Int>>(emptyMap())
    val basketItems: StateFlow<Map<FoodItem, Int>> = _basketItems.asStateFlow()

    fun addItem(item: FoodItem) {
        _basketItems.update {
            val newBasket = it.toMutableMap()
            val currentQuantity = newBasket.getOrDefault(item, 0)
            newBasket[item] = currentQuantity + 1
            newBasket
        }
    }

    fun removeItem(item: FoodItem) {
        _basketItems.update {
            val newBasket = it.toMutableMap()
            val currentQuantity = newBasket.getOrDefault(item, 0)
            if (currentQuantity > 1) {
                newBasket[item] = currentQuantity - 1
            } else {
                newBasket.remove(item)
            }
            newBasket
        }
    }

    fun clearBasket() {
        _basketItems.value = emptyMap()
    }

    val totalPrice: Double
        get() = _basketItems.value.entries.sumOf { (item, quantity) ->
            item.price.removePrefix("$").toDouble() * quantity
        }
}
