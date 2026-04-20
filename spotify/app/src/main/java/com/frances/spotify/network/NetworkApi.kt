package com.frances.spotify.network

import com.frances.spotify.datamodel.Playlist
import com.frances.spotify.datamodel.Section
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NetworkApi {
    // call endpoint: xxxx:8080/feed, restful: GET
    @GET("/feed")
    fun getHomeFeed(): Call<List<Section>>

    @GET("/playlist/{id}")
    fun getPlaylist(@Path("id") id: Int): Call<Playlist>
}