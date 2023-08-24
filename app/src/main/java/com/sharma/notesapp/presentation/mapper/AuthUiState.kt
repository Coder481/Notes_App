package com.sharma.notesapp.presentation.mapper

import com.sharma.notesapp.data.model.FirebaseAuthData
import com.sharma.notesapp.domain.resource.AuthResource

sealed class AuthUiState {
    object Idle: AuthUiState()
    object Loading: AuthUiState()
    data class CodeSent(val code: String): AuthUiState()
    data class Success(val data: FirebaseAuthData?): AuthUiState()
    data class Failure(val error: String): AuthUiState()
}

fun AuthResource<FirebaseAuthData?>.toUiState(): AuthUiState {
    return when (this) {
        is AuthResource.Loading -> AuthUiState.Loading
        is AuthResource.CodeSent -> AuthUiState.CodeSent(this.code)
        is AuthResource.Success -> AuthUiState.Success(this.data)
        is AuthResource.Failure -> AuthUiState.Failure(this.errorMessage)
    }
}
