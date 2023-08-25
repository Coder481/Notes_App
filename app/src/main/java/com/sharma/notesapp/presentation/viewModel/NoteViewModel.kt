package com.sharma.notesapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharma.notesapp.domain.model.Note
import com.sharma.notesapp.domain.resource.Resource
import com.sharma.notesapp.domain.useCase.AddNoteUseCase
import com.sharma.notesapp.domain.useCase.DeleteNoteUseCase
import com.sharma.notesapp.domain.useCase.GetAllNotesUseCase
import com.sharma.notesapp.domain.useCase.GetPhoneNumberUseCase
import com.sharma.notesapp.domain.useCase.UpdateNoteUseCase
import com.sharma.notesapp.presentation.mapper.NoteUiState
import com.sharma.notesapp.presentation.mapper.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getPhoneNumberUseCase: GetPhoneNumberUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<NoteUiState>(NoteUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val listOfNotes = mutableListOf<Note>()

    fun getAllNotes() {
        viewModelScope.launch {
            val phoneNumber = getPhoneNumberUseCase() ?: "0"
            getAllNotesUseCase(phoneNumber).collect{ res ->
                if (res is Resource.Success) listOfNotes.addAll(res.data)
                _uiState.update { res.toUiState() }
            }
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            val phoneNumber = getPhoneNumberUseCase() ?: "0"
            addNoteUseCase(note, phoneNumber).collect { res ->
                if (res is Resource.Success) {
                    listOfNotes.add(res.data)
                    _uiState.update { NoteUiState.Success(listOfNotes) }
                }
                if (res is Resource.Failure) {
                    _uiState.update { NoteUiState.Failure(res.errorMessage) }
                }
            }
        }
    }

    fun updateNote(note: Note, position: Int) {
        viewModelScope.launch {
            val phoneNumber = getPhoneNumberUseCase() ?: "0"
            updateNoteUseCase(note, phoneNumber).collect { res ->
                if (res is Resource.Success) {
                    listOfNotes.removeAt(position)
                    listOfNotes.add(position, note)
                    _uiState.update { NoteUiState.Success(listOfNotes) }
                }
                if (res is Resource.Failure) {
                    _uiState.update { NoteUiState.Failure(res.errorMessage) }
                }
            }
        }
    }

    fun deleteNote(note: Note, position: Int) {
        viewModelScope.launch {
            val phoneNumber = getPhoneNumberUseCase() ?: "0"
            deleteNoteUseCase(note, phoneNumber).collect { res ->
                if (res is Resource.Success) {
                    listOfNotes.removeAt(position)
                    _uiState.update { NoteUiState.Success(listOfNotes) }
                }
                if (res is Resource.Failure) {
                    _uiState.update { NoteUiState.Failure(res.errorMessage) }
                }
            }
        }
    }
}