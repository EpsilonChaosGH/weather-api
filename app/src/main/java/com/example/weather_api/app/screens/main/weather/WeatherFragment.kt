package com.example.weather_api.app.screens.main.weather

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_api.R
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.main.entities.Coordinates
import com.example.weather_api.app.screens.base.BaseFragment
import com.example.weather_api.databinding.FragmentWeatherBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


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

        binding.cityEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getWeatherByCity(binding.cityEditText.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        addMenu()
        observeForecastState()
        observeState()
    }


    private fun getWeatherByCity(city: String) {
        viewModel.getWeatherByCity(City(city))
        if (viewModel.state.value?.emptyCityError == true) {
            showSoftKeyboard(binding.cityEditText)
        } else {
            binding.cityTextInput.visibility = View.GONE
        }
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
                    viewModel.getWeatherByCoordinates(coordinates)
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

    private fun addMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.actionSearch -> {
                        if (binding.cityTextInput.visibility == View.VISIBLE) {
                            binding.cityTextInput.visibility = View.GONE
                            hideKeyboardFrom(binding.cityEditText)
                        } else {
                            binding.cityTextInput.visibility = View.VISIBLE
                            showSoftKeyboard(binding.cityEditText)
                        }
                        true
                    }
                    R.id.actionLocation -> {
                        getWeatherByCoordinates()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboardFrom(view: View?) {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}