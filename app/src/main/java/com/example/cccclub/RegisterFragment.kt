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

class RegisterFragment : Fragment() {

    private lateinit var prefs: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SharedPreferencesHelper(requireContext())

        println("DEBUG: RegisterFragment created")
        prefs.printDebugInfo()

        val etName = view.findViewById<EditText>(R.id.etName)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            println("DEBUG: Register attempt - name='$name', email='$email'")

            // Проверка заполненности полей
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка минимальной длины пароля
            if (password.length < 6) {
                Toast.makeText(requireContext(), "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка формата email
            if (!isValidEmail(email)) {
                Toast.makeText(requireContext(), "Введите корректный email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка, не зарегистрирован ли уже пользователь с таким email
            if (prefs.userExists(email)) {
                Toast.makeText(requireContext(), "Пользователь с таким email уже зарегистрирован", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Сохраняем пользователя
            prefs.saveUser(name, email, password)

            // Печатаем отладочную информацию
            println("DEBUG: After saving user:")
            prefs.printDebugInfo()

            if (prefs.isLoggedIn()) {
                Toast.makeText(requireContext(), "Регистрация успешна!", Toast.LENGTH_SHORT).show()

                // ЗАПУСКАЕМ MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(requireContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}