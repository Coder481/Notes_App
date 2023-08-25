package com.sharma.notesapp.data.repository

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sharma.notesapp.data.local.SharedPreferenceHelper
import com.sharma.notesapp.domain.model.FirebaseAuthData
import com.sharma.notesapp.domain.repository.LoginRepository
import com.sharma.notesapp.domain.resource.AuthResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DefaultLoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val sharedPreferenceHelper: SharedPreferenceHelper
): LoginRepository {

    private val TAG = "LoginRepository"

    private val ioDispatcher = Dispatchers.IO
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun sendOtp(phoneNumber: String, phoneAuthOptions: PhoneAuthOptions.Builder): Flow<AuthResource<FirebaseAuthData?>> = callbackFlow<AuthResource<FirebaseAuthData?>> {
        firebaseAuth.setLanguageCode("en")

        trySend(AuthResource.Loading)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")

                // auto fill OTP
                val otp = credential.smsCode
                otp?.let {
                    trySend(AuthResource.CodeSent(otp))
                }

                trySend(AuthResource.Loading)
                // sign user in and emit success resource
                runBlocking {
                    trySend(signInWithPhoneAuthCredential(credential, phoneNumber).first())
                    close()
                }

            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                var message = ""
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid credentials
                    message = "Invalid credentials entered!\nPlease try again with correct data"
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    message = "Too many requests at this time!\nPlease try again after sometime."
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                    message = "Unknown error!\nPlease try again after sometime."
                }

                trySend(AuthResource.Failure(message))
                close()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                trySend(AuthResource.Success(FirebaseAuthData(verificationId, token)))
            }
        }

        // phoneAuthOptions -> PhoneAuthOptions with activity included used in reCAPTCHA
        val options = phoneAuthOptions
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {  }
    }.flowOn(ioDispatcher)

    override fun verifyPhoneNumber(code: String, phoneNumber: String, firebaseAuthData: FirebaseAuthData)
    : Flow<AuthResource<FirebaseAuthData?>> = callbackFlow<AuthResource<FirebaseAuthData?>> {
        trySend(AuthResource.Loading)
        val credential = PhoneAuthProvider.getCredential(firebaseAuthData.verificationId, code)
        trySend(signInWithPhoneAuthCredential(credential, phoneNumber).first())
        awaitClose {  }
    }.flowOn(ioDispatcher)

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, phoneNumber: String)
    : Flow<AuthResource<FirebaseAuthData?>> {
        return callbackFlow<AuthResource<FirebaseAuthData?>> {

            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        Log.d(TAG, "signInWithCredential:success")

                        val user = task.result?.user
                        sharedPreferenceHelper.savePhoneNumber(phoneNumber)
                        trySend(AuthResource.Success(null))
                        close()
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            trySend(AuthResource.Failure("Invalid code entered!"))
                        }
                        else {
                            trySend(
                                AuthResource.Failure(task.exception?.message ?: "Unknown error")
                            )
                        }
                        close()
                    }
                }

            awaitClose { }
        }.flowOn(ioDispatcher)
    }
}