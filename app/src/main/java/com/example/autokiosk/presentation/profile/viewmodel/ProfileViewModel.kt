package com.example.autokiosk.presentation.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autokiosk.domain.usecase.CardUseCases
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val cardUseCases: CardUseCases
) : ViewModel() {
    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> = _email

    private val _hasLinkedCards = MutableStateFlow(false)
    val hasLinkedCards: StateFlow<Boolean> = _hasLinkedCards.asStateFlow()

    init {
        viewModelScope.launch {
            cardUseCases.getAllCards.execute().collect { cards ->
                val hasCards = cards.isNotEmpty()
                _hasLinkedCards.value = hasCards
            }
        }
    }

    fun loadUserEmail() {
        val currentUser = firebaseAuth.currentUser
        _email.value = currentUser?.email ?: "Не авторизован"
    }

    fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
