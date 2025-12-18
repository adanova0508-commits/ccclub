package com.example.cccclub

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileFragment : Fragment() {

    private lateinit var prefs: SharedPreferencesHelper
    private lateinit var orderManager: OrderManager
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userPhoneTextView: TextView
    private lateinit var userPlaceTextView: TextView
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SharedPreferencesHelper(requireContext())
        orderManager = OrderManager(requireContext())

        // Инициализация view
        userNameTextView = view.findViewById(R.id.userName)
        userEmailTextView = view.findViewById(R.id.userEmail)
        userPhoneTextView = view.findViewById(R.id.userPhone)
        userPlaceTextView = view.findViewById(R.id.userAddress)
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnLogout = view.findViewById(R.id.btnLogout)

        loadProfileData()
        setupOrdersList()
        setupButtons()
    }

    private fun loadProfileData() {
        userNameTextView.text = prefs.getUserName()
        userEmailTextView.text = prefs.getUserEmail()

        val phone = prefs.getProfilePhone()
        if (phone.isNotEmpty()) {
            userPhoneTextView.text = "Телефон: $phone"
            userPhoneTextView.visibility = View.VISIBLE
        } else {
            userPhoneTextView.visibility = View.GONE
        }

        val place = prefs.getProfilePreferredPlace()
        if (place.isNotEmpty()) {
            userPlaceTextView.text = "Предпочитаемое место: $place"
            userPlaceTextView.visibility = View.VISIBLE
        } else {
            userPlaceTextView.visibility = View.GONE
        }
    }

    private fun setupOrdersList() {
        val orders = orderManager.getOrders()

        // Отфильтруем null, если они есть
        val safeOrders = orders.filterNotNull()

        if (safeOrders.isEmpty()) {
            view?.findViewById<TextView>(R.id.noOrdersText)?.visibility = View.VISIBLE
            ordersRecyclerView.visibility = View.GONE
        } else {
            view?.findViewById<TextView>(R.id.noOrdersText)?.visibility = View.GONE
            ordersRecyclerView.visibility = View.VISIBLE

            val adapter = OrderAdapter(
                safeOrders,
                onOrderClick = { order ->
                    showOrderDetails(order)
                },
                onRepeatOrder = { order ->
                    repeatOrder(order)
                }
            )

            ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            ordersRecyclerView.adapter = adapter
        }
    }


    private fun setupButtons() {
        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Выйти") { _, _ ->
                    prefs.logout()
                    Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT)
                        .show()

                    // Переходим на экран авторизации
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
        // Проверяем, является ли пользователь админом
        if (prefs.getProfileRole() == "admin") {
            val btnGoToAdmin = view?.findViewById<Button>(R.id.btnGoToAdmin) // добавь в XML
            btnGoToAdmin?.visibility = View.VISIBLE
            btnGoToAdmin?.setOnClickListener {
                // Переходим в админку
                (activity as MainActivity).loadFragment(AdminFragment())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Обновляем список заказов каждый раз, когда фрагмент становится видимым
        setupOrdersList()
    }

    private fun showEditProfileDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)

        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
        val etPhone = dialogView.findViewById<EditText>(R.id.etPhone)
        val etPreferredPlace = dialogView.findViewById<EditText>(R.id.etPreferredPlace)

        // Заполняем текущие данные
        etName.setText(prefs.getUserName())
        etEmail.setText(prefs.getUserEmail())
        etPhone.setText(prefs.getProfilePhone())
        etPreferredPlace.setText(prefs.getProfilePreferredPlace())

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Редактировать профиль")
            .setPositiveButton("Сохранить") { _, _ ->
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val preferredPlace = etPreferredPlace.text.toString().trim()

                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Заполните обязательные поля",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                // Обновляем данные профиля
                prefs.updateProfile(name, email, phone, preferredPlace)
                loadProfileData()
                Toast.makeText(requireContext(), "Данные сохранены", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showOrderDetails(order: Order) {
        // 1. Безопасно формируем список товаров
        val itemsText = order.items?.joinToString("\n") {
            "• ${it.name} (${it.quantity} шт.) - ${it.price * it.quantity} руб"
        } ?: "Нет товаров"

        // 2. Определяем статус
        val statusText = when (order.status) {
            OrderStatus.COMPLETED -> "Завершен"
            OrderStatus.CANCELLED -> "Отменен"
            OrderStatus.IN_PROGRESS -> "В процессе"
            else -> "Неизвестно"
        }

        // 3. Ключевое исправление: безопасная обработка PlaceType
        // НЕ вызываем .ordinal() или любые другие методы напрямую у order.placeType
        val placeTypeText = when (order.placeType) {
            PlaceType.COMPUTER -> "Компьютер"
            PlaceType.PS5 -> "PlayStation 5"
            PlaceType.TABLE -> "Стол для настольных игр"
            PlaceType.VR -> "VR место"
            PlaceType.ESPORTS -> "Киберспорт арена"
            PlaceType.KARAOKE -> "Караоке комната"
            PlaceType.CINEMA -> "Кино зал"
            PlaceType.CONFERENCE -> "Конференц-зал"
            else -> "Не указано" // Сюда попадёт и null, и любой другой неучтённый тип
        }

        // 4. Формируем сообщение
        AlertDialog.Builder(requireContext())
            .setTitle("Детали заказа #${order.id.takeLast(3).uppercase()}")
            .setMessage(
                """
            Дата: ${order.date}
            Длительность: ${order.duration} час.
            Статус: $statusText
            Тип места: $placeTypeText
            Место: ${if (order.placeNumber.isNotEmpty()) order.placeNumber else "Не указано"}
            
            Товары:
            $itemsText
            
            Итого: ${order.totalAmount} руб
            """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .show()
    }
    private fun repeatOrder(order: Order) {
        // Здесь можно добавить логику повторения заказа, например, добавление всех товаров из заказа в корзину
        Toast.makeText(requireContext(), "Заказ будет повторен (в разработке)", Toast.LENGTH_SHORT).show()
    }


}