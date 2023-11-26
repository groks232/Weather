package com.groks.weather.data.interfaces

import com.groks.weather.BuildConfig
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CitiesApi {
    @Headers("X-Api-Key: ${BuildConfig.CITIES_API_KEY}")
    @GET("/v1/city")
    suspend fun getCities(
        @Query("name") name: String,
        @Query("limit") limit: Int = 10,
        @Query("min_population ") minPopulation: Int = 100
    ): ResponseBody
}