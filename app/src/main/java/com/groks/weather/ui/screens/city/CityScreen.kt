package com.groks.weather.ui.screens.city

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.groks.weather.viewmodel.ViewModel
import kotlinx.coroutines.launch

@Composable
fun CityScreen(viewModel: ViewModel) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            SearchField(
                viewModel = viewModel,
                onSearch = {
                    viewModel.getCities(viewModel.searchRequestString)
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            item {
                Text("Choose a city", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
            }
            viewModel.viewModelScope.launch {
                viewModel.hints = viewModel.readHints()
                if (viewModel.hints!!.size > 4) viewModel.deleteHint()
            }

            if (viewModel.citiesRoot != null && viewModel.hints != null){
                items(viewModel.citiesRoot!!.size) {
                    NavigationDrawerItem(
                        label = {
                            Text(text = "${viewModel.citiesRoot!![it].name}, ${viewModel.citiesRoot!![it].country}")
                        },
                        selected = false,
                        onClick = {
                            viewModel.viewModelScope.launch {
                                viewModel.putCity(city = viewModel.citiesRoot!![it].name)
                            }
                        }
                    )
                }
            }

            if (viewModel.hints != null && viewModel.citiesRoot == null)
            items(viewModel.hints!!.size) {
                NavigationDrawerItem(
                    label = {
                        Text(text = viewModel.hints!![it].city)
                    },
                    selected = false,
                    onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.putCity(city = viewModel.hints!![it].city)
                        }
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    viewModel: ViewModel,
    onSearch: () -> Unit
){
    var active by remember { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = {
            Text(text = "Search")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },

        trailingIcon = {
            if(active){
                IconButton(onClick = {
                    if (viewModel.searchRequestString.isNotEmpty())
                        viewModel.updateSearchRequestString("")
                    else {
                        active = false
                        viewModel.citiesRoot = null
                    }
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                }
            }

        },
        query = viewModel.searchRequestString,
        onQueryChange = { newText ->
            viewModel.updateSearchRequestString(newText)
            onSearch()
        },
        onSearch = {
            active = false
            onSearch()
        },
        active = active,
        onActiveChange = {
            active = it
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if(viewModel.citiesRoot != null){
                items(viewModel.citiesRoot!!.size) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable {
                            viewModel.viewModelScope.launch {
                                viewModel.putCity(viewModel.citiesRoot!![it].name)
                                viewModel.updateCity(viewModel.getCity()!!)
                                viewModel.addHintItem(viewModel.citiesRoot!![it].name)
                            }
                        },
                        contentAlignment = Alignment.CenterStart
                    ){
                        Row {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "History Icon",
                                modifier = Modifier
                                    .padding(end = 10.dp, start = 16.dp)
                            )

                            Text(text = viewModel.citiesRoot!![it].name)
                        }
                    }
                }
            }
        }
    }
}