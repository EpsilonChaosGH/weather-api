package com.example.weather_api.app.model.settings

import android.content.Context
import com.example.weather_api.app.model.settings.AppSettings.Companion.NO_CITY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesAppSettings @Inject constructor(
    @ApplicationContext appContext: Context
) : AppSettings {

    private val sharedPreferences =
        appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override fun setCurrentCityName(cityName: String) {
        sharedPreferences.edit()
            .putString(PREF_CURRENT_CITY_NAME, cityName)
            .apply()
    }

    override fun getCurrentCityName(): String =
        sharedPreferences.getString(PREF_CURRENT_CITY_NAME, NO_CITY) ?: NO_CITY

    companion object {
        private const val PREF_CURRENT_CITY_NAME = "currentCity"
    }
}