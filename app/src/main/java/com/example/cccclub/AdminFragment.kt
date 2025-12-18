package com.example.cccclub

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

// Класс MenuItem
data class MenuItem(
    val id: String,
    var name: String,
    var description: String,
    var price: Double,
    var category: String,
    var available: Boolean,
    var imageUrl: String = ""
)

// Singleton MenuManager
object MenuManager {
    private lateinit var prefs: android.content.SharedPreferences
    private val gson = Gson()
    private const val KEY = "menu_items"

    private var isInitialized = false

    fun initialize(context: android.content.Context) {
        if (!isInitialized) {
            prefs = context.getSharedPreferences("menu_prefs", android.content.Context.MODE_PRIVATE)
            isInitialized = true
            println("DEBUG: MenuManager initialized successfully")
        }
    }

    fun addMenuItem(item: MenuItem) {
        if (!isInitialized) {
            println("ERROR: MenuManager not initialized!")
            return
        }

        println("DEBUG: Adding menu item: ${item.name} (id: ${item.id})")
        val items = getMenuItems().toMutableList()
        items.add(item)
        saveItems(items)
        println("DEBUG: Menu item added successfully")
    }

    fun updateMenuItem(item: MenuItem) {
        if (!isInitialized) {
            println("ERROR: MenuManager not initialized!")
            return
        }

        println("DEBUG: Updating menu item: ${item.name}")
        val items = getMenuItems().toMutableList()
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
            saveItems(items)
            println("DEBUG: Menu item updated successfully")
        } else {
            println("ERROR: Menu item not found for update")
        }
    }

    fun removeMenuItem(id: String) {
        if (!isInitialized) {
            println("ERROR: MenuManager not initialized!")
            return
        }

        println("DEBUG: Removing menu item with id: $id")
        val items = getMenuItems()
        val filteredItems = items.filter { it.id != id }
        saveItems(filteredItems)
        println("DEBUG: Menu item removed successfully")
    }

    fun getMenuItems(): List<MenuItem> {
        if (!isInitialized) {
            println("ERROR: MenuManager not initialized!")
            return emptyList()
        }

        println("DEBUG: Reading menu items from SharedPreferences...")
        val json = prefs.getString(KEY, null)

        return if (json == null || json.isEmpty()) {
            println("DEBUG: No menu items found in SharedPreferences, creating default")
            createDefaultMenu()
        } else {
            try {
                println("DEBUG: Parsing JSON from SharedPreferences")
                val type = object : TypeToken<List<MenuItem>>() {}.type
                val items = gson.fromJson<List<MenuItem>>(json, type) ?: emptyList()
                println("DEBUG: Successfully loaded ${items.size} menu items")
                items
            } catch (e: Exception) {
                println("ERROR: Failed to parse menu items: ${e.message}")
                e.printStackTrace()
                createDefaultMenu()
            }
        }
    }

    private fun createDefaultMenu(): List<MenuItem> {
        println("DEBUG: Creating default menu items")
        val defaultItems = listOf(
            MenuItem("1", "Пицца Маргарита", "Сыр, томаты, орегано", 350.0, "food", true),
            MenuItem("2", "Бургер", "Говяжья котлета, овощи", 280.0, "food", true),
            MenuItem("3", "Кофе американо", "200 мл", 80.0, "drink", true),
            MenuItem("4", "Кола", "0.5 л", 100.0, "drink", true),
            MenuItem("5", "Игровой компьютер", "Мощный ПК с RTX 4080", 300.0, "service", true),
            MenuItem("6", "PlayStation 5", "Консоль нового поколения", 250.0, "service", true)
        )
        saveItems(defaultItems)
        return defaultItems
    }

    private fun saveItems(items: List<MenuItem>) {
        try {
            println("DEBUG: Converting menu items to JSON...")
            val json = gson.toJson(items)
            println("DEBUG: Saving ${items.size} menu items to SharedPreferences")

            val editor = prefs.edit()
            editor.putString(KEY, json)
            val success = editor.commit() // Используем commit() для немедленного сохранения

            if (success) {
                println("DEBUG: Menu items saved SUCCESSFULLY to SharedPreferences")
            } else {
                println("ERROR: Failed to save menu items to SharedPreferences")
            }

        } catch (e: Exception) {
            println("ERROR: Exception while saving menu items: ${e.message}")
            e.printStackTrace()
        }
    }

    fun printDebugInfo() {
        println("DEBUG: ===== MENU MANAGER DEBUG INFO =====")
        println("DEBUG: MenuManager initialized: $isInitialized")
        val items = getMenuItems()
        println("DEBUG: Total menu items in storage: ${items.size}")
        items.forEachIndexed { index, item ->
            println("DEBUG: Item #$index: '${item.name}' (id: ${item.id}), price=${item.price}, available=${item.available}, category=${item.category}")
        }
        println("DEBUG: ===== END DEBUG INFO =====")
    }
}

