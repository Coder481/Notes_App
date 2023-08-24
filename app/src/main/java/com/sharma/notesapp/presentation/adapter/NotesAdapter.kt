package com.sharma.notesapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sharma.notesapp.databinding.ViewNoteItemBinding
import com.sharma.notesapp.domain.model.Note
import com.sharma.notesapp.presentation.helper.DateHelper
import com.sharma.notesapp.presentation.helper.setDueDateTextWithColor

class NotesAdapter: RecyclerView.Adapter<NotesAdapter.ItemViewHolder>() {

    private var items: List<Note>? = null
    private var clickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val b = ViewNoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(b)
    }

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        items?.let { holder.bind(it[position], clickListener) }
    }

    fun populateData(items: List<Note>, onItemClickListener: OnItemClickListener) {
        this.items = items
        this.clickListener = onItemClickListener
        notifyDataSetChanged()
    }

    class ItemViewHolder(
        private val binding: ViewNoteItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, clickListener: OnItemClickListener?) {
            binding.apply {
                tvTitle.text = note.title
                tvDescription.text = note.content
                tvDate.setDueDateTextWithColor(DateHelper.timeStampToDate(note.dateOfCreation))
                parentCard.setOnClickListener { clickListener?.onClick(note.id) }
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(itemId: String)
    }
}