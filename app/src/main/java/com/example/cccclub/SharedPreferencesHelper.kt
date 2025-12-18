package com.example.cccclub

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val CURRENT_USER_KEY = "current_user_email"

    data class User(
        val name: String,
        val email: String,
        val password: String,
        val role: String = "client",
        val phone: String = "",
        val preferredPlace: String = ""
    )

    // Сохранение данных пользователя при регистрации
    fun saveUser(name: String, email: String, password: String) {
        println("DEBUG: Saving user: name=$name, email=$email")

        val user = User(name, email, password)
        val users = getAllUsers().toMutableList()

        // Проверяем, нет ли уже пользователя с таким email
        if (users.any { it.email == email }) {
            println("DEBUG: User with email $email already exists")
            return
        }

        users.add(user)
        saveAllUsers(users)

        // Устанавливаем текущего пользователя
        setCurrentUser(email)

        println("DEBUG: User saved successfully")
    }

    // Проверка логина
    fun loginUser(email: String, password: String): Boolean {
        println("DEBUG: loginUser called with email=$email")

        // СПЕЦИАЛЬНАЯ ОБРАБОТКА ДЛЯ АДМИНА
        if (email == "admin@club.com" && password == "admin123") {
            println("DEBUG: Admin login attempt")

            // Проверяем, существует ли админ
            val adminExists = getAllUsers().any { it.email == "admin@club.com" }

            if (!adminExists) {
                println("DEBUG: Creating admin user")
                createAdminUser()
            }

            // Устанавливаем текущего пользователя
            setCurrentUser("admin@club.com")
            println("DEBUG: Admin login successful")
            return true
        }

        // ДЛЯ ОБЫЧНЫХ ПОЛЬЗОВАТЕЛЕЙ
        val users = getAllUsers()
        val user = users.find { it.email == email && it.password == password }

        if (user != null) {
            println("DEBUG: Regular user login successful")
            setCurrentUser(email)
            return true
        } else {
            println("DEBUG: Login failed - user not found or password incorrect")
            return false
        }
    }

    // Проверка существования пользователя по email
    fun userExists(email: String): Boolean {
        val users = getAllUsers()
        val exists = users.any { it.email == email }
        println("DEBUG: userExists - checking email='$email', result=$exists")
        return exists
    }

    // Получение текущего пользователя
    fun getCurrentUser(): User? {
        val currentEmail = prefs.getString(CURRENT_USER_KEY, "")
        if (currentEmail.isNullOrEmpty()) return null

        val users = getAllUsers()
        return users.find { it.email == currentEmail }
    }

    // Получение текущего имени
    fun getUserName(): String {
        return getCurrentUser()?.name ?: "Пользователь"
    }

    // Получение текущего email
    fun getUserEmail(): String {
        return getCurrentUser()?.email ?: ""
    }

    // Получение текущего пароля
    fun getUserPassword(): String {
        return getCurrentUser()?.password ?: ""
    }

    // Обновление данных профиля текущего пользователя
    fun updateProfile(name: String, email: String, phone: String = "", preferredPlace: String = "") {
        val currentUser = getCurrentUser()
        if (currentUser != null) {
            val updatedUser = currentUser.copy(
                name = name,
                email = email,
                phone = phone,
                preferredPlace = preferredPlace
            )

            val users = getAllUsers().toMutableList()
            val index = users.indexOfFirst { it.email == currentUser.email }
            if (index != -1) {
                users[index] = updatedUser
                saveAllUsers(users)
                setCurrentUser(email) // Обновляем текущего пользователя
            }
        }
    }

    // Проверка авторизации
    fun isLoggedIn(): Boolean {
        return getCurrentUser() != null
    }

    // Выход
    fun logout() {
        prefs.edit().remove(CURRENT_USER_KEY).apply()
    }

    // Создание администратора
    fun createAdminUser() {
        val admin = User(
            name = "Администратор",
            email = "admin@club.com",
            password = "admin123",
            role = "admin",
            phone = "+79991112233"
        )

        val users = getAllUsers().toMutableList()
        if (users.none { it.email == "admin@club.com" }) {
            users.add(admin)
            saveAllUsers(users)
            println("DEBUG: Admin user created")
        }
    }

    // Проверка является ли текущий пользователь админом
    fun isAdmin(): Boolean {
        return getCurrentUser()?.role == "admin"
    }

    // Получение роли
    fun getProfileRole(): String {
        return getCurrentUser()?.role ?: "client"
    }

    // Получение телефона
    fun getProfilePhone(): String {
        return getCurrentUser()?.phone ?: ""
    }

    // Получение предпочитаемого места
    fun getProfilePreferredPlace(): String {
        return getCurrentUser()?.preferredPlace ?: ""
    }

    // Вспомогательные методы для работы с JSON
    private fun getAllUsers(): List<User> {
        val json = prefs.getString("users", "[]")
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveAllUsers(users: List<User>) {
        val json = gson.toJson(users)
        prefs.edit().putString("users", json).apply()
    }

    private fun setCurrentUser(email: String) {
        prefs.edit().putString(CURRENT_USER_KEY, email).apply()
        prefs.edit().putBoolean("is_logged_in", true).apply()
    }

    // Функция для отладки
    fun printDebugInfo() {
        println("DEBUG: === SHARED PREFERENCES ===")
        println("DEBUG: Current user: ${getCurrentUser()?.email}")
        println("DEBUG: is_logged_in: ${isLoggedIn()}")
        println("DEBUG: All users count: ${getAllUsers().size}")
        getAllUsers().forEachIndexed { index, user ->
            println("DEBUG: User $index: ${user.email} - ${user.name}")
        }
        println("DEBUG: === END ===")
    }
}