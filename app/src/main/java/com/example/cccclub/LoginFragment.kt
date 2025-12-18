package com.example.cccclub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class LoginFragment : Fragment() {

    private lateinit var prefs: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SharedPreferencesHelper(requireContext())

        // Для отладки
        println("DEBUG: LoginFragment created")
        prefs.printDebugInfo()

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnLoginAsAdmin = view.findViewById<Button>(R.id.btnLoginAsAdmin)

        // Настройка кнопки входа как администратор
        btnLoginAsAdmin.setOnClickListener {
            println("DEBUG: Admin login button clicked")
            loginAsAdmin()
        }

        // Настройка обычной кнопки входа
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            println("DEBUG: Login attempt for email: $email")

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(requireContext(), "Введите корректный email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка существования пользователя
            if (!prefs.userExists(email)) {
                Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Попытка входа
            if (prefs.loginUser(email, password)) {
                Toast.makeText(requireContext(), "Вход выполнен успешно", Toast.LENGTH_SHORT).show()
                println("DEBUG: Login successful, going to MainActivity")

                // Для отладки - показываем текущего пользователя
                prefs.printDebugInfo()

                // ЗАПУСКАЕМ MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(requireContext(), "Неверный пароль", Toast.LENGTH_SHORT).show()
                println("DEBUG: Login failed - wrong password")
            }
        }
    }

    private fun loginAsAdmin() {
        val adminEmail = "admin@club.com"
        val adminPassword = "admin123"

        println("DEBUG: Admin login attempt for $adminEmail")

        // Пробуем войти как админ
        if (prefs.loginUser(adminEmail, adminPassword)) {
            Toast.makeText(requireContext(), "Вход как администратор выполнен", Toast.LENGTH_SHORT).show()
            println("DEBUG: Admin login successful")

            // Для отладки
            prefs.printDebugInfo()

            // ЗАПУСКАЕМ MainActivity
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } else {
            Toast.makeText(requireContext(), "Ошибка входа как администратор", Toast.LENGTH_LONG).show()
            println("DEBUG: Admin login failed")

            // Если не получилось, создаем админа и снова пытаемся войти
            println("DEBUG: Creating admin user and retrying login")
            prefs.createAdminUser()

            if (prefs.loginUser(adminEmail, adminPassword)) {
                Toast.makeText(requireContext(), "Админ создан и вход выполнен", Toast.LENGTH_SHORT).show()

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(requireContext(), "Не удалось создать администратора", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}