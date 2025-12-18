package com.example.cccclub

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var prefs: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = SharedPreferencesHelper(this)

        // ПРОВЕРКА: если пользователь не авторизован - выходим
        if (!prefs.isLoggedIn()) {
            // Возвращаемся на экран авторизации
            finish()
            return
        }

        bottomNav = findViewById(R.id.bottom_navigation)

        // Проверяем, является ли пользователь админом
        val isAdmin = prefs.getUserEmail() == "admin@club.com"

        if (isAdmin) {
            setupAdminNavigation()
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_services -> {
                    loadFragment(ServicesFragment())
                    true
                }
                R.id.nav_food -> {
                    loadFragment(FoodFragment())
                    true
                }
                R.id.nav_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.nav_profile -> {
                    // ЕСЛИ АДМИН - ПОКАЗЫВАЕМ АДМИНКУ, ИНАЧЕ ПРОФИЛЬ
                    if (isAdmin) {
                        loadFragment(AdminFragment())
                    } else {
                        loadFragment(ProfileFragment())
                    }
                    true
                }
                else -> false
            }
        }

        // Загружаем фрагмент по умолчанию
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_services
        }
    }

    private fun setupAdminNavigation() {
        val menu = bottomNav.menu
        val profileItem = menu.findItem(R.id.nav_profile)
        profileItem?.title = "Админ"
    }

    fun loadFragment(fragment: Fragment) { // УБРАЛ private
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}