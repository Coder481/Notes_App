package com.sharma.notesapp.domain.repository

import com.sharma.notesapp.domain.Resource
import com.sharma.notesapp.data.model.FirebaseAuthData
import com.sharma.notesapp.domain.resource.AuthResource
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun sendOtp(phoneNumber: String): Flow<AuthResource<FirebaseAuthData?>>
    fun verifyPhoneNumber(code: String, firebaseAuthData: FirebaseAuthData)
    : Flow<AuthResource<FirebaseAuthData?>>
}