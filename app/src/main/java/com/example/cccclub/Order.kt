package com.example.cccclub

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val items: List<CartItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val date: String = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date()),
    val placeNumber: String = "", // Номер места
    val duration: Int = 1, // Длительность в часа,
    val placeType: PlaceType? = null // Убедитесь, что стоит знак "?"


)

enum class OrderStatus {
    COMPLETED,       // Завершен
    CANCELLED,       // Отменен
    IN_PROGRESS      // В процессе
}

enum class PlaceType {
    COMPUTER,    // Игровой компьютер
    PS5,         // PlayStation 5
    TABLE,       // Настольные игры стол
    VR,          // VR очки место
    ESPORTS,     // Киберспорт арена
    KARAOKE,     // Караоке комната
    CINEMA,      // Кино зал
    CONFERENCE   // Конференц-зал
}

class OrderManager(private val context: android.content.Context) {

    private val prefs = context.getSharedPreferences("orders_prefs", android.content.Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "user_orders"

    fun createOrder(items: List<CartItem>, total: Double, placeType: PlaceType, placeNumber: String, duration: Int): Order {
        val order = Order(
            items = items.toList(),
            totalAmount = total,
            status = OrderStatus.IN_PROGRESS,
            placeType = placeType,
            placeNumber = placeNumber,
            duration = duration
        )

        val orders = getOrders().toMutableList()
        orders.add(0, order) // Добавляем новый заказ в начало

        saveOrders(orders)
        return order
    }

    fun getOrders(): List<Order> {
        val json = prefs.getString(key, null)
        return if (json == null) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Order>>() {}.type
            gson.fromJson(json, type)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val orders = getOrders().toMutableList()
        val orderIndex = orders.indexOfFirst { it.id == orderId }

        if (orderIndex != -1) {
            val updatedOrder = orders[orderIndex].copy(status = newStatus)
            orders[orderIndex] = updatedOrder
            saveOrders(orders)
        }
    }

    fun getOrderById(orderId: String): Order? {
        return getOrders().find { it.id == orderId }
    }

    private fun saveOrders(orders: List<Order>) {
        val json = gson.toJson(orders)
        prefs.edit().putString(key, json).apply()
    }

    // Для тестирования - создаем несколько тестовых заказов
    fun createTestOrders(userName: String = "Пользователь") {
        val testOrders = listOf(
            Order(
                id = "order_001",
                items = listOf(
                    CartItem("food_1", "Кола", "Напиток 0.5л", 100.0, "food", 2),
                    CartItem("food_15", "Картофель фри", "150г", 120.0, "food", 1)
                ),
                totalAmount = 320.0,
                status = OrderStatus.COMPLETED,
                date = "15.12.2023 18:30",
                placeType = PlaceType.COMPUTER,
                placeNumber = "Компьютер №7",
                duration = 3
            ),
            Order(
                id = "order_002",
                items = listOf(
                    CartItem("service_1", "Игровой компьютер", "Мощный ПК с RTX 4080", 300.0, "service", 3)
                ),
                totalAmount = 900.0,
                status = OrderStatus.IN_PROGRESS,
                date = "16.12.2023 14:15",
                placeType = PlaceType.PS5,
                placeNumber = "PlayStation №3",
                duration = 2
            ),
            Order(
                id = "order_003",
                items = listOf(
                    CartItem("food_11", "Пицца Маргарита", "30см, сыр, томаты", 350.0, "food", 1),
                    CartItem("food_8", "Кофе американо", "200мл", 80.0, "food", 2)
                ),
                totalAmount = 510.0,
                status = OrderStatus.COMPLETED,
                date = "10.12.2023 12:00",
                placeType = PlaceType.ESPORTS,
                placeNumber = "Арена №1",
                duration = 4
            ),
            Order(
                id = "order_004",
                items = listOf(
                    CartItem("food_13", "Бургер классический", "С говяжьей котлетой", 280.0, "food", 2),
                    CartItem("food_4", "Вода газированная", "0.5л", 60.0, "food", 3)
                ),
                totalAmount = 740.0,
                status = OrderStatus.CANCELLED,
                date = "05.12.2023 20:45",
                placeType = PlaceType.VR,
                placeNumber = "VR место №2",
                duration = 1
            )
        )

        saveOrders(testOrders)
    }

    fun clearOrders() {
        prefs.edit().remove(key).apply()
    }
}