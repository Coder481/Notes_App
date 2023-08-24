package com.sharma.notesapp.presentation.mapper

import com.sharma.notesapp.domain.model.Note
import com.sharma.notesapp.domain.resource.Resource

sealed class NoteUiState {
    object Idle: NoteUiState()
    object Loading: NoteUiState()
    data class Success(val data: List<Note>): NoteUiState()
    data class Failure(val error: String): NoteUiState()
}

fun Resource<List<Note>>.toUiState(): NoteUiState {
    return when(this) {
        is Resource.Loading -> NoteUiState.Loading
        is Resource.Success -> NoteUiState.Success(this.data)
        is Resource.Failure -> NoteUiState.Failure(this.errorMessage)
    }
}
