package com.example.cccclub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FoodFragment : Fragment() {

    private lateinit var prefs: SharedPreferencesHelper
    private lateinit var cartManager: CartManager
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_food, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация MenuManager
        MenuManager.initialize(requireContext())

        prefs = SharedPreferencesHelper(requireContext())
        cartManager = CartManager(requireContext())
        foodRecyclerView = view.findViewById(R.id.foodRecyclerView)

        setupFoodList()
        loadItems("food") // Загружаем только еду

        // Для отладки
        println("DEBUG: FoodFragment created")
        MenuManager.printDebugInfo()
    }

    private fun setupFoodList() {
        adapter = FoodAdapter(emptyList(),
            onQuantityChange = { foodItem, quantity ->
                // Просто обновляем количество в памяти
                println("DEBUG: Quantity changed for ${foodItem.name}: $quantity")
            },
            onAddToCart = { foodItem, quantity ->
                if (prefs.isLoggedIn()) {
                    val cartItem = CartItem(
                        id = foodItem.id,
                        name = foodItem.name,
                        description = foodItem.description,
                        price = foodItem.price,
                        category = foodItem.category,
                        quantity = quantity
                    )
                    cartManager.addItem(cartItem)
                    Toast.makeText(requireContext(), "${foodItem.name} (${quantity} шт.) добавлено в корзину", Toast.LENGTH_SHORT).show()

                    // Сбросить количество после добавления
                    adapter.resetQuantities()
                } else {
                    Toast.makeText(requireContext(), "Для добавления в корзину требуется авторизация", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    startActivity(intent)
                }
            }
        )

        foodRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        foodRecyclerView.adapter = adapter
    }

    private fun loadItems(category: String) {
        println("DEBUG: Loading items for category: $category")

        // Получаем все элементы из MenuManager
        val allItems = MenuManager.getMenuItems()

        // Фильтруем по категории и доступности
        val filteredItems = allItems.filter {
            it.category == category && it.available
        }

        println("DEBUG: Found ${filteredItems.size} items for category '$category'")

        // Конвертируем MenuItem в FoodItem для адаптера
        val foodItems = filteredItems.map { menuItem ->
            FoodItem(
                id = menuItem.id,
                name = menuItem.name,
                description = menuItem.description,
                price = menuItem.price,
                category = menuItem.category
            )
        }

        // Обновляем адаптер
        adapter.updateItems(foodItems)

        // Показываем сообщение если нет элементов
        if (filteredItems.isEmpty()) {
            println("DEBUG: No items found for category '$category'")
            Toast.makeText(requireContext(), "Пока нет доступных позиций в этой категории", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        println("DEBUG: FoodFragment onResume - refreshing data")
        loadItems("food") // Загружаем данные при возвращении
    }

    data class FoodItem(
        val id: String,
        val name: String,
        val description: String,
        val price: Double,
        val category: String
    )
}