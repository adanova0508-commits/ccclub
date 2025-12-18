package com.example.cccclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("DEBUG: AuthActivity onCreate")

        val prefs = SharedPreferencesHelper(this)

        // ПРОВЕРЯЕМ: если пользователь уже авторизован - идем в MainActivity
        val isLoggedIn = prefs.isLoggedIn()
        println("DEBUG: isLoggedIn = $isLoggedIn")

        if (isLoggedIn) {
            println("DEBUG: User already logged in, going to MainActivity")
            goToMainActivity()
            return
        }

        setContentView(R.layout.activity_auth)

        // Создаем тестового админа
        println("DEBUG: Creating admin user")
        prefs.createAdminUser()

        // Настраиваем кнопку "Войти как администратор"
        val btnLoginAsAdmin = findViewById<Button>(R.id.btnLoginAsAdmin)
        btnLoginAsAdmin.setOnClickListener {
            println("DEBUG: Login as admin button clicked")
            loginAsAdmin()
        }

        // Пробуем настроить ViewPager
        try {
            setupViewPager()
        } catch (e: Exception) {
            println("DEBUG: Error setting up ViewPager: ${e.message}")
            Toast.makeText(this, "Формы входа/регистрации временно недоступны", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginAsAdmin() {
        val prefs = SharedPreferencesHelper(this)
        println("DEBUG: Attempting admin login")

        val success = prefs.loginUser("admin@club.com", "admin123")
        println("DEBUG: loginUser returned $success")

        // ПРОВЕРЯЕМ еще раз после логина
        val isLoggedIn = prefs.isLoggedIn()
        println("DEBUG: After login, isLoggedIn = $isLoggedIn")

        if (success && isLoggedIn) {
            Toast.makeText(this, "Вход выполнен как администратор", Toast.LENGTH_SHORT).show()
            goToMainActivity()
        } else {
            Toast.makeText(this, "Ошибка входа. Убедитесь, что админ создан", Toast.LENGTH_LONG).show()
            println("DEBUG: Login failed - success=$success, isLoggedIn=$isLoggedIn")
        }
    }

    private fun goToMainActivity() {
        println("DEBUG: Starting MainActivity")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupViewPager() {
        // Код настройки ViewPager
        val viewPager = findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)

        val adapter = AuthPagerAdapter(this)
        viewPager.adapter = adapter

        com.google.android.material.tabs.TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Вход" else "Регистрация"
        }.attach()
    }
}