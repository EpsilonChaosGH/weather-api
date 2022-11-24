package com.example.weather_api.app.screens.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weather_api.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, MainFragment())
                .commit()
        }

}