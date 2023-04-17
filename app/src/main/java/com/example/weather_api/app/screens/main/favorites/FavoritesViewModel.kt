package com.example.weather_api.app.screens.main.favorites

import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.model.FavoritesState
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.core_data.WeatherRepository
import com.example.weather_api.core_data.mappers.toWeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoritesViewModel @Inject constructor(
    weatherRepository: WeatherRepository,
    logger: Logger
) : BaseViewModel(weatherRepository, logger) {

    private val _favoritesState: MutableStateFlow<FavoritesState> =
        MutableStateFlow(FavoritesState())
    val favoritesState: StateFlow<FavoritesState> = _favoritesState.asStateFlow()

    init {
        listenCurrentState()
    }

    fun refreshFavorites() {
        viewModelScope.safeLaunch {
            _favoritesState.value = _favoritesState.value.copy(refreshState = true)
            weatherRepository.refreshFavorites()
            _favoritesState.value = _favoritesState.value.copy(refreshState = false)
        }
    }

    fun deleteFromFavorites(city: String) {
        viewModelScope.safeLaunch {
            weatherRepository.deleteFromFavoritesByCity(city)
        }
    }

    fun setCurrentWeather(city: String) {
        viewModelScope.safeLaunch {
            weatherRepository.fromFavoritesMainWeatherToCurrent(city)
        }
    }

    private fun listenCurrentState() {
        viewModelScope.safeLaunch {
            weatherRepository.listenFavoriteLocations().collect { list ->
                if (list.isEmpty()) {
                    _favoritesState.value = _favoritesState.value.copy(emptyListState = true)
                } else {
                    _favoritesState.value = _favoritesState.value.copy(emptyListState = false)
                    _favoritesState.value = _favoritesState.value.copy(list.map { weather ->
                        weather!!.weatherEntity.toWeatherState(isFavorites = true)
                    })
                }
            }
        }
    }
}