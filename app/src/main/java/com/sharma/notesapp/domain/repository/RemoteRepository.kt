package com.sharma.notesapp.domain.repository

import com.sharma.notesapp.domain.model.Note
import com.sharma.notesapp.domain.resource.Resource
import kotlinx.coroutines.flow.Flow

interface RemoteRepository {
    fun getAllNotes(): Flow<Resource<List<Note>>>
    fun addNote(note: Note): Flow<Resource<Note>>
    fun updateNote(note: Note): Flow<Resource<Note>>
    fun deleteNote(note: Note): Flow<Resource<Note>>
}