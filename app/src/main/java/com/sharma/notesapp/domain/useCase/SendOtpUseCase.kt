package com.sharma.notesapp.domain.useCase

import com.sharma.notesapp.data.model.FirebaseAuthData
import com.sharma.notesapp.domain.repository.LoginRepository
import com.sharma.notesapp.domain.resource.AuthResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(phoneNumber: String): Flow<AuthResource<FirebaseAuthData?>> {
        return loginRepository.sendOtp(phoneNumber)
    }
}