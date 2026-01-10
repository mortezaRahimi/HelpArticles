package com.mortex.helparticles.presentation.ui.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun Instant.toDate(): String {
    val dateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val min = if (dateTime.minute < 10) "0${dateTime.minute}" else dateTime.minute
    return "${dateTime.date} - ${dateTime.hour}:${min}"
}

