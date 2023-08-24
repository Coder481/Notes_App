package com.sharma.notesapp.presentation.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {

    fun timeStampToDate(timeStamp: Long): String {
        val sdf = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date(timeStamp))
    }

    fun dateToTimeStamp(date: String): Long {
        val sdf = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault())
        val date = sdf.parse(date)
        return date?.time ?: 0L
    }

    private const val DEFAULT_DATE_FORMAT = "dd/MM/yyyy"
}