package com.frances.spotify.repository

import android.util.Log
import com.frances.spotify.datamodel.Section
import com.frances.spotify.network.NetworkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

// Constructor injection: 1. when new a home repository, the network api can be used, 2. do not need to use @provide
class HomeRepository @Inject constructor(
    private val networkApi: NetworkApi
) {

    // Issue: main safe, directly call execute, let get response be executed in other thread
    // Suspend: make sure the main thread safe, to force the call in the coroutine scope, then it can be
    // used in other thread
    suspend fun getHomeSections(): List<Section> = withContext(Dispatchers.IO) {
        // Mock the slow network service
        // 3s delay
        delay(3000)
        networkApi.getHomeFeed().execute().body() ?: listOf()
    }
}