package com.groks.weather.data.requests

import android.util.Log
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.groks.weather.data.interfaces.CitiesApi
import com.groks.weather.data.model.CitiesRoot
import retrofit2.Retrofit

object CitiesApiRequest {
    private var productResponse: CitiesRoot? = null

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.api-ninjas.com/")
        .build()

    private val citiesApiService: CitiesApi = retrofit.create(CitiesApi::class.java)

    suspend fun citiesRequest(city: String, callback: (CitiesRoot?, Exception?) -> Unit){
        try {
            val responseBody = citiesApiService.getCities(name = city)
            val responseText = responseBody.string()
            Log.d("Json string", responseText)
            val objectMapper = ObjectMapper().registerKotlinModule()
            val productsType = object : TypeReference<CitiesRoot>() {}
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
    }
}