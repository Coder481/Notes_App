package com.sharma.notesapp.data.repository

import com.sharma.notesapp.data.local.SharedPreferenceHelper
import com.sharma.notesapp.domain.repository.LocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultLocalRepository @Inject constructor(
    private val sharedPreferenceHelper: SharedPreferenceHelper
): LocalRepository {
    override suspend fun getPhoneNumber(): String? = withContext(Dispatchers.IO){
        sharedPreferenceHelper.getPhoneNumber()
    }
}