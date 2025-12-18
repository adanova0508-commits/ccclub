package com.example.cccclub

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "cart_items"

    fun addItem(item: CartItem) {
        val cart = getCartItems().toMutableList()

        // Проверяем, есть ли уже такой товар
        val existingItem = cart.find { it.id == item.id }
        if (existingItem != null) {
            existingItem.quantity += item.quantity
        } else {
            cart.add(item)
        }

        saveCart(cart)
    }

    fun removeItem(itemId: String) {
        val cart = getCartItems().toMutableList()
        cart.removeAll { it.id == itemId }
        saveCart(cart)
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        val cart = getCartItems().toMutableList()
        val item = cart.find { it.id == itemId }
        if (item != null) {
            if (newQuantity <= 0) {
                removeItem(itemId)
            } else {
                item.quantity = newQuantity
                saveCart(cart)
            }
        }
    }

    fun getCartItems(): List<CartItem> {
        val json = prefs.getString(key, null)
        return if (json == null) {
            emptyList()
        } else {
            val type = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson(json, type)
        }
    }

    fun getCartTotal(): Double {
        return getCartItems().sumOf { it.price * it.quantity }
    }

    fun clearCart() {
        prefs.edit().remove(key).apply()
    }

    private fun saveCart(cart: List<CartItem>) {
        val json = gson.toJson(cart)
        prefs.edit().putString(key, json).apply()
    }
}

data class CartItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String, // "service" или "food"
    var quantity: Int = 1

)