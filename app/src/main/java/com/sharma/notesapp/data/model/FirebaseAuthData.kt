package com.sharma.notesapp.data.model

import com.google.firebase.auth.PhoneAuthProvider

data class FirebaseAuthData(
    val verificationId: String,
    val token: PhoneAuthProvider.ForceResendingToken
)
