package com.example.weather_api.core_db.shared_preferebces

import android.content.Context
import com.example.weather_api.app.model.Const
import com.example.weather_api.core_data.models.Coordinates
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesAppSettings @Inject constructor(
    @ApplicationContext appContext: Context
) : AppSettings {

    private val sharedPreferences =
        appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override fun getCurrentCoordinates(): Coordinates {
        return Coordinates(
            sharedPreferences.getString(PREF_CURRENT_LON, Const.DEFAULT_LON) ?: Const.DEFAULT_LON,
            sharedPreferences.getString(PREF_CURRENT_LAT, Const.DEFAULT_LAT) ?: Const.DEFAULT_LAT
        )
    }

    override fun setCurrentCoordinates(coordinates: Coordinates) {
        sharedPreferences.edit()
            .putString(PREF_CURRENT_LON, coordinates.lon)
            .putString(PREF_CURRENT_LAT, coordinates.lat)
            .apply()
    }

    companion object {
        private const val PREF_CURRENT_LON = "CURRENT_LON"
        private const val PREF_CURRENT_LAT = "CURRENT_LAT"
    }
}