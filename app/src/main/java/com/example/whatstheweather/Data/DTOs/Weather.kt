package com.example.whatstheweather.Data.DTOs

data class Weather(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)