package com.example.cccclub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onQuantityChange: (String, Int) -> Unit,
    private val onRemoveClick: (String) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvCartItemName)
        val description: TextView = itemView.findViewById(R.id.tvCartItemDescription)
        val price: TextView = itemView.findViewById(R.id.tvCartItemPrice)
        val quantity: TextView = itemView.findViewById(R.id.tvCartItemQuantity)
        val total: TextView = itemView.findViewById(R.id.tvCartItemTotal)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        val removeButton: Button = itemView.findViewById(R.id.btnRemoveCartItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.name.text = cartItem.name
        holder.description.text = cartItem.description
        holder.price.text = "Цена: ${String.format("%.2f", cartItem.price)} руб"
        holder.quantity.text = cartItem.quantity.toString()
        holder.total.text = "Сумма: ${String.format("%.2f", cartItem.price * cartItem.quantity)} руб"

        // Кнопка уменьшения количества
        holder.btnDecrease.setOnClickListener {
            if (cartItem.quantity > 1) {
                val newQuantity = cartItem.quantity - 1
                onQuantityChange(cartItem.id, newQuantity)
            }
        }

        // Кнопка увеличения количества
        holder.btnIncrease.setOnClickListener {
            val newQuantity = cartItem.quantity + 1
            onQuantityChange(cartItem.id, newQuantity)
        }

        // Кнопка удаления товара
        holder.removeButton.setOnClickListener {
            onRemoveClick(cartItem.id)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}