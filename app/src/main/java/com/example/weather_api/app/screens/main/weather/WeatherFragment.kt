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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.weather_api.R
import com.example.weather_api.app.model.AirQuality
import com.example.weather_api.app.model.Field
import com.example.weather_api.core_data.models.City
import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.app.screens.base.BaseFragment
import com.example.weather_api.core_data.EmptyFieldException
import com.example.weather_api.databinding.FragmentWeatherBinding
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : BaseFragment(R.layout.fragment_weather) {

    override val viewModel by viewModels<WeatherViewModel>()

    private val binding by viewBinding(FragmentWeatherBinding::bind)

    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) { WeatherAdapter() }

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private val requestLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onGotLocationPermissionResult
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

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

    @SuppressLint("SetTextI18n")
    private fun observeWeatherState() {
        viewModel.state.observe(viewLifecycleOwner) {
            with(binding) {
                cityEditText.error =
                    if (it.emptyCityError) getString(R.string.error_field_is_empty) else null

                cityTextInput.isEnabled = it.enableViews
                searchByCoordinatesImageView.isEnabled = it.enableViews

                cityNameTextView.text = it.cityName
                countryTextView.text = it.country
                temperatureTextView.text = "${it.temperature}°C"
                currentWeatherTextView.text = it.description
                currentDateTextView.text = it.date
                feelsLikeTextView.text = "${it.feelsLike}°"
                humidityTextView.text = "${it.humidity} %"
                pressureTextView.text = "${it.pressure} hPa"
                windSpeedTextView.text = "${it.windSpeed} m/s"

                when (it.mainWeather) {
                    "Clear" -> weatherIconImageView.setImageResource(R.drawable.ic_sun)
                    "Clouds" -> weatherIconImageView.setImageResource(R.drawable.ic_cloud)
                    "Rain" -> weatherIconImageView.setImageResource(R.drawable.ic_heavey_rain)
                    "Snow" -> weatherIconImageView.setImageResource(R.drawable.ic_winter)
                    "Mist" -> weatherIconImageView.setImageResource(R.drawable.ic_fog)
                    else -> weatherIconImageView.setImageResource(R.drawable.ic_cloudy)
                }

                if (it.isFavorite) favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_24)
                else favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)

                progressBar.visibility =
                    if (it.showProgress) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    private fun observeAirState() {
        viewModel.airState.observe(viewLifecycleOwner) {
            checkNo2(it)
            checkPm10(it)
            checkO3(it)
            checkPm25(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkNo2(it: WeatherViewModel.AirState) {
        when (it.no2Quality) {
            AirQuality.Good -> {
                binding.NO2QualityTextView.text = "Good"
                binding.NO2QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
            AirQuality.Fair -> {
                binding.NO2QualityTextView.text = "Fair"
                binding.NO2QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
            AirQuality.Moderate -> {
                binding.NO2QualityTextView.text = "Moderate"
                binding.NO2QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.yellow
                    )
                )
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
            AirQuality.Poor -> {
                binding.NO2QualityTextView.text = "Poor"
                binding.NO2QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange
                    )
                )
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
            AirQuality.VeryPoor -> {
                binding.NO2QualityTextView.text = "VeryPoor"
                binding.NO2QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
            AirQuality.Error -> {
                binding.NO2QualityTextView.text = "Error"
                binding.NO2QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkPm10(it: WeatherViewModel.AirState) {
        when (it.pm10Quality) {
            AirQuality.Good -> {
                binding.PM10QualityTextView.text = "Good"
                binding.PM10QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
            AirQuality.Fair -> {
                binding.PM10QualityTextView.text = "Fair"
                binding.PM10QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
            AirQuality.Moderate -> {
                binding.PM10QualityTextView.text = "Moderate"
                binding.PM10QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.yellow
                    )
                )
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
            AirQuality.Poor -> {
                binding.PM10QualityTextView.text = "Poor"
                binding.PM10QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange
                    )
                )
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
            AirQuality.VeryPoor -> {
                binding.PM10QualityTextView.text = "VeryPoor"
                binding.PM10QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
            AirQuality.Error -> {
                binding.PM10QualityTextView.text = "Error"
                binding.PM10QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkO3(it: WeatherViewModel.AirState) {
        when (it.o3Quality) {
            AirQuality.Good -> {
                binding.O3QualityTextView.text = "Good"
                binding.O3QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
            AirQuality.Fair -> {
                binding.O3QualityTextView.text = "Fair"
                binding.O3QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
            AirQuality.Moderate -> {
                binding.O3QualityTextView.text = "Moderate"
                binding.O3QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.yellow
                    )
                )
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
            AirQuality.Poor -> {
                binding.O3QualityTextView.text = "Poor"
                binding.O3QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange
                    )
                )
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
            AirQuality.VeryPoor -> {
                binding.O3QualityTextView.text = "VeryPoor"
                binding.O3QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
            AirQuality.Error -> {
                binding.O3QualityTextView.text = "Error"
                binding.O3QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkPm25(it: WeatherViewModel.AirState) {
        when (it.pm25Quality) {
            AirQuality.Good -> {
                binding.PM25QualityTextView.text = "Good"
                binding.PM25QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
            }
            AirQuality.Fair -> {
                binding.PM25QualityTextView.text = "Fair"
                binding.PM25QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
            }
            AirQuality.Moderate -> {
                binding.PM25QualityTextView.text = "Moderate"
                binding.PM25QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.yellow
                    )
                )
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
            }
            AirQuality.Poor -> {
                binding.PM25QualityTextView.text = "Poor"
                binding.PM25QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange
                    )
                )
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
            }
            AirQuality.VeryPoor -> {
                binding.PM25QualityTextView.text = "VeryPoor"
                binding.PM25QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
            }
            AirQuality.Error -> {
                binding.PM25QualityTextView.text = "Error"
                binding.PM25QualityTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
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