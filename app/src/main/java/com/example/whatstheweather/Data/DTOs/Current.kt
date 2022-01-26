package com.example.whatstheweather.Data.DTOs

data class Current(
    val condition: Condition,
    val humidity: Int,
    val is_day: Int,
    val last_updated: String,
    val pressure_in: Double,
    val temp_c: String,
    val uv: Double,
    val wind_kph: Double,
    val precip_mm:String
)