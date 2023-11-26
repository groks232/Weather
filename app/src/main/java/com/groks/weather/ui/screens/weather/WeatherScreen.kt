package com.groks.weather.ui.screens.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.groks.weather.viewmodel.ViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeatherScreen(viewModel: ViewModel){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isConnectionError by viewModel.connectionState
    val hasReachedMaxRetry by viewModel.hasReachedMaxRetry
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val city by viewModel.cityState

    LaunchedEffect(city) {
        viewModel.getData()
    }

    val pullRefreshState =
        rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = viewModel::refresh
        )

    if (isConnectionError) {
        Box(modifier = Modifier.fillMaxSize()){
            Column(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = "Connection problems",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 15.dp)
                )
                if (!hasReachedMaxRetry)
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                else
                    IconButton(
                        onClick = {
                            viewModel.getData()
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Drawer button"
                        )
                    }
            }
        }
    } else {
        if (viewModel.forecast != null){
            ModalNavigationDrawer(
                drawerContent = {
                    DrawerContent(
                        scope = scope,
                        viewModel = viewModel,
                        drawerState = drawerState
                    )
                },
                drawerState = drawerState
            ) {
                Scaffold(
                    modifier = Modifier
                        .pullRefresh(pullRefreshState),
                    topBar = {
                        TopBar(
                            onNavigationButtonClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()){
                            Column(
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                CurrentWeatherPanel(viewModel = viewModel)

                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    LocationPanel(viewModel = viewModel)

                                    HourlyPanel(viewModel)

                                    DailyPanel(viewModel)

                                    AstronomyPanel(viewModel)
                                }
                            }

                            PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
                        }
                    }
                }
            }
        }
        else {
            Box(modifier = Modifier.fillMaxSize()){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}