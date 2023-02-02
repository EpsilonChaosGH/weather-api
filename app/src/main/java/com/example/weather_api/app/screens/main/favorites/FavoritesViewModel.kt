package com.example.weather_api.app.screens.main.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.app.utils.share
import com.example.weather_api.core_data.WeatherRepository
import com.example.weather_api.core_data.models.WeatherEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoritesViewModel @Inject constructor(
    weatherRepository: WeatherRepository,
    logger: Logger
) : BaseViewModel(weatherRepository, logger) {

    private val _favoritesState = MutableLiveData<List<WeatherEntity>>()
    val favoritesState = _favoritesState.share()

    init {
        listenCurrentState()
    }

    private fun listenCurrentState() {
        viewModelScope.launch {
            weatherRepository.listenCurrentFavoritesLocations().collect { weather ->
                _favoritesState.value = weather
            }
        }
    }
}