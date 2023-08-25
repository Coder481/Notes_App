package com.sharma.notesapp.domain.model

import com.google.firebase.auth.PhoneAuthProvider

data class FirebaseAuthData(
    val verificationId: String,
    val token: PhoneAuthProvider.ForceResendingToken
)
