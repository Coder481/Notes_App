package com.sharma.notesapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sharma.notesapp.R
import com.sharma.notesapp.databinding.FragmentNotesDisplayBinding
import com.sharma.notesapp.domain.model.Note
import com.sharma.notesapp.presentation.MainActivity
import com.sharma.notesapp.presentation.adapter.NotesAdapter
import com.sharma.notesapp.presentation.helper.collectLifeCycleAware
import com.sharma.notesapp.presentation.helper.gone
import com.sharma.notesapp.presentation.helper.performTransaction
import com.sharma.notesapp.presentation.helper.visible
import com.sharma.notesapp.presentation.mapper.NoteUiState
import com.sharma.notesapp.presentation.viewModel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesDisplayFragment: Fragment() {

    private lateinit var adapter: NotesAdapter
    private val viewModel: NoteViewModel by activityViewModels()
    lateinit var binding: FragmentNotesDisplayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesDisplayBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
    }

    private fun setLayout() {
        viewModel.getAllNotes()
        viewModel.uiState.collectLifeCycleAware(viewLifecycleOwner) {
            if (it is NoteUiState.Loading) {
                showHideLoader(true)
            }
            if (it is NoteUiState.Success) {
                showHideErrorLayout(false, null)
                setAdapter(it.data)
            }
            if (it is NoteUiState.Failure) {
                showHideErrorLayout(true, it.error)
            }
        }
    }

    private fun setAdapter(data: List<Note>) {
        // add note button
        binding.addNoteLayout.setOnClickListener {
            performTransaction("")
        }

        if (data.isEmpty()) {
            showHideErrorLayout(true,
                requireContext().getString(R.string.no_todo_items_add_by_clicking_button))
            return
        }
        adapter = NotesAdapter()
        adapter.populateData(data, object : NotesAdapter.OnItemClickListener{
            override fun onClick(itemId: String) {
                // move to next fragment with the ID
                moveToDetailsFragment(itemId)
            }
        })
        binding.rvNotes.adapter = adapter
    }

    private fun moveToDetailsFragment(itemId: String) {
        activity?.let {
            if (it is MainActivity) {
                it.performFragmentTransaction(itemId)
            }
        }
    }

    private fun showHideErrorLayout(show: Boolean, error: String?) {
        showHideLoader(false)
        binding.apply {
            if (show) {
                rvNotes.gone()
                noItemsLayout.visible()
                tvNoItems.text = error
            } else {
                noItemsLayout.gone()
                rvNotes.visible()
            }
        }
    }

    private fun showHideLoader(show: Boolean) {
        binding.apply {
            if (show) {
                loader.visible()
                noItemsLayout.gone()
                rvNotes.gone()
            } else {
                loader.gone()
            }
        }
    }
}