package com.sharma.notesapp.domain.resource

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure<out T>(val errorMessage: String) : Resource<T>()
}
