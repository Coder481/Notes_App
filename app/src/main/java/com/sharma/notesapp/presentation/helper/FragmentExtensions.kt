package com.sharma.notesapp.presentation.helper

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sharma.notesapp.presentation.MainActivity

fun Fragment.showToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}
fun Fragment.popBack() {
    activity?.let {
        if (it is MainActivity) {
            it.popBack()
        }
    }
}

fun Fragment.performTransaction(itemId: String) {
    activity?.let {
        if (it is MainActivity) {
            it.performFragmentTransaction(
                itemId = itemId
            )
        }
    }
}