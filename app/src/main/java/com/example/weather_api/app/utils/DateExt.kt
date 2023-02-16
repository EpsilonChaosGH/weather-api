package com.example.weather_api.app.utils

import java.text.SimpleDateFormat
import java.util.*

const val FORMAT_EEE_d_MMMM_HH_mm = "EEE, d MMMM HH:mm"
const val FORMAT_EEE_HH_mm = "EEE, HH:mm"

fun String.toTime(pattern: String) =
    SimpleDateFormat(pattern, Locale.getDefault()).parse(this)?.time ?: 0L

fun Long.format(pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)