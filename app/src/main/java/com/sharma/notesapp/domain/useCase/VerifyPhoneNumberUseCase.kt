package com.sharma.notesapp.domain.useCase

import com.sharma.notesapp.data.model.FirebaseAuthData
import com.sharma.notesapp.domain.repository.LoginRepository
import com.sharma.notesapp.domain.resource.AuthResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyPhoneNumberUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(code: String, authData: FirebaseAuthData)
    : Flow<AuthResource<FirebaseAuthData?>> {
        return loginRepository.verifyPhoneNumber(code, authData)
    }
}