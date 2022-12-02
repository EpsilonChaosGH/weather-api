package com.example.weather_api.app.screens.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_api.R
import com.example.weather_api.app.model.BackendException
import com.example.weather_api.app.model.CityNotFoundException
import com.example.weather_api.app.model.ConnectionException
import com.example.weather_api.app.model.InvalidApiKeyException
import com.example.weather_api.app.model.main.WeatherRepository
import com.example.weather_api.app.utils.MutableLiveEvent
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.app.utils.publishEvent
import com.example.weather_api.app.utils.share
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel(
    val weatherRepository: WeatherRepository,
    private val logger: Logger
) : ViewModel() {

    private val _showErrorMessageResEvent = MutableLiveEvent<Int>()
    val showErrorMessageResEvent = _showErrorMessageResEvent.share()

    private val _showErrorMessageEvent = MutableLiveEvent<String>()
    val showErrorMessageEvent = _showErrorMessageEvent.share()

    fun showToast(message: Int) {
        _showErrorMessageResEvent.publishEvent(message)
    }

    fun showToastString(message: String) {
        _showErrorMessageEvent.publishEvent(message)
    }

    fun CoroutineScope.safeLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            try {
                block.invoke(this)
            } catch (e: ConnectionException) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.connection_error)
            } catch (e: BackendException) {
                logError(e)
                _showErrorMessageEvent.publishEvent(e.message ?: "")
            } catch (e: CityNotFoundException) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.city_not_found_exception)
            } catch (e: InvalidApiKeyException) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.invalid_api_key)
            } catch (e: Exception) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.internal_error)
            }
        }
    }

    private fun logError(e: Throwable) {
        logger.error(javaClass.simpleName, e)
    }
}