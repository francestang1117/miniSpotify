package com.frances.spotify;

import android.content.Intent
import android.os.Bundle
import android.window.SplashScreen
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.frances.spotify.splash.HighEnergySplashScreen

class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splash = ComposeView(this).apply {
            setContent {
                HighEnergySplashScreen (
                    onFinished = {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    },
                    title = "MiniSpotify",
                    subtitle = "Your music, your way."
                )
            }
        }

        setContentView(splash)
    }
}