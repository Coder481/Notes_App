package com.sharma.notesapp.data.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
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
    fun provideSharedPreferences(
        @ApplicationContext applicationContext: Context
    ): SharedPreferences {
        return applicationContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    fun provideGson(): Gson = Gson()
}