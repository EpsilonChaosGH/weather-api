package com.example.weather_api.app.screens.main.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.model.WeatherState
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.FORMAT_EEE_d_MMMM_HH_mm
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.app.utils.share
import com.example.weather_api.core_data.WeatherRepository
import com.example.weather_api.core_data.mappers.toWeatherState
import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.core_data.models.WeatherEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoritesViewModel @Inject constructor(
    weatherRepository: WeatherRepository,
    logger: Logger
) : BaseViewModel(weatherRepository, logger) {

    private val _favoritesState = MutableLiveData<List<WeatherState>>()
    val favoritesState = _favoritesState.share()

    init {
        listenCurrentState()
    }

    private fun listenCurrentState() {
        viewModelScope.safeLaunch {
            weatherRepository.listenCurrentFavoritesLocations().collect { list ->
                val favoritesDef = mutableListOf<Deferred<WeatherEntity>>()
                list.map {
                    val response = async {
                        return@async weatherRepository.getFavoriteWeatherByCoordinates(
                            Coordinates(
                                lon = it.lon,
                                lat = it.lat
                            )
                        )
                    }
                    favoritesDef.add(response)
                }
                val favorites = favoritesDef.map { it.await() }.toMutableList()
                favorites.sortBy { it.city }
                _favoritesState.value = favorites.map { it.toWeatherState(FORMAT_EEE_d_MMMM_HH_mm) }
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
            weatherRepository.setCurrentWeather(city)
        }
    }
}