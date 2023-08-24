package com.sharma.notesapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.sharma.notesapp.databinding.FragmentNoteDetailsBinding
import com.sharma.notesapp.domain.model.Note
import com.sharma.notesapp.presentation.helper.DateHelper
import com.sharma.notesapp.presentation.helper.collectLifeCycleAware
import com.sharma.notesapp.presentation.helper.gone
import com.sharma.notesapp.presentation.helper.popBack
import com.sharma.notesapp.presentation.helper.setDueDateTextWithColor
import com.sharma.notesapp.presentation.helper.showToast
import com.sharma.notesapp.presentation.helper.visible
import com.sharma.notesapp.presentation.mapper.NoteUiState
import com.sharma.notesapp.presentation.viewModel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsFragment: Fragment() {

    private var updateClicked: Boolean = false
    lateinit var binding: FragmentNoteDetailsBinding
    private val viewModel: NoteViewModel by activityViewModels()
    private val args: NoteDetailsFragmentArgs by navArgs()
    private var successMessage = "Note updated successfully"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()
        setLayout()
    }

    private fun setObserver() {
        viewModel.uiState.collectLifeCycleAware(viewLifecycleOwner) { uiState ->
            if (uiState is NoteUiState.Loading) {
                showHideLoader(true)
            }
            if (uiState is NoteUiState.Success) {
                if (updateClicked) {
                    showHideErrorLayout(false, null)
                    showToast(successMessage)
                    popBack()
                }
            }
            if (uiState is NoteUiState.Failure) {
                showHideErrorLayout(true, "${uiState.error}\n\nClick to retry...")
            }
        }
    }

    private fun setLayout() {
        if (args.itemId.isNotEmpty()) {
            populateScreen()
        } else {
            setHints()
        }
    }

    private fun populateScreen() {
        val list = viewModel.listOfNotes
        val item = list.find { it.id == args.itemId }
        item?.let {
            binding.apply {
                viewItemDetails.tvTitle.setText(it.title)
                viewItemDetails.descriptionField.setText(it.content)
                viewItemDetails.tvDueDate.setDueDateTextWithColor(
                    DateHelper.timeStampToDate(it.dateOfCreation)
                )
                addTitleListener(it)
            }
        }
    }

    private fun addTitleListener(note: Note) {
        binding.viewItemDetails.apply {
            tvTitle.addTextChangedListener {
                it?.let {
                    binding.btnUpdateItem.isEnabled = it.toString() != note.title
                }
            }
            descriptionField.addTextChangedListener {
                it?.let {
                    binding.btnUpdateItem.isEnabled = it.toString() != note.content
                }
            }
        }
        binding.apply {
            btnUpdateItem.text = "Update"
            btnUpdateItem.isEnabled = false
            btnDeleteItem.text = "Delete"
            btnUpdateItem.setOnClickListener {
                val position = viewModel.listOfNotes.indexOf(note)
                val newNote = note.copy(
                    title = binding.viewItemDetails.tvTitle.text.toString(),
                    content = binding.viewItemDetails.descriptionField.text.toString(),
                    dateOfCreation = System.currentTimeMillis()
                )
                if (position != -1) {
                    successMessage = "Note updated successfully"
                    updateClicked = true
                    viewModel.updateNote(newNote, position)
                }
            }
            btnDeleteItem.setOnClickListener {
                successMessage = "Note deleted successfully"
                updateClicked = true
                val position = viewModel.listOfNotes.indexOf(note)
                viewModel.deleteNote(note, position)
            }
        }
    }

    private fun setHints() {
        binding.apply {
            viewItemDetails.tvTitle.hint = "Input Title"
            viewItemDetails.descriptionField.hint = "Input Description"
            viewItemDetails.descriptionField.isFocusable = true
            val date = DateHelper.timeStampToDate(System.currentTimeMillis())
            viewItemDetails.tvDueDate.setDueDateTextWithColor(date)

            btnDeleteItem.gone()
            btnUpdateItem.text = "Add Note"
            btnUpdateItem.setOnClickListener {
                if (viewItemDetails.tvTitle.text.toString().isEmpty()) {
                    showToast("Please add title before proceeding")
                    return@setOnClickListener
                }

                val note = Note(
                    title = viewItemDetails.tvTitle.text.toString(),
                    content = viewItemDetails.descriptionField.text.toString(),
                    dateOfCreation = System.currentTimeMillis(),
                    id = ""
                )
                successMessage = "Note added successfully"
                updateClicked = true
                viewModel.addNote(note)
            }
        }
    }

    private fun showHideErrorLayout(show: Boolean, error: String?) {
        showHideLoader(false)
        binding.apply {
            if (show) {
                viewItemDetails.root.gone()
                noItemsLayout.visible()
                tvNoItems.text = error
                tvNoItems.setOnClickListener {
                    showHideErrorLayout(false, null)
                }
            } else {
                noItemsLayout.gone()
                viewItemDetails.root.visible()
            }
        }
    }

    private fun showHideLoader(show: Boolean) {
        binding.apply {
            if (show) {
                loader.visible()
                noItemsLayout.gone()
                viewItemDetails.root.gone()
            } else {
                loader.gone()
            }
        }
    }
}