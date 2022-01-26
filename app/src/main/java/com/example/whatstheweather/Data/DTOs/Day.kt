package com.example.whatstheweather.Data.DTOs

data class Day(
    val avghumidity: String,
    val condition: Condition,
    val daily_chance_of_rain: String,
    val maxtemp_c: String,
    val maxwind_kph: String,
    val mintemp_c: String,
    val totalprecip_in: String
)