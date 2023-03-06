package com.example.weather_api.app.utils

import java.text.SimpleDateFormat
import java.util.*

const val FORMAT_EEE_d_MMMM_HH_mm = "EEE, d MMMM HH:mm"
const val FORMAT_EEE_HH_mm = "EEE, HH:mm"
const val FORMAT_HH_mm = "HH:mm"

fun String.toTime(pattern: String) =
    SimpleDateFormat(pattern, Locale.getDefault()).parse(this)?.time ?: 0L

fun Long.format(pattern: String, timeZone: Long): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this + timeZone)

fun Long.diffFormat(): String {
    return when (val diffTime = Calendar.getInstance().time.time - this) {
        in 0..3600000 -> "Last update: " +
                SimpleDateFormat("m", Locale.getDefault()).format(diffTime) +
                " minutes ago"

        in 0..86400000 -> "Last update: " +
                SimpleDateFormat("k", Locale.getDefault()).format(diffTime) +
                " hours ago"

        in 86400000..604800000 -> "Last update: " +
                SimpleDateFormat("d", Locale.getDefault()).format(diffTime) +
                " days ago"

        in 604800000..Long.MAX_VALUE -> "Last update: " +
                SimpleDateFormat(FORMAT_EEE_HH_mm, Locale.getDefault()).format(diffTime)
        else -> ""
    }
}
