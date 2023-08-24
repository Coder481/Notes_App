package com.sharma.notesapp.domain.useCase

import com.sharma.notesapp.domain.model.Note
import com.sharma.notesapp.domain.repository.RemoteRepository
import com.sharma.notesapp.domain.resource.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) {
    suspend operator fun invoke(note: Note): Flow<Resource<Note>> {
        return remoteRepository.deleteNote(note)
    }
}