package com.frances.spotify.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.frances.spotify.datamodel.Album
import com.frances.spotify.datamodel.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer
): ViewModel(), Player.Listener {
    // viewModel to provide player state
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        // this -> PlayerViewModel
        // the player will check its state itself, and then tell the registered listener: viewmodel
        exoPlayer.addListener(this)
        // create a coroutine flow
        viewModelScope.launch {
            // cold flow: means the block is called every time a terminal operator is applied to the resulting flow
            flow {
                while (true) {
                    if (exoPlayer.isPlaying) {
                        // update the playerUiState's currentMs and durationMs
                        // to: means Pair<A, B>
                        emit(exoPlayer.currentPosition to exoPlayer.duration)
                    }
                    delay(1000)
                }
            }.collect {
                // Accepts the given collector and emits values into it
                _uiState.value = uiState.value.copy(currentMs = it.first, durationMs = it.second)
                Log.d("SpotifyPlayer", "CurrentMs: ${it.first}, DurationMs: ${it.second}")
            }
        }
    }

    // When the viewmodel is destroyed, then we have to remove
    // If not remove, will cause circular dependency, viewmodel depends on listener,
    // listener depends on viewmodel
    // -> memory leak, java do garbage collection
    // Because first create exoplayer, then viewmodel, and garbage first destroy viewmodel, then
    // destroy exoplayer,
    // If circular dependency, then the garbage could not destroy viewmodel and exoplayer
    override fun onCleared() {
        exoPlayer.removeListener(this)
        super.onCleared()
    }

    // Track player's playing state and error message
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Log.d("SpotifyPlayer", isPlaying.toString())
        _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Log.d("SpotifyPlayer", error.toString())
    }


    fun load(song: Song, album: Album) {
        _uiState.value = PlayerUiState(album, song)

        val mediaItem = MediaItem.Builder().setUri(song.src).build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun play() {
        // If _uiState.value = _uiState. could write this, but not good
        // 播放器的真正播放并不取决于用户的操作，因为还有network等问题，最终它取决于播放器内部的状态，所以将states和这个play bind到一起
        // 不是一个好选择
        // We have to let player to show its state
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun seekTo(positionMs: Long) {
        // Optimistically update
        _uiState.value = uiState.value.copy(
            currentMs = positionMs
        )
        exoPlayer.seekTo(positionMs)
    }
}

// The initial state
data class PlayerUiState(
    val album: Album? = null,
    val song: Song? = null,
    val isPlaying: Boolean = false,
    val currentMs: Long = 0,
    val durationMs: Long = 0
)