class AdminFragment : Fragment() {

    private lateinit var orderManager: OrderManager

    // View элементы
    private lateinit var ordersListView: ListView
    private lateinit var usersListView: ListView
    private lateinit var dateRangeSpinner: Spinner
    private lateinit var filterStatusSpinner: Spinner
    private lateinit var btnExportExcel: Button
    private lateinit var btnAddMenuItem: Button
    private lateinit var btnManageStopList: Button
    private lateinit var btnViewCustomers: Button
    private lateinit var btnSendNotifications: Button
    private lateinit var btnLogout: Button
    private lateinit var btnExitAdmin: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_simple, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация MenuManager с отладкой
        println("DEBUG: AdminFragment.onViewCreated() - START")

        try {
            MenuManager.initialize(requireContext())
            println("DEBUG: MenuManager initialized in AdminFragment")
        } catch (e: Exception) {
            println("ERROR: Failed to initialize MenuManager: ${e.message}")
            e.printStackTrace()
        }

        orderManager = OrderManager(requireContext())

        // Для отладки
        println("DEBUG: Calling MenuManager.printDebugInfo()")
        MenuManager.printDebugInfo()

        // Инициализация view
        ordersListView = view.findViewById(R.id.ordersListView)
        usersListView = view.findViewById(R.id.usersListView)
        dateRangeSpinner = view.findViewById(R.id.dateRangeSpinner)
        filterStatusSpinner = view.findViewById(R.id.filterStatusSpinner)
        btnExportExcel = view.findViewById(R.id.btnExportExcel)
        btnAddMenuItem = view.findViewById(R.id.btnAddMenuItem)
        btnManageStopList = view.findViewById(R.id.btnManageStopList)
        btnViewCustomers = view.findViewById(R.id.btnViewCustomers)
        btnSendNotifications = view.findViewById(R.id.btnSendNotifications)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnExitAdmin = view.findViewById(R.id.btnExitAdmin)

        setupUI()
        setupLists()
        setupFilterSpinners()
        setupLogoutButton()
        setupExitAdminButton()

