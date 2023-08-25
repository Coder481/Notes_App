package com.sharma.notesapp.domain.useCase

import com.google.firebase.auth.PhoneAuthOptions
import com.sharma.notesapp.domain.model.FirebaseAuthData
import com.sharma.notesapp.domain.repository.LoginRepository
import com.sharma.notesapp.domain.resource.AuthResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    operator fun invoke(phoneNumber: String, phoneAuthOptions: PhoneAuthOptions.Builder): Flow<AuthResource<FirebaseAuthData?>> {
        return loginRepository.sendOtp(phoneNumber, phoneAuthOptions)
    }
}