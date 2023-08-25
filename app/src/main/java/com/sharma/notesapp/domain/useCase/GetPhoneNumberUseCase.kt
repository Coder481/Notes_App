package com.sharma.notesapp.domain.useCase

import com.sharma.notesapp.domain.repository.LocalRepository
import javax.inject.Inject

class GetPhoneNumberUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(): String? {
        return localRepository.getPhoneNumber()
    }
}