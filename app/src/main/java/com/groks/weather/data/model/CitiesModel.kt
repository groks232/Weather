package com.groks.weather.data.model

data class Cities (
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val population: Long,
    val is_capital: Boolean
)

typealias CitiesRoot = List<Cities>;
