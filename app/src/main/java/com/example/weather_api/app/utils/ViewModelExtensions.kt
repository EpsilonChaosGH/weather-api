package com.example.weather_api.app.utils


import com.example.weather_api.R
import com.example.weather_api.core_data.CityNotFoundException
import com.example.weather_api.core_data.ConnectionException
import com.example.weather_api.core_data.InvalidApiKeyException
import com.example.weather_api.core_data.RequestRateLimitException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch


fun CoroutineScope.safeLaunchAsync(block: suspend CoroutineScope.() -> Unit) =
    this.async {
        val result: Int? = try {
            block.invoke(this)
            null
        } catch (e: ConnectionException) {
            R.string.error_connection
        } catch (e: CityNotFoundException) {
            R.string.error_404_city_not_found
        } catch (e: InvalidApiKeyException) {
            R.string.error_401_invalid_api_key
        } catch (e: RequestRateLimitException) {
            R.string.error_429_request_rate_limit_surpassing
        } catch (e: Exception) {
            R.string.error_internal
        }
        return@async result
    }