package com.example.whatstheweather.Services

import com.example.whatstheweather.Data.DTOs.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIEndpoints {
    @GET("v1/forecast.json?")
    fun getWeather(@Query("q") cityName:String,
                   @Query("key") key:String="0184c86564c54eef9ce121501212712",
                    @Query("days") days:String="7") : Call<Weather>
}