        println("DEBUG: AdminFragment.onViewCreated() - END")
    }

    private fun setupUI() {
        btnExportExcel.setOnClickListener {
            exportToExcel()
        }

        btnAddMenuItem.setOnClickListener {
            println("DEBUG: 'Add menu item' button clicked")
            showAddMenuItemDialog()
        }

        btnManageStopList.setOnClickListener {
            println("DEBUG: 'Manage stop list' button clicked")
            showStopListDialog()
        }

        btnViewCustomers.setOnClickListener {
            showCustomersDialog()
        }

        btnSendNotifications.setOnClickListener {
            showNotificationDialog()
        }
    }

    private fun setupLogoutButton() {
        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Выйти") { _, _ ->
                    val prefs = SharedPreferencesHelper(requireContext())
                    prefs.logout()
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private fun setupExitAdminButton() {
        btnExitAdmin.setOnClickListener {
            activity?.let {
                if (it is MainActivity) {
                    it.loadFragment(ProfileFragment())
                }
            }
        }
    }

    private fun setupLists() {
        // Список последних заказов
        val orders = orderManager.getOrders().take(10)
        val orderStrings = orders.map { order ->
            "${order.date.substring(0, 10)} - ${String.format("%.2f руб", order.totalAmount)}"
        }

        val orderAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            orderStrings
        )

        ordersListView.adapter = orderAdapter
        ordersListView.setOnItemClickListener { _, _, position, _ ->
            if (position < orders.size) {
                showOrderDetails(orders[position])
            }
        }

        // Список пользователей (симуляция)
        val users = listOf(
            "Иван Петров - vip клиент",
            "Мария Сидорова - 15 заказов",
            "Алексей Иванов - новый клиент",
            "Елена Кузнецова - предпочтение: PS5",
            "Дмитрий Смирнов - часто заказывает пиццу"
        )

        val userAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            users
        )
        usersListView.adapter = userAdapter
    }

    private fun setupFilterSpinners() {
        // Фильтр по дате
        val dateRanges = arrayOf(
            "Сегодня",
            "Неделя",
            "Месяц",
            "Все время"
        )

        val dateAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dateRanges
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        dateRangeSpinner.adapter = dateAdapter

        // Фильтр по статусу
        val statuses = arrayOf(
            "Все статусы",
            "В процессе",
            "Завершен",
            "Отменен"
        )

        val statusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statuses
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        filterStatusSpinner.adapter = statusAdapter
    }

    private fun showAddMenuItemDialog() {
        println("DEBUG: showAddMenuItemDialog() called")

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_menu_item, null)

        val etName = dialogView.findViewById<EditText>(R.id.etItemName)
        val etDescription = dialogView.findViewById<EditText>(R.id.etItemDescription)
        val etPrice = dialogView.findViewById<EditText>(R.id.etItemPrice)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val checkboxAvailable = dialogView.findViewById<CheckBox>(R.id.checkboxAvailable)

        // Настройка категорий
        val categories = arrayOf("Еда", "Напитки", "Услуги")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Добавить новую позицию")
            .setPositiveButton("Добавить") { _, _ ->
                val name = etName.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val priceText = etPrice.text.toString().trim()
                val category = when (spinnerCategory.selectedItemPosition) {
                    0 -> "food"
                    1 -> "drink"
                    2 -> "service"
                    else -> "other"
                }
                val available = checkboxAvailable.isChecked

                if (name.isEmpty() || priceText.isEmpty()) {
                    Toast.makeText(requireContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val price = priceText.toDoubleOrNull() ?: 0.0

                val newItem = MenuItem(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    description = description,
                    price = price,
                    category = category,
                    available = available,
                    imageUrl = ""
                )

                println("DEBUG: Attempting to add new item: ${newItem.name}")

                // Сохраняем через MenuManager
                MenuManager.addMenuItem(newItem)

                // Для отладки - немедленно проверяем сохранение
                println("DEBUG: After adding item, checking storage...")
                MenuManager.printDebugInfo()

                Toast.makeText(requireContext(), "Позиция добавлена: $name", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .setOnDismissListener {
                println("DEBUG: Add menu item dialog dismissed")
            }
            .show()
    }

    private fun showStopListDialog() {
        // Для отладки
        println("DEBUG: showStopListDialog() called")
        MenuManager.printDebugInfo()

        val items = MenuManager.getMenuItems()
        val unavailableItems = items.filter { !it.available }

        val itemNames = unavailableItems.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Стоп-лист (${unavailableItems.size} позиций)")
            .setItems(itemNames) { _, which ->
                showMenuItemDetails(unavailableItems[which])
            }
            .setPositiveButton("Добавить в стоп-лист") { _, _ ->
                showAddToStopListDialog()
            }
            .setNegativeButton("Закрыть", null)
            .show()
    }

    private fun showAddToStopListDialog() {
        val items = MenuManager.getMenuItems().filter { it.available }
        val itemNames = items.map { it.name }.toTypedArray()
        val selectedItems = BooleanArray(items.size)

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить в стоп-лист")
            .setMultiChoiceItems(itemNames, selectedItems) { _, which, isChecked ->
                selectedItems[which] = isChecked
            }
            .setPositiveButton("Сохранить") { _, _ ->
                selectedItems.forEachIndexed { index, selected ->
                    if (selected) {
                        val item = items[index].copy(available = false)
                        MenuManager.updateMenuItem(item)
                    }
                }
                Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show()
                // Обновляем отладку
                MenuManager.printDebugInfo()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showCustomersDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Информация о клиентах")
            .setMessage("Функция просмотра клиентов в разработке")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showNotificationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_send_notification, null)

        val etTitle = dialogView.findViewById<EditText>(R.id.etNotificationTitle)
        val etMessage = dialogView.findViewById<EditText>(R.id.etNotificationMessage)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerNotificationType)

        val types = arrayOf("Всем клиентам", "VIP клиентам", "Новым клиентам", "По предпочтениям")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Отправить уведомление")
            .setPositiveButton("Отправить") { _, _ ->
                val title = etTitle.text.toString()
                val message = etMessage.text.toString()

                if (title.isNotEmpty() && message.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Уведомление отправлено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showOrderDetails(order: Order) {
        AlertDialog.Builder(requireContext())
            .setTitle("Детали заказа #${order.id.takeLast(3).uppercase()}")
            .setMessage(
                """
                Дата: ${order.date}
                Статус: ${order.status}
                Сумма: ${String.format("%.2f руб", order.totalAmount)}
                Товары: ${order.items.joinToString { "${it.name} (x${it.quantity})" }}
                
                ${if (order.placeNumber.isNotEmpty()) "Место: ${order.placeNumber}" else ""}
                """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .setNeutralButton("Изменить статус") { _, _ ->
                showChangeStatusDialog(order)
            }
            .show()
    }

    private fun showMenuItemDetails(item: MenuItem) {
        AlertDialog.Builder(requireContext())
            .setTitle(item.name)
            .setMessage(
                """
                Описание: ${item.description}
                Цена: ${String.format("%.2f руб", item.price)}
                Категория: ${item.category}
                Доступность: ${if (item.available) "В наличии" else "В стоп-листе"}
                """.trimIndent()
            )
            .setPositiveButton("Изменить") { _, _ ->
                showEditMenuItemDialog(item)
            }
            .setNegativeButton(if (item.available) "В стоп-лист" else "Вернуть в продажу") { _, _ ->
                val updatedItem = item.copy(available = !item.available)
                MenuManager.updateMenuItem(updatedItem)
                Toast.makeText(requireContext(), "Статус изменен", Toast.LENGTH_SHORT).show()
                // Обновляем отладку
                MenuManager.printDebugInfo()
            }
            .show()
    }

    private fun showEditMenuItemDialog(item: MenuItem) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_menu_item, null)

        val etName = dialogView.findViewById<EditText>(R.id.etItemName).apply { setText(item.name) }
        val etDescription = dialogView.findViewById<EditText>(R.id.etItemDescription).apply { setText(item.description) }
        val etPrice = dialogView.findViewById<EditText>(R.id.etItemPrice).apply { setText(item.price.toString()) }
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val checkboxAvailable = dialogView.findViewById<CheckBox>(R.id.checkboxAvailable).apply { isChecked = item.available }

        val categories = arrayOf("Еда", "Напитки", "Услуги")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        spinnerCategory.setSelection(
            when (item.category) {
                "food" -> 0
                "drink" -> 1
                "service" -> 2
                else -> 0
            }
        )

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Редактировать позицию")
            .setPositiveButton("Сохранить") { _, _ ->
                val updatedItem = item.copy(
                    name = etName.text.toString().trim(),
                    description = etDescription.text.toString().trim(),
                    price = etPrice.text.toString().toDoubleOrNull() ?: item.price,
                    category = when (spinnerCategory.selectedItemPosition) {
                        0 -> "food"
                        1 -> "drink"
                        2 -> "service"
                        else -> item.category
                    },
                    available = checkboxAvailable.isChecked
                )

                MenuManager.updateMenuItem(updatedItem)

                // Для отладки
                println("DEBUG: Item updated: ${updatedItem.name}")
                MenuManager.printDebugInfo()

                Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Удалить") { _, _ ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Подтверждение удаления")
                    .setMessage("Удалить ${item.name}?")
                    .setPositiveButton("Удалить") { _, _ ->
                        MenuManager.removeMenuItem(item.id)

                        // Для отладки
                        println("DEBUG: Item removed: ${item.name}")
                        MenuManager.printDebugInfo()

                        Toast.makeText(requireContext(), "Позиция удалена", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
            .setNeutralButton("Отмена", null)
            .show()
    }

    private fun showChangeStatusDialog(order: Order) {
        val statuses = arrayOf("В процессе", "Завершен", "Отменен")

        AlertDialog.Builder(requireContext())
            .setTitle("Изменить статус заказа")
            .setItems(statuses) { _, which ->
                val newStatus = when (which) {
                    0 -> OrderStatus.IN_PROGRESS
                    1 -> OrderStatus.COMPLETED
                    2 -> OrderStatus.CANCELLED
                    else -> order.status
                }

                orderManager.updateOrderStatus(order.id, newStatus)
                Toast.makeText(requireContext(), "Статус обновлен", Toast.LENGTH_SHORT).show()
                setupLists()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun exportToExcel() {
        try {
            val orders = orderManager.getOrders()
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "orders_report_${dateFormat.format(Date())}.csv"

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            FileWriter(file).use { writer ->
                writer.write("ID;Дата;Статус;Сумма;Клиент;Товары\n")

                orders.forEach { order ->
                    val items = order.items.joinToString("; ") { "${it.name} x${it.quantity}" }
                    writer.write("${order.id};${order.date};${order.status};${order.totalAmount};${order.items.firstOrNull()?.name ?: "Неизвестно"};$items\n")
                }
            }

            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/csv")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(intent)
            Toast.makeText(requireContext(), "Отчет сохранен в Загрузки", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка при экспорте: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        println("DEBUG: AdminFragment.onResume() called")
        setupLists()
        // Обновляем отладку при возвращении на фрагмент
        MenuManager.printDebugInfo()
    }
}