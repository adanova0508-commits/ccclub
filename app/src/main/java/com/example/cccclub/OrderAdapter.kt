package com.example.cccclub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private val orders: List<Order>,
    private val onOrderClick: (Order) -> Unit,
    private val onRepeatOrder: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.orderId)
        val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val orderItems: TextView = itemView.findViewById(R.id.orderItems)
        val orderPlace: TextView = itemView.findViewById(R.id.orderAddress) // Переименуем в orderPlace
        val orderTotal: TextView = itemView.findViewById(R.id.orderTotal)
        val orderDuration: TextView = itemView.findViewById(R.id.orderDuration)
        val btnRepeatOrder: Button = itemView.findViewById(R.id.btnRepeatOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        // Защита от null
        if (!order.placeNumber.isNullOrEmpty()) {
            val placeTypeText = when (order.placeType) {
                PlaceType.COMPUTER -> "Компьютер"
                PlaceType.PS5 -> "PlayStation 5"
                PlaceType.TABLE -> "Стол для настольных игр"
                PlaceType.VR -> "VR место"
                PlaceType.ESPORTS -> "Киберспорт арена"
                PlaceType.KARAOKE -> "Караоке комната"
                PlaceType.CINEMA -> "Кино зал"
                PlaceType.CONFERENCE -> "Конференц-зал"
                null -> "Место" // Явно обрабатываем null
                else -> "Место"
            }
            holder.orderPlace.text = "$placeTypeText: ${order.placeNumber}"
            holder.orderPlace.visibility = View.VISIBLE
        } else {
            holder.orderPlace.visibility = View.GONE
        }
        holder.btnRepeatOrder.setOnClickListener {
            onRepeatOrder(order)
        }
        // Формируем список товаров с защитой от null
        val itemsText = order.items?.joinToString(", ") {
            val name = it?.name ?: ""
            val quantity = it?.quantity ?: 0
            "$name ($quantity)"
        } ?: ""
        holder.orderItems.text = itemsText

        // Настраиваем статус
        when (order.status) {
            OrderStatus.COMPLETED -> {
                holder.orderStatus.text = "Завершен"
                holder.orderStatus.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.status_completed_bg)
                holder.orderStatus.setTextColor(holder.itemView.context.getColor(android.R.color.white))
                holder.btnRepeatOrder.visibility = View.VISIBLE
            }
            OrderStatus.CANCELLED -> {
                holder.orderStatus.text = "Отменен"
                holder.orderStatus.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.status_cancelled_bg)
                holder.orderStatus.setTextColor(holder.itemView.context.getColor(android.R.color.white))
                holder.btnRepeatOrder.visibility = View.GONE
            }
            OrderStatus.IN_PROGRESS -> {
                holder.orderStatus.text = "В процессе"
                holder.orderStatus.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.status_in_progress_bg)
                holder.orderStatus.setTextColor(holder.itemView.context.getColor(android.R.color.white))
                holder.btnRepeatOrder.visibility = View.GONE
            }
            else -> {
                holder.orderStatus.text = "Неизвестен"
                holder.orderStatus.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.status_in_progress_bg)
                holder.orderStatus.setTextColor(holder.itemView.context.getColor(android.R.color.white))
                holder.btnRepeatOrder.visibility = View.GONE
            }
        }

        // Показываем место если есть
        if (!order.placeNumber.isNullOrEmpty()) {
            val placeTypeText = when (order.placeType) {
                PlaceType.COMPUTER -> "Компьютер"
                PlaceType.PS5 -> "PlayStation 5"
                PlaceType.TABLE -> "Стол для настольных игр"
                PlaceType.VR -> "VR место"
                PlaceType.ESPORTS -> "Киберспорт арена"
                PlaceType.KARAOKE -> "Караоке комната"
                PlaceType.CINEMA -> "Кино зал"
                PlaceType.CONFERENCE -> "Конференц-зал"
                else -> "Место"
            }
            holder.orderPlace.text = "$placeTypeText: ${order.placeNumber}"
            holder.orderPlace.visibility = View.VISIBLE
        } else {
            holder.orderPlace.visibility = View.GONE
        }

        // Обработчики кликов
        holder.itemView.setOnClickListener {
            onOrderClick(order)
        }

        holder.btnRepeatOrder.setOnClickListener {
            onRepeatOrder(order)
        }
    }

    override fun getItemCount(): Int = orders.size
}