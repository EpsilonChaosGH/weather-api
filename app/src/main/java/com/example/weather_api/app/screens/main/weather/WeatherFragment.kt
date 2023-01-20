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
import com.example.weather_api.R
import com.example.weather_api.app.model.AirQuality
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.main.entities.Coordinates
import com.example.weather_api.app.screens.base.BaseFragment
import com.example.weather_api.databinding.FragmentWeatherBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : BaseFragment(R.layout.fragment_weather) {

    private val requestLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onGotLocationPermissionResult
    )

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var adapter: WeatherAdapter
    override val viewModel by viewModels<WeatherViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeatherBinding.bind(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        adapter = WeatherAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.cityEditText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getWeatherByCity(binding.cityEditText.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        binding.SearchByCoordinatesImageView.setOnClickListener {
            getWeatherByCoordinates()
        }

        observeForecastState()
        observeState()
        observeAirState()
    }

    private fun getWeatherByCity(city: String) {
        viewModel.getWeatherAndWeatherForecastByCity(City(city))
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
                    viewModel.getWeatherAndWeatherForecastByCoordinate(coordinates)
                } else {
                    viewModel.showToast(R.string.gps_not_found)
                }
            }
    }

    private fun observeForecastState() {
        viewModel.forecastState.observe(viewLifecycleOwner) {
            adapter.weatherList = it
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) {

            binding.cityEditText.error =
                if (it.emptyCityError) getString(R.string.field_is_empty) else null

            binding.cityTextInput.isEnabled = it.enableViews
            binding.SearchByCoordinatesImageView.isEnabled = it.enableViews

            binding.cityNameTextView.text = it.cityName
            binding.countryTextView.text = it.country
            binding.temperatureTextView.text = it.temperature
            binding.currentWeatherTextView.text = it.description
            binding.currentDateTextView.text = it.date
            binding.feelsLikeTextView.text = "${it.feelsLike}°"
            binding.humidityTextView.text = "${it.humidity} %"
            binding.pressureTextView.text = "${it.pressure} hPa"
            binding.windSpeedTextView.text = "${it.windSpeed} m/s"

            when (it.mainWeather) {
                "Clear" -> binding.weatherIconImageView.setImageResource(R.drawable.ic_sun)
                "Clouds" -> binding.weatherIconImageView.setImageResource(R.drawable.ic_cloud)
                "Rain" -> binding.weatherIconImageView.setImageResource(R.drawable.ic_heavey_rain)
                "Snow" -> binding.weatherIconImageView.setImageResource(R.drawable.ic_winter)
                "Mist" -> binding.weatherIconImageView.setImageResource(R.drawable.ic_fog)
                else -> binding.weatherIconImageView.setImageResource(R.drawable.ic_cloudy)
            }

            binding.progressBar.visibility = if (it.showProgress) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun observeAirState(){
        viewModel.airState.observe(viewLifecycleOwner){
            checkNo2(it)
            checkPm10(it)
            checkO3(it)
            checkPm25(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkNo2(it: WeatherViewModel.AirState){
        when(it.no2Quality){
            AirQuality.Good -> {
                binding.NO2QualityTextView.text = "Good"
                binding.NO2QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
            AirQuality.Fair -> {
                binding.NO2QualityTextView.text = "Fair"
                binding.NO2QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
            AirQuality.Moderate -> {
                binding.NO2QualityTextView.text = "Moderate"
                binding.NO2QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
            AirQuality.Poor -> {
                binding.NO2QualityTextView.text = "Poor"
                binding.NO2QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
            AirQuality.VeryPoor -> {
                binding.NO2QualityTextView.text = "VeryPoor"
                binding.NO2QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
            AirQuality.Error -> {
                binding.NO2QualityTextView.text = "Error"
                binding.NO2QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                binding.NO2concentrationTextView.text = "${it.no2} μg/m3"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkPm10(it: WeatherViewModel.AirState){
        when(it.pm10Quality){
            AirQuality.Good -> {
                binding.PM10QualityTextView.text = "Good"
                binding.PM10QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
            AirQuality.Fair -> {
                binding.PM10QualityTextView.text = "Fair"
                binding.PM10QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
            AirQuality.Moderate -> {
                binding.PM10QualityTextView.text = "Moderate"
                binding.PM10QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
            AirQuality.Poor -> {
                binding.PM10QualityTextView.text = "Poor"
                binding.PM10QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
            AirQuality.VeryPoor -> {
                binding.PM10QualityTextView.text = "VeryPoor"
                binding.PM10QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
            AirQuality.Error -> {
                binding.PM10QualityTextView.text = "Error"
                binding.PM10QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                binding.PM10concentrationTextView.text = "${it.pm10} μg/m3"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkO3(it: WeatherViewModel.AirState){
        when(it.o3Quality){
            AirQuality.Good -> {
                binding.O3QualityTextView.text = "Good"
                binding.O3QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
            AirQuality.Fair -> {
                binding.O3QualityTextView.text = "Fair"
                binding.O3QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
            AirQuality.Moderate -> {
                binding.O3QualityTextView.text = "Moderate"
                binding.O3QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
            AirQuality.Poor -> {
                binding.O3QualityTextView.text = "Poor"
                binding.O3QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
            AirQuality.VeryPoor -> {
                binding.O3QualityTextView.text = "VeryPoor"
                binding.O3QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
            AirQuality.Error -> {
                binding.O3QualityTextView.text = "Error"
                binding.O3QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                binding.O3concentrationTextView.text = "${it.o3} μg/m3"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkPm25(it: WeatherViewModel.AirState){
        when(it.pm25Quality){
            AirQuality.Good -> {
                binding.PM25QualityTextView.text = "Good"
                binding.PM25QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
            }
            AirQuality.Fair -> {
                binding.PM25QualityTextView.text = "Fair"
                binding.PM25QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
            }
            AirQuality.Moderate -> {
                binding.PM25QualityTextView.text = "Moderate"
                binding.PM25QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
            }
            AirQuality.Poor -> {
                binding.PM25QualityTextView.text = "Poor"
                binding.PM25QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
            }
            AirQuality.VeryPoor -> {
                binding.PM25QualityTextView.text = "VeryPoor"
                binding.PM25QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.PM25concentrationTextView.text = "${it.pm25} μg/m3"
            }
            AirQuality.Error -> {
                binding.PM25QualityTextView.text = "Error"
                binding.PM25QualityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
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
                .setPositiveButton(R.string.open) { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }
}