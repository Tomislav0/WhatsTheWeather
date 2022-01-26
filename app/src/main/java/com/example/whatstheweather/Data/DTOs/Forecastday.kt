package com.example.whatstheweather.Data.DTOs

data class Forecastday(
    val date: String,
    val date_epoch:String,
    val day: Day,
    val hour: List<Hour>
)