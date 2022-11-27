package com.example.weather_api.app.screens.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weather_api.R
import com.example.weather_api.app.Singletons
import com.example.weather_api.app.screens.main.weather.WeatherFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Singletons.init(applicationContext)
    }
}