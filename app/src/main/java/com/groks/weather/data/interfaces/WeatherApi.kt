package com.groks.weather.data.interfaces

import com.groks.weather.BuildConfig
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") key: String = BuildConfig.WEATHER_API_KEY,
        @Query("q") city: String,
        @Query("days") numberOfDays: Int = 7
    ): ResponseBody
}