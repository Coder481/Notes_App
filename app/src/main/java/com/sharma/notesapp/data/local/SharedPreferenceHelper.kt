package com.sharma.notesapp.data.local

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferenceHelper @Inject constructor(
    private val sharedPreferences: SharedPreferences
){
    fun savePhoneNumber(phoneNumber: String) {
        sharedPreferences.edit().putString(PHONE_NUMBER_KEY, phoneNumber).apply()
    }
    fun getPhoneNumber(): String {
        return sharedPreferences.getString(PHONE_NUMBER_KEY, "") ?: ""
    }

    companion object {
        const val PHONE_NUMBER_KEY = "phone_number"
    }
}