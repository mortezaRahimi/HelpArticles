package com.mortex.helparticles.presentation.ui.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun Instant.toDate(): String {
    val dateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.date} - ${dateTime.hour}:${dateTime.minute}"
}

