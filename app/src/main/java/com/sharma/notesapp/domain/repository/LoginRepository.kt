package com.sharma.notesapp.domain.repository

import com.google.firebase.auth.PhoneAuthOptions
import com.sharma.notesapp.domain.model.FirebaseAuthData
import com.sharma.notesapp.domain.resource.AuthResource
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun sendOtp(phoneNumber: String, phoneAuthOptions: PhoneAuthOptions.Builder): Flow<AuthResource<FirebaseAuthData?>>
    fun verifyPhoneNumber(code: String, phoneNumber: String, firebaseAuthData: FirebaseAuthData)
    : Flow<AuthResource<FirebaseAuthData?>>
}