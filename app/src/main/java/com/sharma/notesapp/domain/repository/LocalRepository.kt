package com.sharma.notesapp.domain.repository

interface LocalRepository {
    suspend fun getPhoneNumber(): String?
}