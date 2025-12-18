package com.example.cccclub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(
    private var items: List<FoodFragment.FoodItem>,
    private val onQuantityChange: (FoodFragment.FoodItem, Int) -> Unit,
    private val onAddToCart: (FoodFragment.FoodItem, Int) -> Unit
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    // Храним количество для каждого элемента
    private val quantities = mutableMapOf<String, Int>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.foodName)
        val categoryTextView: TextView = itemView.findViewById(R.id.foodCategory)
        val descriptionTextView: TextView = itemView.findViewById(R.id.foodDescription)
        val priceTextView: TextView = itemView.findViewById(R.id.foodPrice)
        val quantityTextView: TextView = itemView.findViewById(R.id.tvFoodQuantity)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecreaseFood)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncreaseFood)
        val btnAdd: Button = itemView.findViewById(R.id.btnAddFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Устанавливаем начальное количество
        if (!quantities.containsKey(item.id)) {
            quantities[item.id] = 1
        }
        val currentQuantity = quantities[item.id] ?: 1

        // Заполняем данные
        holder.nameTextView.text = item.name
        holder.categoryTextView.text = item.category
        holder.descriptionTextView.text = item.description
        holder.priceTextView.text = String.format("%.0f руб", item.price)
        holder.quantityTextView.text = currentQuantity.toString()

        // Обработчики кнопок +/-
        holder.btnDecrease.setOnClickListener {
            val quantity = quantities[item.id] ?: 1
            if (quantity > 1) {
                quantities[item.id] = quantity - 1
                holder.quantityTextView.text = (quantity - 1).toString()
                onQuantityChange(item, quantity - 1)
            }
        }

        holder.btnIncrease.setOnClickListener {
            val quantity = quantities[item.id] ?: 1
            quantities[item.id] = quantity + 1
            holder.quantityTextView.text = (quantity + 1).toString()
            onQuantityChange(item, quantity + 1)
        }

        // Кнопка добавления в корзину
        holder.btnAdd.setOnClickListener {
            val quantity = quantities[item.id] ?: 1
            onAddToCart(item, quantity)
        }

        // Клик по всему элементу
        holder.itemView.setOnClickListener {
            // Можно показать детали
        }
    }

    override fun getItemCount(): Int = items.size

    // МЕТОД ДЛЯ ОБНОВЛЕНИЯ ДАННЫХ
    fun updateItems(newItems: List<FoodFragment.FoodItem>) {
        this.items = newItems
        notifyDataSetChanged()
        println("DEBUG: FoodAdapter updated with ${newItems.size} items")
    }

    // Метод для сброса количества (опционально)
    fun resetQuantities() {
        quantities.clear()
        notifyDataSetChanged()
    }
}