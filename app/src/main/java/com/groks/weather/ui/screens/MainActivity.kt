package com.groks.weather.ui.screens

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.groks.weather.ui.NavController
import com.groks.weather.ui.screens.city.CityScreen
import com.groks.weather.ui.screens.loading.LoadingScreen
import com.groks.weather.ui.theme.WeatherTheme
import com.groks.weather.viewmodel.DataStoreHelper
import com.groks.weather.viewmodel.ViewModel
import com.groks.weather.viewmodel.ViewModelFactory
import com.groks.weather.viewmodel.ViewState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val context = this.applicationContext
                    Main(context = context)
                }
            }
        }
    }
}

@Composable
fun Main(context: Context){
    val navController = rememberNavController()

    val dataStoreHelper = DataStoreHelper(context)

    val viewModel: ViewModel = viewModel(factory = ViewModelFactory(dataStoreHelper))

    val viewState by viewModel.viewState.collectAsState()

    when(viewState){
        is ViewState.Loading -> LoadingScreen()
        is ViewState.HasCity -> NavControllerEntry(navController = navController, viewModel = viewModel)
        is ViewState.NotHasCity -> CityScreen(viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavControllerEntry(navController: NavHostController, viewModel: ViewModel){
    Scaffold {
        Column(Modifier.padding(paddingValues = it)) {
            NavController(navController = navController, viewModel = viewModel)
        }
    }
}