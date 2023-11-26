package com.groks.weather.data.requests

import android.util.Log
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.groks.weather.data.interfaces.WeatherApi
import com.groks.weather.data.model.Root
import kotlinx.coroutines.delay
import retrofit2.Retrofit

class ConnectionErrorException(message: String) : Exception(message)

object WeatherApiRequest {
    private const val MAX_RETRY_COUNT = 10
    private const val DELAY_MS = 2000L // Initial delay in milliseconds

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://api.weatherapi.com/v1/")
        .build()

    private val weatherApiService: WeatherApi = retrofit.create(WeatherApi::class.java)

    suspend fun weatherRequest(city: String, callback: (Root?, Exception?) -> Unit) {
        var retryCount = 0
        var productResponse: Root?
        val delayMs = DELAY_MS

        try {

            while (retryCount < MAX_RETRY_COUNT) {

                try {
                    val responseBody = weatherApiService.getForecast(city = city)
                    val responseText = responseBody.string()
                    Log.d("Json string", responseText)
                    val objectMapper = ObjectMapper().registerKotlinModule()
                    val productsType = object : TypeReference<Root>() {}
                    productResponse = objectMapper.readValue(responseText, productsType)

                    // Check if the response is correct
                    if (productResponse != null) {
                        callback(productResponse, null)
                        return
                    }

                } catch (e: JsonParseException) {
                    Log.e("MyScreen", "JSON parsing error: ${e.message}")
                    callback(null, e)
                    return
                } catch (e: Exception) {
                    callback(null, e)
                    Log.e("MyScreen", "exception: ${e.message}")
                }

                retryCount++
                delay(delayMs) // Delay before the next retry
                if (retryCount == MAX_RETRY_COUNT) throw ConnectionErrorException("Reached max retry count")
            }
        } catch (e: ConnectionErrorException){
            callback(null, e)
            Log.e("Connection", "Retry exception: ${e.message}")
        }
    }
}