package com.example.weather_api.app.screens.main.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.model.WeatherState
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.app.utils.share
import com.example.weather_api.core_data.WeatherRepository
import com.example.weather_api.core_data.mappers.toWeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoritesViewModel @Inject constructor(
    weatherRepository: WeatherRepository,
    logger: Logger
) : BaseViewModel(weatherRepository, logger) {

    private val _favoritesState = MutableLiveData<List<WeatherState>>()
    val favoritesState = _favoritesState.share()

    private val _emptyListState = MutableLiveData<Boolean>()
    val emptyListState = _emptyListState.share()

    init {
        listenCurrentState()
    }

    suspend fun refreshFavorites() {
        viewModelScope.safeLaunch {
            weatherRepository.refreshFavorites()
        }.join()
    }

    private fun listenCurrentState() {

        viewModelScope.safeLaunch {
            weatherRepository.listenFavoriteLocations().collect { list ->
                if (list.isEmpty()) {
                    _emptyListState.value = true
                } else {
                    _emptyListState.value = false
                    _favoritesState.value = list.map { weather ->
                        weather!!.weatherEntity.toWeatherState(isFavorites = true)
                    }
                }
            }
        }
    }

    fun deleteFromFavorites(city: String) {
        viewModelScope.safeLaunch {
            weatherRepository.deleteFromFavoritesByCity(city)
        }
    }

    fun setCurrentWeather(city: String) {
        viewModelScope.launch {
            weatherRepository.fromFavoritesMainWeatherToCurrent(city)
        }
    }
}