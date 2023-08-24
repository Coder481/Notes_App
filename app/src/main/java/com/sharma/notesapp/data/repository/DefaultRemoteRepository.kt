package com.sharma.notesapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.sharma.notesapp.data.local.SharedPreferenceHelper
import com.sharma.notesapp.domain.model.Note
import com.sharma.notesapp.domain.repository.RemoteRepository
import com.sharma.notesapp.domain.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DefaultRemoteRepository @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val gson: Gson
): RemoteRepository {

    private val ioDispatcher = Dispatchers.IO

    override fun getAllNotes(): Flow<Resource<List<Note>>> = callbackFlow<Resource<List<Note>>> {

        trySend(Resource.Loading)

        val list = mutableListOf<Note>()
        val collection = firebaseFireStore.collection(sharedPreferenceHelper.getPhoneNumber())
        collection.get()
            .addOnSuccessListener {
                it.documents.forEach { document ->
                    document.get("data")?.let { data ->
                        val json = data as String
                        val note = gson.fromJson(json, Note::class.java)
                        list.add(note)
                    }
                }
                trySend(Resource.Success(list))
            }
            .addOnFailureListener {
                trySend(Resource.Failure(it.message ?: "Unknown error"))
            }

        awaitClose {  }

    }.flowOn(ioDispatcher)

    override fun addNote(note: Note): Flow<Resource<Note>> = callbackFlow<Resource<Note>> {

        trySend(Resource.Loading)

        val collection = firebaseFireStore.collection(sharedPreferenceHelper.getPhoneNumber())
        val documentRef = collection.document()
        val newNote = note.copy(id = documentRef.id)
        val json = gson.toJson(newNote)
        val map = mutableMapOf<String, String>().also { it["data"] = json }
        documentRef.set(map)
            .addOnSuccessListener {
                trySend(Resource.Success(newNote))
            }
            .addOnFailureListener {
                trySend(Resource.Failure(it.message ?: "Unknown error"))
            }
        awaitClose {  }
    }.flowOn(ioDispatcher)

    override fun updateNote(note: Note): Flow<Resource<Note>> = callbackFlow<Resource<Note>> {

        trySend(Resource.Loading)

        val collection = firebaseFireStore.collection(sharedPreferenceHelper.getPhoneNumber())
        val document = collection.document(note.id)
        val json = gson.toJson(note)
        document.update("data", json)
            .addOnSuccessListener {
                trySend(Resource.Success(note))
            }
            .addOnFailureListener {
                trySend(Resource.Failure(it.message ?: "Unknown error"))
            }
        awaitClose {  }
    }.flowOn(ioDispatcher)

    override fun deleteNote(note: Note): Flow<Resource<Note>> = callbackFlow<Resource<Note>> {

        trySend(Resource.Loading)

        val collection = firebaseFireStore.collection(sharedPreferenceHelper.getPhoneNumber())
        val document = collection.document(note.id)
        document.delete()
            .addOnSuccessListener {
                trySend(Resource.Success(note))
            }
            .addOnFailureListener {
                trySend(Resource.Failure(it.message ?: "Unknown error"))
            }
        awaitClose {  }
    }.flowOn(ioDispatcher)
}