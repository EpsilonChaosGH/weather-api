package com.example.weather_api.app.screens.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_api.R
import com.example.weather_api.app.utils.ConnectivityObserver
import com.example.weather_api.app.utils.MutableLiveEvent
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.app.utils.publishEvent
import com.example.weather_api.app.utils.share
import com.example.weather_api.core_data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel(
    val weatherRepository: WeatherRepository,
    val connectivityObserver: ConnectivityObserver,
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

    fun CoroutineScope.safeLaunch(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch() {
            try {
                block.invoke(this)
            } catch (e: ConnectionException) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.error_connection)
            } catch (e: BackendException) {
                logError(e)
                _showErrorMessageEvent.publishEvent(e.message ?: "")
            } catch (e: CityNotFoundException) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.error_404_city_not_found)
            } catch (e: InvalidApiKeyException) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.error_401_invalid_api_key)
            } catch (e: RequestRateLimitException) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.error_429_request_rate_limit_surpassing)
            } catch (e: Exception) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.error_internal)
            } catch (e: StorageException) {
                logError(e)
                showToastString("empty favorites")
            }
        }

    private fun logError(e: Throwable) {
        logger.error(javaClass.simpleName, e)
    }
}