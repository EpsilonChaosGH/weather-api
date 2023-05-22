package com.example.weather_api.app.screens.main.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.model.FavoritesState
import com.example.weather_api.app.utils.Event
import com.example.weather_api.app.utils.safeLaunchAsync
import com.example.weather_api.core_data.WeatherRepositoryImpl
import com.example.weather_api.core_data.mappers.toWeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val weatherRepository: WeatherRepositoryImpl,
) : ViewModel() {

    private val _showErrorMessageResEvent = MutableStateFlow<Event<Int?>>(Event(null))
    val showErrorMessageResEvent = _showErrorMessageResEvent.asStateFlow()

    private val _favoritesState: MutableStateFlow<FavoritesState?> = MutableStateFlow(null)
    val favoritesState: StateFlow<FavoritesState?> = _favoritesState.asStateFlow()

    init {
        listenCurrentState()
    }

    fun refreshFavorites() {
        viewModelScope.launch {
            val result = viewModelScope.safeLaunchAsync {
                _favoritesState.value = _favoritesState.value?.copy(refreshState = true)
                weatherRepository.refreshFavorites()
                _favoritesState.value = _favoritesState.value?.copy(refreshState = false)
            }
            _showErrorMessageResEvent.value = Event(result.await())
        }
    }

    fun deleteFromFavorites(city: String) {
        viewModelScope.launch {
            val result = viewModelScope.safeLaunchAsync {
                weatherRepository.deleteFromFavoritesByCity(city)
            }
            _showErrorMessageResEvent.value = Event(result.await())
        }
    }

    fun setCurrentWeather(city: String) {
        viewModelScope.launch {

            val result = viewModelScope.safeLaunchAsync {
                weatherRepository.fromFavoritesMainWeatherToCurrent(city)
            }
            _showErrorMessageResEvent.value = Event(result.await())
        }
    }

    private fun listenCurrentState() {
        viewModelScope.launch {

            val result = viewModelScope.safeLaunchAsync {
                weatherRepository.listenFavoriteLocations().collect { list ->
                    if (list.isEmpty()) {
                        _favoritesState.value = _favoritesState.value?.copy(emptyListState = true)
                    } else {
                        _favoritesState.value = _favoritesState.value?.copy(emptyListState = false)

                        _favoritesState.value = FavoritesState(favorites = list.map {
                            it!!.weatherEntity.toWeatherState(isFavorites = true)
                        })
                    }
                }
            }

            _showErrorMessageResEvent.value = Event(result.await())
        }
    }
}