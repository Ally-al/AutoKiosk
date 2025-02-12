package com.example.autokiosk.utils
object ValidationUtils {

    // Валидация email
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Валидация пароля
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}
