package com.groks.weather.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.groks.weather.ui.screens.weather.WeatherScreen
import com.groks.weather.viewmodel.ViewModel

@Composable
fun NavController(navController: NavHostController, viewModel: ViewModel){
    NavHost(navController = navController, startDestination = "weather_screen"){
        navigation(
            startDestination = "weather",
            route = "weather_screen"
        ){
            composable("weather"){
                WeatherScreen(viewModel = viewModel)
            }
        }
    }
}