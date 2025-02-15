package com.example.autokiosk.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autokiosk.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.autokiosk.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    fun loginUser(email: String, password: String) {
        if (!ValidationUtils.isValidEmail(email) || !ValidationUtils.isValidPassword(password)) {
            _loginResult.value = false
            return
        }
        viewModelScope.launch {
            val result = loginUseCase.execute(email, password)
            _loginResult.value = result
        }
    }
}
