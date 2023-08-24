package com.sharma.notesapp.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.sharma.notesapp.data.model.FirebaseAuthData
import com.sharma.notesapp.databinding.FragmentAuthBinding
import com.sharma.notesapp.presentation.MainActivity
import com.sharma.notesapp.presentation.helper.collectLifeCycleAware
import com.sharma.notesapp.presentation.helper.showToast
import com.sharma.notesapp.presentation.mapper.AuthUiState
import com.sharma.notesapp.presentation.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment: Fragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var binding: FragmentAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // user has already sign-in
        // move to the home screen
        if (firebaseAuth.currentUser != null) {
            moveToHomePage()
        }
    }

    private fun moveToHomePage() {
        activity?.let {
            if (it is MainActivity) it.moveToHome()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
    }

    private fun setLayout() {
        initialLayout()

        viewModel.uiState.collectLifeCycleAware(viewLifecycleOwner) { uiState ->
            if (uiState is AuthUiState.Loading) {
                // show loader
            }
            if (uiState is AuthUiState.CodeSent) {
                // auto fill the otp view
                val code = uiState.code
                binding.editTextOTP.setText(code)
                binding.buttonVerify.isEnabled = false
            }
            if (uiState is AuthUiState.Success) {
                // otp send to phone number
                // ask user to enter the otp and hit verify button
                uiState.data?.let {
                    addClickOfVerifyButton(it)
                } ?: kotlin.run {
                    // authentication complete
                    // move to home page
                    moveToHomePage()
                }
            }
        }
    }

    private fun initialLayout() {
        binding.apply {
            addClickOfSendOtpButton()
            editTextOTP.hint = "Enter OTP"
            editTextPhoneNumber.hint = "Enter Phone Number"
        }
    }

    private fun addClickOfSendOtpButton() {
        binding.apply {
            buttonVerify.text = "Send OTP"
            buttonVerify.setOnClickListener {
                val phoneNumber = editTextPhoneNumber.text.toString()
                if (phoneNumber.length < 10) {
                    showToast("Enter correct phone number")
                    return@setOnClickListener
                }
                viewModel.sendOtp(phoneNumber)
            }
        }
    }

    private fun addClickOfVerifyButton(firebaseAuthData: FirebaseAuthData) {
        binding.apply {
            buttonVerify.text = "Verify"
            buttonVerify.setOnClickListener {
                val code = editTextOTP.text.toString()
                if (code.isEmpty()) {
                    showToast("Enter valid code")
                    return@setOnClickListener
                }
                viewModel.verifyOtp(code, firebaseAuthData)
            }
        }
    }
}