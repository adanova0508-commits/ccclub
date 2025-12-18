# CCC Club - Android Приложение

## 📋 Обзор проекта
Android приложение для развлекательного клуба, которое позволяет:
- Бронировать различные типы мест (игровые компьютеры, PS5, VR и др.)
- Заказывать еду и напитки
- Просматривать историю заказов
- Управлять профилем пользователя

## 👥 Роли пользователей
1. **Клиент** - Обычный пользователь, может бронировать места и делать заказы
2. **Администратор** - Имеет доступ к админ-панели и статистике

## 🏗️ Структура проекта

### Основные компоненты

**Активности:**
- `AuthActivity.kt` - Экран аутентификации с вкладками входа/регистрации
- `MainActivity.kt` - Главный экран с нижней навигацией

**Фрагменты:**
- `ServicesFragment.kt` - Выбор услуг и бронирование мест
- `FoodFragment.kt` - Меню еды и напитков
- `CartFragment.kt` - Управление корзиной покупок
- `ProfileFragment.kt` - Профиль пользователя (для клиентов)
- `AdminFragment.kt` - Админ-панель (для администраторов)
- `LoginFragment.kt` - Форма входа
- `RegisterFragment.kt` - Форма регистрации

**Адаптеры:**
- `AuthPagerAdapter.kt` - Адаптер для ViewPager2 аутентификации
- `CartAdapter.kt` - Адаптер для элементов корзины в RecyclerView
- `FoodAdapter.kt` - Адаптер для элементов меню еды
- `OrderAdapter.kt` - Адаптер для истории заказов

**Модели данных:**
- `Order.kt` - Класс данных заказа с перечислениями:
  - `OrderStatus`: COMPLETED (завершен), CANCELLED (отменен), IN_PROGRESS (в процессе)
  - `PlaceType`: COMPUTER (компьютер), PS5, TABLE (стол), VR, ESPORTS (киберспорт), KARAOKE (караоке), CINEMA (кино), CONFERENCE (конференц-зал)
- `CartItem.kt` - Элемент корзины с полями: id, name, description, price, category, quantity

**Менеджеры:**
- `CartManager.kt` - Управление операциями корзины (добавление, удаление, обновление, расчет суммы)
- `OrderManager.kt` - Обработка создания заказов, обновления статусов и истории
- `SharedPreferencesHelper.kt` - Управление аутентификацией пользователей, регистрацией и локальным хранилищем

## 🎨 Макеты интерфейса

**Основные экраны:**
- `activity_main.xml` - Главный макет с контейнером фрагментов и нижней навигацией
- `activity_auth.xml` - Макет аутентификации с ViewPager2

**Макеты фрагментов:**
- `fragment_food.xml` - Меню еды с RecyclerView
- `fragment_cart.xml` - Экран корзины
- `fragment_profile.xml` - Экран профиля пользователя
- `fragment_admin.xml` - Макет админ-панели

**Макеты элементов:**
- `item_cart.xml` - Карточка элемента корзины с контролами количества и отображением цены
- `item_food.xml` - Макет элемента еды
- `item_order.xml` - Элемент истории заказов

## ⚙️ Зависимости (build.gradle.kts)

**Основные библиотеки:**
```kotlin
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("com.google.android.material:material:1.11.0")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
implementation("androidx.cardview:cardview:1.0.0")
implementation("androidx.viewpager2:viewpager2:1.0.0")
implementation("androidx.fragment:fragment-ktx:1.6.2")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
implementation("com.google.code.gson:gson:2.10.1")
implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
```

## 🔑 Система аутентификации

**Хранение пользователей:**
- Использует SharedPreferences с сериализацией Gson
- Сохраняет: имя, email, пароль, роль, телефон, предпочитаемое место

**Аккаунт администратора:**
- Админ по умолчанию: `admin@club.com` / `admin123`
- Автоматически создается при первом запуске приложения
- Переключение интерфейса в зависимости от роли в MainActivity

**Возможности:**
- Регистрация пользователей с проверкой уникальности email
- Валидация входа
- Сохранение сессии
- Определение роли (админ/клиент)

## 🛒 Система корзины

**Возможности CartManager:**
- Добавление/удаление товаров
- Обновление количества
- Расчет общей суммы
- Постоянное хранение через SharedPreferences

**Категории товаров:**
- "food" - Еда и напитки
- "service" - Бронирование мест

## 📦 Система заказов

**Возможности OrderManager:**
- Создание новых заказов с деталями места
- Отслеживание статуса заказа
- Просмотр истории заказов
- Обновление статуса (для администраторов)

**Заказ включает:**
- Список товаров
- Общую сумму
- Статус (в процессе/завершен/отменен)
- Тип и номер места
- Продолжительность в часах
- Дату и время

## 🎯 Навигация

1. **AuthActivity** → Вход/Регистрация
2. **MainActivity** → Нижняя навигация:
   - Услуги (бронирование мест)
   - Еда (меню еды)
   - Корзина (покупки)
   - Профиль/Админ (в зависимости от роли пользователя)

## 📊 Тестирование

**Структура тестов:**
- `ExampleUnitTest.kt` - Юнит-тесты
- `ExampleInstrumentedTest.kt` - Инструментальные тесты

## 🔧 Конфигурация сборки

**Настройки Android:**
- namespace: `com.example.cccclub`
- minSdk: 21
- targetSdk: 34
- compileSdk: 34
- viewBinding: включен

## 🌐 Цветовая схема
Цвета определены в `colors.xml`:
- `primary_brown` - Основной цвет бренда
- `accent_gold` - Акцентный цвет
- `light_brown` - Вторичный цвет
- `background` - Цвет фона
- `text_primary` / `text_secondary` - Цвета текста

## 🚀 Быстрый старт

1. Склонируйте проект
2. Откройте в Android Studio
3. Синхронизируйте Gradle
4. Запустите на эмуляторе/устройстве
5. Используйте данные администратора: `admin@club.com` / `admin123`

## 📱 Поддерживаемые типы мест
1. COMPUTER - Игровые компьютеры
2. PS5 - Станции PlayStation 5
3. TABLE - Столы для настольных игр
4. VR - Станции виртуальной реальности
5. ESPORTS - Киберспортивные арены
6. KARAOKE - Караоке-комнаты
7. CINEMA - Кинозалы
8. CONFERENCE - Конференц-залы
