package com.sharma.notesapp.domain.model

import com.google.gson.annotations.SerializedName

data class Note(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("date_of_creation") val dateOfCreation: Long
)
