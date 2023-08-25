package com.sharma.notesapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthOptions
import com.sharma.notesapp.domain.model.FirebaseAuthData
import com.sharma.notesapp.domain.useCase.SendOtpUseCase
import com.sharma.notesapp.domain.useCase.VerifyPhoneNumberUseCase
import com.sharma.notesapp.presentation.mapper.AuthUiState
import com.sharma.notesapp.presentation.mapper.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyPhoneNumberUseCase: VerifyPhoneNumberUseCase
): ViewModel() {

    lateinit var numberWithoutSpaces: String
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun sendOtp(phoneNumber: String, phoneAuthOptions: PhoneAuthOptions.Builder) {
        viewModelScope.launch {
            numberWithoutSpaces = removeWhitespace(phoneNumber)
            _uiState.update { AuthUiState.Loading }
            sendOtpUseCase(numberWithoutSpaces, phoneAuthOptions).collect { authRes ->
                _uiState.update { authRes.toUiState() }
            }
        }
    }

    fun removeWhitespace(phoneNumber: String): String {
        return phoneNumber.trim().replace("\\s+".toRegex(), "")
    }

    fun verifyOtp(code: String, firebaseAuthData: FirebaseAuthData) {
        viewModelScope.launch {
            _uiState.update { AuthUiState.Loading }
            verifyPhoneNumberUseCase(code, numberWithoutSpaces, firebaseAuthData).collect { authRes ->
                _uiState.update { authRes.toUiState() }
            }
        }
    }
}