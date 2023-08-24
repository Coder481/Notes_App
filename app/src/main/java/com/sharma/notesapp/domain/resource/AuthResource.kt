package com.sharma.notesapp.domain.resource

sealed class AuthResource<out T> {
    object Loading : AuthResource<Nothing>()
    data class CodeSent<out T>(val code: String): AuthResource<T>()
    data class Success<out T>(val data: T) : AuthResource<T>()
    data class Failure<out T>(val errorMessage: String) : AuthResource<T>()
}
