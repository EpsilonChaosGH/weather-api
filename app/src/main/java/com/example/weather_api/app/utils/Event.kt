package com.example.weather_api.app.utils

import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class Event<T>(
    value: T
) {

    private var _value: T? = value

    fun get(): T? = _value.also { _value = null }
}

suspend fun <T> Flow<Event<T>>.observeEventFlow(lifecycleOwner: Lifecycle, listener: (T) -> Unit) {
    this.flowWithLifecycle(lifecycleOwner, Lifecycle.State.STARTED)
        .distinctUntilChanged()
        .collect { event ->
            event.get()?.let { value ->
                listener(value)
            }
        }
}

suspend fun <T> Flow<T>.observeFlow(lifecycleOwner: Lifecycle, listener: (T) -> Unit) {
    this.flowWithLifecycle(lifecycleOwner, Lifecycle.State.STARTED)
        .distinctUntilChanged()
        .collect { event ->
            event?.let { value ->
                listener(value)
            }
        }
}