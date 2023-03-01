package com.example.weather_api.app.screens.main.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.weather_api.R
import com.example.weather_api.app.model.Field
import com.example.weather_api.core_data.models.City
import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.app.screens.base.BaseFragment
import com.example.weather_api.app.utils.observeEvent
import com.example.weather_api.core_data.EmptyFieldException
import com.example.weather_api.databinding.FragmentWeatherBinding
import com.facebook.shimmer.Shimmer
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : BaseFragment(R.layout.fragment_weather) {

    override val viewModel by viewModels<WeatherViewModel>()

    private val binding by viewBinding(FragmentWeatherBinding::bind)

    private val shimmer by lazy {
        Shimmer.ColorHighlightBuilder()
            .setBaseColor(ContextCompat.getColor(requireContext(), R.color.main_secondary_color))
            .setHighlightColor(ContextCompat.getColor(requireContext(), R.color.main_text_color))
            .setBaseAlpha(1f)
            .setHighlightAlpha(1f)
            .build()
    }

    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) { WeatherAdapter() }

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private val requestLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onGotLocationPermissionResult
    )

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            veilLayout.shimmer = shimmer

            recyclerView.adapter = adapter
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            cityEditText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        if (cityEditText.text!!.isBlank()) throw EmptyFieldException(Field.City)
                        getWeatherByCity(cityEditText.text.toString())
                        return@OnEditorActionListener true
                    } catch (e: EmptyFieldException) {
                        viewModel.emptyFieldException(e)
                    }
                }
                false
            })

            favoriteImageView.setOnClickListener { addOrRemoveToFavorite() }
            searchByCoordinatesImageView.setOnClickListener { getWeatherByCoordinates() }
        }

        observeForecastState()
        observeWeatherState()
        observeAirState()
        showVeil()
        hideVail()
    }

    private fun showVeil() {
        viewModel.showVeilEvent.observeEvent(viewLifecycleOwner) {
            binding.veilLayout.veil()
        }
    }

    private fun hideVail() {
        viewModel.hideVeilEvent.observeEvent(viewLifecycleOwner) {
            binding.veilLayout.unVeil()
        }
    }

    private fun getWeatherByCity(city: String) {
        viewModel.getWeatherAndForecastAndAirByCity(City(city))
    }

    private fun addOrRemoveToFavorite() {
        viewModel.addOrRemoveToFavorite()
    }

    private fun getWeatherByCoordinates() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val coordinates = Coordinates(
                        lat = location.latitude.toString(),
                        lon = location.longitude.toString()
                    )
                    viewModel.getWeatherAndForecastAndAirByCoordinate(coordinates)
                } else {
                    viewModel.showToast(R.string.error_gps_not_found)
                }
            }
    }

    private fun observeForecastState() {
        viewModel.forecastState.observe(viewLifecycleOwner) {
            adapter.weatherList = it
        }
    }

    private fun observeWeatherState() {
        viewModel.weatherState.observe(viewLifecycleOwner) { weatherState ->
            with(binding) {
                cityEditText.error =
                    if (weatherState.emptyCityError) getString(R.string.error_field_is_empty) else null

                cityTextInput.isEnabled = weatherState.enableViews
                searchByCoordinatesImageView.isEnabled = weatherState.enableViews

                cityNameTextView.text = weatherState.city
                temperatureTextView.text = weatherState.temperature
                currentWeatherTextView.text = weatherState.description
                currentDateTextView.text = weatherState.data
                feelsLikeTextView.text = weatherState.feelsLike
                humidityTextView.text = weatherState.humidity
                pressureTextView.text = weatherState.pressure
                windSpeedTextView.text = weatherState.windSpeed
                weatherIconImageView.setImageResource(weatherState.weatherType.iconResId)
                Toast.makeText(
                    requireContext(),
                    weatherState.isFavorite.toString(),
                    Toast.LENGTH_SHORT
                ).show()

                if (weatherState.isFavorite) favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_24)
                else favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)

                progressBar.visibility =
                    if (weatherState.showProgress) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun observeAirState() {
        viewModel.airState.observe(viewLifecycleOwner) { airState ->
            with(binding) {
                valueNO2.text = airState.no2
                qualityNO2.text = getString(airState.no2Quality.qualityResId)
                qualityNO2.setTextColor(
                    ContextCompat.getColor(requireContext(), airState.no2Quality.colorResId)
                )

                valueO3.text = airState.o3
                qualityO3.text = getString(airState.o3Quality.qualityResId)
                qualityO3.setTextColor(
                    ContextCompat.getColor(requireContext(), airState.o3Quality.colorResId)
                )

                valuePM10.text = airState.pm10
                qualityPM10.text = getString(airState.pm10Quality.qualityResId)
                qualityPM10.setTextColor(
                    ContextCompat.getColor(requireContext(), airState.pm10Quality.colorResId)
                )

                valuePM25.text = airState.pm25
                qualityPM25.text = getString(airState.pm25Quality.qualityResId)
                qualityPM25.setTextColor(
                    ContextCompat.getColor(requireContext(), airState.pm25Quality.colorResId)
                )
            }
        }
    }

    private fun onGotLocationPermissionResult(granted: Boolean) {
        if (granted) {
            viewModel.showToast(R.string.permission_grated)
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                askUserForOpeningAppSettings()
            } else {
                viewModel.showToast(R.string.permissions_denied)
            }
        }
    }

    private fun askUserForOpeningAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        )
        if (requireActivity().packageManager.resolveActivity(
                appSettingsIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            viewModel.showToast(R.string.permissions_denied_forever)
        } else {
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.permissions_denied)
                .setMessage(R.string.permissions_denied_forever)
                .setPositiveButton(R.string.button_open_settings) { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }
}