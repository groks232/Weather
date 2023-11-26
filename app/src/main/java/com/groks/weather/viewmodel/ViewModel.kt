package com.groks.weather.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.groks.weather.data.db.AppDatabase
import com.groks.weather.data.db.CityDB
import com.groks.weather.data.model.CitiesRoot
import com.groks.weather.data.model.Root
import com.groks.weather.data.requests.CitiesApiRequest
import com.groks.weather.data.requests.WeatherApiRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ViewModel(
    private val dataStoreHelper: DataStoreHelper
): ViewModel(){
    private var _searchRequestString by mutableStateOf("")

    val searchRequestString: String
        get() = _searchRequestString

    fun updateSearchRequestString(searchRequestString: String){
        _searchRequestString = searchRequestString
    }


    private val _connectionState = mutableStateOf(false)
    val connectionState: State<Boolean> = _connectionState

    private fun setConnectedState(isConnected: Boolean) {
        _connectionState.value = isConnected
    }

    private val _hasReachedMaxRetry = mutableStateOf(false)
    val hasReachedMaxRetry: State<Boolean> = _hasReachedMaxRetry

    private fun setReachedMaxRetry(isReached: Boolean) {
        _hasReachedMaxRetry.value = isReached
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private var _city = mutableStateOf<String?>(null)

    val cityState: State<String?> = _city
    fun updateCity(city: String){
        _city.value = city
    }

    var citiesRoot by mutableStateOf<CitiesRoot?>(null)

    fun getCities(city: String){
        viewModelScope.launch {
            CitiesApiRequest.citiesRequest(city = city){ response, exception ->
                if (exception != null){

                }

                else if (response != null){
                    citiesRoot = response
                }
            }
        }
    }

    fun getData(){
        setReachedMaxRetry(false)
        viewModelScope.launch {
            updateCity(getCity()!!)
            if (cityState.value != null)
                forecast = null
            WeatherApiRequest.weatherRequest(cityState.value!!){ response, exception ->
                if (exception != null){
                    setConnectedState(true)
                    if (exception.message == "Reached max retry count"){
                        setReachedMaxRetry(true)
                    }
                }
                else if (response != null) {
                    setConnectedState(false)
                    forecast = response
                }
            }
        }
    }

    fun refresh(){
        viewModelScope.launch {
            _isRefreshing.value = true
            WeatherApiRequest.weatherRequest(cityState.value!!){ response, exception ->
                if (exception != null){
                    Log.e("MyScreen", "Error: ${exception.message}")
                }
                else if (response != null) {
                    forecast = response
                }
            }
            delay(1500L)
            _isRefreshing.value = false
        }
    }

    var forecast by mutableStateOf<Root?>(null)

    val viewState = dataStoreHelper.hasLoggedIn.map { hasLoggedIn ->
        if (hasLoggedIn) {
            ViewState.HasCity
        } else {
            ViewState.NotHasCity
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0),
        initialValue = ViewState.Loading
    )

    suspend fun putCity(city: String){
        dataStoreHelper.putCity(city)
    }

    suspend fun deleteCity(){
        dataStoreHelper.deleteCity()
    }

    suspend fun getCity(): String?{
        return dataStoreHelper.readCity()
    }

    //////////////////////////////////////

    suspend fun addHintItem(item: String){
        val dao = dataStoreHelper.db.dbDao()
        dao.insertHistoryItem(CityDB(city = item))
    }

    suspend fun readHints(): List<CityDB>{
        val dao = dataStoreHelper.db.dbDao()
        return dao.getAllHistoryItems()
    }

    suspend fun deleteHint(){
        val dao = dataStoreHelper.db.dbDao()
        dao.deleteHistoryItem()
    }

    var hints by mutableStateOf<List<CityDB>?>(null)
}

class DataStoreHelper(context: Context) {
    private val Context.userDataStore by preferencesDataStore("settings")

    private val key = stringPreferencesKey("City")

    private val datastore = context.userDataStore

    val hasLoggedIn: Flow<Boolean> = datastore.data
        .map { preferences ->
            preferences[key]?.isNotEmpty() ?: false
        }

    suspend fun putCity(city: String) {
        datastore.edit { settings ->
            settings[key] = city
        }
    }

    suspend fun deleteCity() {
        datastore.edit { settings ->
            settings.clear()
        }
    }

    suspend fun readCity(): String? {
        var city: String? = null
        datastore.edit {
            city = it[key]
        }

        return city
    }

    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "database-name"
    ).build()
}

sealed class ViewState {
    data object Loading: ViewState() // unknown
    data object HasCity: ViewState() // true
    data object NotHasCity: ViewState() // false
}

class ViewModelFactory(private val dataStoreHelper: DataStoreHelper) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ViewModel(dataStoreHelper) as T
}