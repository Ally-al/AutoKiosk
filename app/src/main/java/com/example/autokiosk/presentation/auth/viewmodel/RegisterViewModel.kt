package com.example.autokiosk.presentation.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autokiosk.domain.usecase.auth.RegisterUseCase
import com.example.autokiosk.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult

    fun registerUser(email: String, password: String, confirmPassword: String) {
        if (!ValidationUtils.isValidEmail(email) || !ValidationUtils.isValidPassword(password)
            || password != confirmPassword) {
            _registerResult.value = false
            return
        }
        viewModelScope.launch {
            val result = registerUseCase.execute(email, password)
            _registerResult.value = result
        }
    }
}
