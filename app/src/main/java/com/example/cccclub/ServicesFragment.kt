package com.example.cccclub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class ServicesFragment : Fragment() {

    private lateinit var prefs: SharedPreferencesHelper
    private lateinit var cartManager: CartManager

    // Список услуг с ценами
    private val services = listOf(
        ServiceItem("service_1", "Игровой компьютер", "Мощный ПК с RTX 4080, 32GB RAM, 240Hz монитор", 300.0),
        ServiceItem("service_2", "PlayStation 5", "Новейшая консоль с библиотекой игр", 250.0),
        ServiceItem("service_3", "Настольные игры", "Большой выбор настольных игр для компании", 100.0),
        ServiceItem("service_4", "VR очки", "Виртуальная реальность с полным погружением", 400.0),
        ServiceItem("service_5", "Киберспорт арена", "Соревновательная зона для киберспорта", 500.0),
        ServiceItem("service_6", "Караоке", "Профессиональная караоке система", 150.0),
        ServiceItem("service_7", "Кино зал", "Комфортный просмотр фильмов на большом экране", 200.0),
        ServiceItem("service_8", "Конференц-зал", "Для бизнес-встреч и мероприятий", 350.0)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SharedPreferencesHelper(requireContext())
        cartManager = CartManager(requireContext())
        setupServices(view)
    }

    private fun setupServices(view: View) {
        val serviceButtons = mapOf<String, Button>(
            "service_1" to view.findViewById(R.id.btnAddGamingPC),
            "service_2" to view.findViewById(R.id.btnAddPS5),
            "service_3" to view.findViewById(R.id.btnAddBoardGames),
            "service_4" to view.findViewById(R.id.btnAddVR),
            "service_5" to view.findViewById(R.id.btnAddEsports),
            "service_6" to view.findViewById(R.id.btnAddKaraoke),
            "service_7" to view.findViewById(R.id.btnAddCinema),
            "service_8" to view.findViewById(R.id.btnAddConference)
        )

        serviceButtons.forEach { (id, button) ->
            button?.setOnClickListener {
                if (prefs.isLoggedIn()) {
                    val service = services.find { it.id == id }
                    service?.let { addToCart(it) }
                } else {
                    Toast.makeText(requireContext(), "Для добавления в корзину требуется авторизация", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun addToCart(service: ServiceItem) {
        val cartItem = CartItem(
            id = service.id,
            name = service.name,
            description = service.description,
            price = service.price,
            category = "service"
        )

        cartManager.addItem(cartItem)
        Toast.makeText(requireContext(), "${service.name} добавлено в корзину", Toast.LENGTH_SHORT).show()
    }

    data class ServiceItem(
        val id: String,
        val name: String,
        val description: String,
        val price: Double
    )
}