package com.groks.weather.data.model


data class Astro (
    var sunrise: String,
    var sunset: String,
    var moonrise: String,
    var moonset: String,
    var moon_phase: String,
    var moon_illumination: Int,
    var is_moon_up: Int,
    var is_sun_up: Int
)

data class Condition (
    var text: String,
    var icon: String,
    var code: Int
)

data class Current (
    var last_updated_epoch: Int,
    var last_updated: String,
    var temp_c: Double,
    var temp_f: Double,
    var is_day: Int,
    var condition: Condition,
    var wind_mph: Double,
    var wind_kph: Double,
    var wind_degree: Int,
    var wind_dir: String,
    var pressure_mb: Double,
    var pressure_in: Double,
    var precip_mm: Double,
    var precip_in: Double,
    var humidity: Int,
    var cloud: Int,
    var feelslike_c: Double,
    var feelslike_f: Double,
    var vis_km: Double,
    var vis_miles: Double,
    var uv: Int,
    var gust_mph: Double,
    var gust_kph: Double
)

data class Day (
    var maxtemp_c: Double,
    var maxtemp_f: Double,
    var mintemp_c: Double,
    var mintemp_f: Double,
    var avgtemp_c: Double,
    var avgtemp_f: Double,
    var maxwind_mph: Double,
    var maxwind_kph: Double,
    var totalprecip_mm: Int,
    var totalprecip_in: Int,
    var totalsnow_cm: Int,
    var avgvis_km: Int,
    var avgvis_miles: Int,
    var avghumidity: Int,
    var daily_will_it_rain: Int,
    var daily_chance_of_rain: Int,
    var daily_will_it_snow: Int,
    var daily_chance_of_snow: Int,
    var condition: Condition,
    var uv: Int
)

data class Forecast (
    var forecastday: ArrayList<Forecastday>
)

data class Forecastday (
    var date: String,
    var date_epoch: Int,
    var day: Day,
    var astro: Astro,
    var hour: ArrayList<Hour>
)

data class Hour (
    var time_epoch: Int,
    var time: String,
    var temp_c: Double,
    var temp_f: Double,
    var is_day: Int,
    var condition: Condition,
    var wind_mph: Double,
    var wind_kph: Double,
    var wind_degree: Int,
    var wind_dir: String,
    var pressure_mb: Int,
    var pressure_in: Double,
    var precip_mm: Int,
    var precip_in: Int,
    var humidity: Int,
    var cloud: Int,
    var feelslike_c: Double,
    var feelslike_f: Double,
    var windchill_c: Double,
    var windchill_f: Double,
    var heatindex_c: Double,
    var heatindex_f: Double,
    var dewpoint_c: Double,
    var dewpoint_f: Double,
    var will_it_rain: Int,
    var chance_of_rain: Int,
    var will_it_snow: Int,
    var chance_of_snow: Int,
    var vis_km: Int,
    var vis_miles: Int,
    var gust_mph: Int,
    var gust_kph: Double,
    var uv: Int
)

data class Location(
    var name: String,
    var region: String,
    var country: String,
    var lat: Double,
    var lon: Double,
    var tz_id: String,
    var localtime_epoch: Int,
    var localtime: String
)

data class Root (
    var location: Location,
    var current: Current,
    var forecast: Forecast
)