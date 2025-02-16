package com.example.autokiosk.presentation.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> = _email

    fun loadUserEmail() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        _email.value = currentUser?.email ?: "Не авторизован"
    }
}
