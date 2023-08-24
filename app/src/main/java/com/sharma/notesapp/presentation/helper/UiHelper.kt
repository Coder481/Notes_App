package com.sharma.notesapp.presentation.helper

import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView

fun TextView.setDueDateTextWithColor(date: String) {
    val spannableStringBuilder = SpannableStringBuilder()

    val dueDateText = "Last updated: "
    val spannable = SpannableString(dueDateText)
    val dueDateHeaderColor = Color.parseColor("#9E9E9E")

    val length = dueDateText.length
    spannable.setSpan(ForegroundColorSpan(dueDateHeaderColor), 0, length, 0)

    spannableStringBuilder.append(spannable)
    spannableStringBuilder.append(date)

    text = spannableStringBuilder
}