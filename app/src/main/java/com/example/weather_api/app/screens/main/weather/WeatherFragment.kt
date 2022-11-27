package com.example.weather_api.app.screens.main.weather

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.weather_api.R
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.screens.base.BaseFragment
import com.example.weather_api.databinding.FragmentWeatherBinding

class WeatherFragment : BaseFragment(R.layout.fragment_weather) {

    private lateinit var binding: FragmentWeatherBinding
    override val viewModel by viewModels<WeatherViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeatherBinding.bind(view)
        binding.getWeatherButton.setOnClickListener { onGetWeatherButtonPressed() }

        observeState()
    }

    private fun onGetWeatherButtonPressed() {
        viewModel.getWeatherByCity(City(binding.cityTextInput.text.toString()))
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) {
            binding.cityTextInput.error =
                if (it.emptyCityError) getString(R.string.field_is_empty) else null

            binding.cityTextInput.isEnabled = it.enableViews
            binding.getWeatherButton.isEnabled = it.enableViews

            binding.weatherTextView.text = it.weather

            binding.progressBar.visibility = if (it.showProgress) View.VISIBLE else View.INVISIBLE
        }
    }
}