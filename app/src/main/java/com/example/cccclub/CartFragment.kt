package com.example.cccclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartFragment : Fragment() {

    private lateinit var cartManager: CartManager
    private lateinit var orderManager: OrderManager
    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvCartEmpty: TextView
    private lateinit var tvCartTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var btnClearCart: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartManager = CartManager(requireContext())
        orderManager = OrderManager(requireContext())

        rvCartItems = view.findViewById(R.id.rvCartItems)
        tvCartEmpty = view.findViewById(R.id.tvCartEmpty)
        tvCartTotal = view.findViewById(R.id.tvCartTotal)
        btnCheckout = view.findViewById(R.id.btnCheckout)
        btnClearCart = view.findViewById(R.id.btnClearCart)

        setupCart()

        btnCheckout.setOnClickListener {
            val cartItems = cartManager.getCartItems()
            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Корзина пуста", Toast.LENGTH_SHORT).show()
            } else {
                val total = cartManager.getCartTotal()

                // ✅ ВАЖНО: Создаем заказ через OrderManager
                orderManager.createOrder(
                    items = cartItems,
                    total = total,
                    placeType = PlaceType.COMPUTER, // или любой другой тип по умолчанию
                    placeNumber = "Автоматически", // или пустая строка
                    duration = 1 // 1 час по умолчанию
                )

                Toast.makeText(
                    requireContext(),
                    "✅ Заказ оформлен! Сумма: ${String.format("%.2f", total)} руб",
                    Toast.LENGTH_LONG
                ).show()

                // Очищаем корзину после создания заказа
                cartManager.clearCart()
                setupCart()
            }
        }

        btnClearCart.setOnClickListener {
            cartManager.clearCart()
            setupCart()
            Toast.makeText(requireContext(), "Корзина очищена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCart() {
        val cartItems = cartManager.getCartItems()

        if (cartItems.isEmpty()) {
            tvCartEmpty.visibility = View.VISIBLE
            rvCartItems.visibility = View.GONE
            btnCheckout.isEnabled = false
            btnClearCart.isEnabled = false
        } else {
            tvCartEmpty.visibility = View.GONE
            rvCartItems.visibility = View.VISIBLE
            btnCheckout.isEnabled = true
            btnClearCart.isEnabled = true

            rvCartItems.layoutManager = LinearLayoutManager(requireContext())
            rvCartItems.adapter = CartAdapter(
                cartItems,
                onQuantityChange = { itemId, newQuantity ->
                    cartManager.updateQuantity(itemId, newQuantity)
                    setupCart() // Обновляем весь список
                },
                onRemoveClick = { itemId ->
                    cartManager.removeItem(itemId)
                    setupCart()
                    Toast.makeText(requireContext(), "Товар удален из корзины", Toast.LENGTH_SHORT).show()
                }
            )
        }

        tvCartTotal.text = "Итого: ${String.format("%.2f", cartManager.getCartTotal())} руб"
    }

    override fun onResume() {
        super.onResume()
        // Обновляем корзину при возвращении на фрагмент
        setupCart()
    }
}