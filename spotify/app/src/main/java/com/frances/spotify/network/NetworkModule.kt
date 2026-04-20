package com.frances.spotify.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module     // define a factory
@InstallIn(SingletonComponent::class)       // the factory should be installed in a scope,
object NetworkModule {      // static

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides           // the factory could automatically execute the retrofit method
    @Singleton          // only the first time this method will execute and create a new instance, the next time this instance will be used and will not create a new one
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()
    }

    @Provides           // network api requires retrofit, then retrofit is built from the provideRetrofit() method
    @Singleton
    fun provideNetworkApi(retrofit: Retrofit): NetworkApi {
        return retrofit.create(NetworkApi::class.java)
    }
}