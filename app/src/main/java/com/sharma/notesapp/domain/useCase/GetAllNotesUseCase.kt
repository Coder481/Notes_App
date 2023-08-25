package com.sharma.notesapp.domain.useCase

import com.sharma.notesapp.domain.model.Note
import com.sharma.notesapp.domain.repository.RemoteRepository
import com.sharma.notesapp.domain.resource.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) {
    operator fun invoke(phoneNumber: kotlin.String): Flow<Resource<List<Note>>> {
        return remoteRepository.getAllNotes(phoneNumber)
    }
}