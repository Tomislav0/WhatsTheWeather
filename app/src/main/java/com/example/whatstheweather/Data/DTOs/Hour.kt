package com.example.whatstheweather.Data.DTOs

data class Hour(
    val chance_of_rain: Int,
    val condition: Condition,
    val humidity: Int,
    val is_day: Int,
    val precip_in: Double,
    val temp_c: String,
    val time: String,
    val time_epoch: String,
    val wind_kph: String
)