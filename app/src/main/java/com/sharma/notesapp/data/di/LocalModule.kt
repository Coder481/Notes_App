package com.sharma.notesapp.data.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.sharma.notesapp.data.local.SharedPreferenceHelper
import com.sharma.notesapp.data.repository.DefaultLocalRepository
import com.sharma.notesapp.domain.repository.LocalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    private const val SHARED_PREFERENCES_NAME = "PersistenceDataStorage"

    @Provides
    fun provideLocalRepository(
        sharedPreferenceHelper: SharedPreferenceHelper
    ): LocalRepository {
        return DefaultLocalRepository(sharedPreferenceHelper)
    }

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext applicationContext: Context
    ): SharedPreferences {
        return applicationContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    fun provideGson(): Gson = Gson()
}