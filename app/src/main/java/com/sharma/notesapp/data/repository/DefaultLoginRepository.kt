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
import com.sharma.notesapp.data.model.FirebaseAuthData
import com.sharma.notesapp.domain.Resource
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
    private val firebaseAuth: FirebaseAuth
): LoginRepository {

    private val TAG = "LoginRepository"

    private val ioDispatcher = Dispatchers.IO
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun sendOtp(phoneNumber: String): Flow<AuthResource<FirebaseAuthData?>> = callbackFlow<AuthResource<FirebaseAuthData?>> {
        firebaseAuth.setLanguageCode("en")

        trySend(AuthResource.Loading)
//        emit(Resource.Loading)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")

                // auto fill OTP
                val otp = credential.smsCode
                otp?.let {
                    trySend(AuthResource.CodeSent(otp))
                }

                trySend(AuthResource.Loading)
                // sign user in and emit success resource
                runBlocking {
                    trySend(signInWithPhoneAuthCredential(credential).first())
                    close()
                }

            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }

                trySend(AuthResource.Failure(e.message ?: ""))
                close()
                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token
                trySend(AuthResource.Success(FirebaseAuthData(verificationId, token)))
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {  }
    }.flowOn(ioDispatcher)

    override fun verifyPhoneNumber(code: String, firebaseAuthData: FirebaseAuthData)
    : Flow<AuthResource<FirebaseAuthData?>> = callbackFlow<AuthResource<FirebaseAuthData?>> {
        trySend(AuthResource.Loading)
        val credential = PhoneAuthProvider.getCredential(firebaseAuthData.verificationId, code)
        trySend(signInWithPhoneAuthCredential(credential).first())
        awaitClose {  }
    }.flowOn(ioDispatcher)

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential)
    : Flow<AuthResource<FirebaseAuthData?>> {
        return callbackFlow<AuthResource<FirebaseAuthData?>> {

            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")

                        val user = task.result?.user
                        trySend(AuthResource.Success(null))
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            trySend(AuthResource.Failure("Invalid code entered!"))
                        }
                        // Update UI
                        else {
                            trySend(
                                AuthResource.Failure(task.exception?.message ?: "Unknown error")
                            )
                        }
                    }
                }

            awaitClose { }
        }.flowOn(ioDispatcher)
    }
}