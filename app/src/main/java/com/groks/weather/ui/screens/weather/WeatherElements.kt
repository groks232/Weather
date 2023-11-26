package com.groks.weather.ui.screens.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import coil.compose.SubcomposeAsyncImage
import com.groks.weather.R
import com.groks.weather.data.model.Hour
import com.groks.weather.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

fun String.addCharAtIndex(char: Char, index: Int) =
    StringBuilder(this).apply { insert(index, char) }.toString()

fun getOneList(viewModel: ViewModel): List<Hour>{
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val list = mutableListOf<Hour>()
    viewModel.forecast!!.forecast.forecastday[0].hour.forEach {
        try {
            if (
                LocalDateTime.parse(it.time, formatter) >
                LocalDateTime.parse(viewModel.forecast?.location?.localtime, formatter)
            ) list.add(it)
        } catch (e: Exception){
            if (
                LocalDateTime.parse(it.time, formatter) >
                LocalDateTime.parse(viewModel.forecast?.location?.localtime?.addCharAtIndex('0', 11), formatter)
            ) list.add(it)
        }
    }

    for (i in 0 until (24 - list.size)){
        list.add(viewModel.forecast!!.forecast.forecastday[1].hour[i])
    }

    return list
}

@Composable
fun HourlyPanel(viewModel: ViewModel){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(30.dp)
    ) {
        Text(
            text = "Hourly",
            Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            fontSize = 20.sp
        )
        Divider(thickness = 1.dp, color = Color.LightGray)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ){
            if (viewModel.forecast != null){
                val list = getOneList(viewModel)
                val fontSize = 17.sp
                items(list.size){
                    Column(
                        modifier = Modifier
                            .height(130.dp)
                            .width(45.dp)
                    ) {
                        Text(text = list[it].time.drop(11), fontSize = fontSize, modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .weight(1f))
                        SubcomposeAsyncImage(
                            model = "http:${list[it].condition.icon}",
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxSize()
                                .weight(2f)
                        )
                        Text(
                            text = "${list[it].temp_c.roundToInt()}°",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .weight(1f), fontSize = fontSize
                        )

                        Text(
                            text = "${list[it].humidity}%",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .weight(1f), fontSize = fontSize
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyPanel(viewModel: ViewModel){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(30.dp)
    ) {
        Text(
            text = "Daily",
            Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            fontSize = 20.sp
        )
        Divider(thickness = 1.dp, color = Color.LightGray)
        viewModel.forecast?.forecast?.forecastday?.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .height(40.dp)
            ) {
                val dayOfWeek = LocalDate.parse(it.date).dayOfWeek.name.lowercase()[0].uppercase() +
                        LocalDate.parse(it.date).dayOfWeek.name.lowercase().drop(1)
                Text(
                    text = dayOfWeek,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(2f)
                )
                Text(
                    text = "${it.day.avghumidity}%",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                )
                SubcomposeAsyncImage(
                    model = "http:${it.day.condition.icon}",
                    contentDescription = null,
                    loading = {
                        //CircularProgressIndicator()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                )
                Text(
                    text = "${it.day.maxtemp_c}°",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                )
                Text(
                    text = "${it.day.mintemp_c}°",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun AstronomyPanel(viewModel: ViewModel){
    Row {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(130.dp)
                .padding(start = 20.dp, end = 5.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ){
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Icon(
                        painterResource(id = R.drawable.wind),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .weight(2f)
                            .fillMaxSize()
                    )
                    Text(
                        text = "Wind speed",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .weight(0.8f)
                    )
                    Text(
                        text = "${
                            viewModel.forecast?.current?.wind_kph?.roundToInt().toString()
                        } km/h",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .weight(0.8f)
                    )
                }

            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(130.dp)
                .padding(start = 5.dp, end = 20.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ){
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 20.dp, horizontal = 20.dp)
                ) {
                    val date12Format = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                    val date24Format = SimpleDateFormat("HH:mm")

                    if (viewModel.forecast != null){
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Icon(
                                painterResource(id = R.drawable.sunrise),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .weight(2f)
                            )
                            Text(
                                text = "Sunrise",
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .weight(0.8f)
                            )
                            Text(
                                text = date24Format.format(date12Format.parse(viewModel.forecast!!.forecast.forecastday[0].astro.sunrise)!!),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .weight(0.8f)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Icon(
                                painterResource(id = R.drawable.sunset),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .weight(2f)
                            )
                            Text(
                                text = "Sunset",
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .weight(0.8f)
                            )
                            Text(
                                text = date24Format.format(date12Format.parse(viewModel.forecast!!.forecast.forecastday[0].astro.sunset)!!),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .weight(0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onNavigationButtonClick: () -> Unit
){
    TopAppBar(
        title = {
            Text(text = "Weather")
        },
        navigationIcon = {
            IconButton(onClick = onNavigationButtonClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Drawer button"
                )
            }
        }
    )
}

@Composable
fun DrawerContent(
    scope: CoroutineScope,
    viewModel: ViewModel,
    drawerState: DrawerState
){
    LaunchedEffect(Unit){
        viewModel.viewModelScope.launch {
            viewModel.hints = viewModel.readHints()
        }
    }
    ModalDrawerSheet(
        modifier = Modifier
    ) {
        Text("Choose a city", modifier = Modifier.padding(16.dp))
        HorizontalDivider()

        Spacer(Modifier.height(12.dp))
        if (viewModel.hints != null)
        viewModel.hints!!.forEach { item ->
            NavigationDrawerItem(
                icon = {  },
                label = { Text(item.city) },
                selected = item.city == viewModel.forecast?.location?.name,
                onClick = {
                    scope.launch {
                        drawerState.close()
                        viewModel.putCity(item.city)
                        viewModel.updateCity(viewModel.getCity()!!)
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        Button(
            onClick = {
                scope.launch {
                    viewModel.deleteCity()
                }
            },
            Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Exit",
                    Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun CurrentWeatherPanel(
    viewModel: ViewModel
){
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            modifier = Modifier
                .padding(start = 30.dp)
                .align(Alignment.CenterVertically)
                .weight(0.4f),
            text = "${viewModel.forecast?.current?.temp_c?.roundToInt()}°",
            fontSize = 80.sp
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(0.4f),
            text = "${viewModel.forecast?.current?.condition?.text}",
            fontSize = 20.sp,
        )
        SubcomposeAsyncImage(
            model = "http:${viewModel.forecast?.current?.condition?.icon}",
            contentDescription = null,
            modifier = Modifier
                .weight(0.3f)
                .height(100.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun LocationPanel(
    viewModel: ViewModel
){
    Row {
        Icon(
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterVertically),
            imageVector = Icons.Filled.Place,
            contentDescription = null
        )

        Text(
            text = "${viewModel.forecast?.location?.name}, ${viewModel.forecast?.location?.country}",
            modifier = Modifier
                .align(Alignment.Bottom),
            fontSize = 30.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}