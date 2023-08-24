package com.sharma.notesapp.data.di

import com.google.firebase.auth.FirebaseAuth
import com.sharma.notesapp.data.repository.DefaultLoginRepository
import com.sharma.notesapp.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth
    ): LoginRepository {
        return DefaultLoginRepository(firebaseAuth)
    }
}