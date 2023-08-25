package com.sharma.notesapp.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.sharma.notesapp.data.local.SharedPreferenceHelper
import com.sharma.notesapp.data.repository.DefaultLoginRepository
import com.sharma.notesapp.data.repository.DefaultRemoteRepository
import com.sharma.notesapp.domain.repository.LoginRepository
import com.sharma.notesapp.domain.repository.RemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth,
        sharedPreferenceHelper: SharedPreferenceHelper
    ): LoginRepository {
        return DefaultLoginRepository(firebaseAuth, sharedPreferenceHelper)
    }

    @Provides
    fun provideRemoteRepository(
        firebaseFireStore: FirebaseFirestore,
        gson: Gson
    ): RemoteRepository {
        return DefaultRemoteRepository(
            firebaseFireStore, gson
        )
    }
}