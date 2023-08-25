package com.sharma.notesapp.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.sharma.notesapp.R
import com.sharma.notesapp.domain.model.FirebaseAuthData
import com.sharma.notesapp.databinding.FragmentAuthBinding
import com.sharma.notesapp.presentation.MainActivity
import com.sharma.notesapp.presentation.helper.collectLifeCycleAware
import com.sharma.notesapp.presentation.helper.gone
import com.sharma.notesapp.presentation.helper.showToast
import com.sharma.notesapp.presentation.helper.visible
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
                showHideLoader(true)
            }
            if (uiState is AuthUiState.CodeSent) {
                // auto fill the otp view
                val code = uiState.code
                binding.editTextOTP.setText(code)
            }
            if (uiState is AuthUiState.Success) {
                uiState.data?.let {
                    // if received with non-null data means
                    // otp send to phone number
                    // ask user to enter the otp and hit verify button
                    showHideLoader(false)
                    addClickOfVerifyButton(it)
                } ?: kotlin.run {
                    // if received with null data means
                    // authentication complete and
                    // move to home page
                    moveToHomePage()
                }
            }
            if (uiState is AuthUiState.Failure) {
                showHideLoader(false)
                binding.loadingText.text = uiState.error
            }
        }
    }

    private fun showHideLoader(show: Boolean) {
        if (show) binding.loader.visible()
        else binding.loader.gone()
    }

    private fun initialLayout() {
        binding.apply {
            addClickOfSendOtpButton()
            editTextOTP.hint = getString(R.string.enter_otp)
            editTextPhoneNumber.hint = getString(R.string.enter_phone_number)
            editTextOTP.addTextChangedListener { disableVerifyButton(false) }
            editTextPhoneNumber.addTextChangedListener { disableVerifyButton(false) }
            showHideLoader(false)
        }
    }

    private fun addClickOfSendOtpButton() {
        binding.apply {
            // disable OTP view until OTP sent
            disableOtpField(true)
            loadingText.text = getString(R.string.enter_phone_number_with_country_code_to_verify)

            buttonVerify.text = getString(R.string.send_otp)
            disableVerifyButton(false)
            buttonVerify.setOnClickListener {
                val phoneNumber = editTextPhoneNumber.text.toString()
                if (phoneNumber.length < 10) {
                    showToast(getString(R.string.enter_correct_phone_number))
                    return@setOnClickListener
                }
                disableVerifyButton(true)
                viewModel.sendOtp(phoneNumber, getPhoneAuthOptions())
                loadingText.text = getString(R.string.sending_otp_please_wait)
            }
        }
    }

    private fun getPhoneAuthOptions(): PhoneAuthOptions.Builder {
        return PhoneAuthOptions
            .Builder(firebaseAuth)
            .setActivity(requireActivity())
    }

    private fun addClickOfVerifyButton(firebaseAuthData: FirebaseAuthData) {
        binding.apply {
            // now enable OTP view
            disableOtpField(false)
            val starredNumber = "\n${getStarredNumber(viewModel.numberWithoutSpaces)}"
            loadingText.text = getString(R.string.enter_otp_sent_to, starredNumber)

            buttonVerify.text = getString(R.string.verify)
            disableVerifyButton(false)
            buttonVerify.setOnClickListener {
                val code = editTextOTP.text.toString()
                if (code.isEmpty()) {
                    showToast(getString(R.string.enter_valid_code))
                    return@setOnClickListener
                }
                disableVerifyButton(true)
                viewModel.verifyOtp(code, firebaseAuthData)
                loadingText.text = getString(R.string.verifying_otp_please_wait)
            }
        }
    }

    private fun disableOtpField(disable: Boolean) {
        binding.apply {
            if (disable) {
                editTextOTP.isEnabled = false
                editTextPhoneNumber.isEnabled = true
            } else {
                editTextOTP.isEnabled = true
                editTextPhoneNumber.isEnabled = false
            }
        }
    }

    private fun disableVerifyButton(disable: Boolean) {
        binding.apply {
            buttonVerify.isEnabled = !disable
        }
    }

    private fun getStarredNumber(number: String): String {
        val phoneNumber = viewModel.removeWhitespace(number)
        val length = phoneNumber.length
        val stars = "*".repeat(length - 6)
        return "${phoneNumber.substring(0, 3)}$stars${
            phoneNumber.substring(
                (3 + stars.length),
                length
            )
        }"
    }
}