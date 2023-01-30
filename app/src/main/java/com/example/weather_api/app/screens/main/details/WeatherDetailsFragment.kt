package com.example.weather_api.app.screens.main.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weather_api.R
import com.example.weather_api.databinding.FragmentWeatherDetailsBinding

class WeatherDetailsFragment : Fragment(R.layout.fragment_weather_details) {

    private lateinit var binding: FragmentWeatherDetailsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeatherDetailsBinding.bind(view)

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_weatherDetailsFragment_to_main_graph)

            //findNavController().navigate(R.id.action_weatherDetailsFragment_to_weatherFragment2)
        }
    }
}