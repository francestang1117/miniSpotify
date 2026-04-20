package com.frances.spotify

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import coil.Coil
import coil.ImageLoader
import okhttp3.OkHttpClient

@HiltAndroidApp     // Let hilt know the entrance of the app is the MainApplication and then register main application in AndroidManifest xml file
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        val imageLoader = ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .build()

        Coil.setImageLoader(imageLoader)
    }
}
