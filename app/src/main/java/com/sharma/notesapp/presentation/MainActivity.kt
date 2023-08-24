package com.sharma.notesapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.sharma.notesapp.R
import com.sharma.notesapp.databinding.ActivityMainBinding
import com.sharma.notesapp.presentation.fragment.AuthFragmentDirections
import com.sharma.notesapp.presentation.fragment.NotesDisplayFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun performFragmentTransaction(itemId: String) {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navHostFragment.navController
            .navigate(
                NotesDisplayFragmentDirections
                    .actionNotesDisplayFragmentToNoteDetailsFragment(itemId = itemId)
            )

    }

    fun popBack() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.popBackStack()
    }

    fun moveToHome() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        transactionToHome()
        navHostFragment.navController.popBackStack(R.id.authFragment, true)
    }

    private fun transactionToHome() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
            .navigate(R.id.notesDisplayFragment)
    }
}