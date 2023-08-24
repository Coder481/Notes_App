package com.sharma.notesapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharma.notesapp.data.model.FirebaseAuthData
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

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun sendOtp(phoneNumber: String) {
        viewModelScope.launch {
            _uiState.update { AuthUiState.Loading }
            sendOtpUseCase(phoneNumber).collect { authRes ->
                _uiState.update { authRes.toUiState() }
            }
        }
    }

    fun verifyOtp(code: String, firebaseAuthData: FirebaseAuthData) {
        viewModelScope.launch {
            _uiState.update { AuthUiState.Loading }
            verifyPhoneNumberUseCase(code, firebaseAuthData).collect { authRes ->
                _uiState.update { authRes.toUiState() }
            }
        }
    }
}