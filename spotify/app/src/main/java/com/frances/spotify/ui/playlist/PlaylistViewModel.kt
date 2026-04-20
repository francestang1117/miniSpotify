package com.frances.spotify.ui.playlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frances.spotify.datamodel.Album
import com.frances.spotify.datamodel.Song
import com.frances.spotify.repository.FavoriteAlbumRepository
import com.frances.spotify.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val favoriteAlbumRepository: FavoriteAlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistUiState(album = Album.empty()))
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    fun fetchPlaylist(album: Album) {
        _uiState.value = _uiState.value.copy(album = album, isLoading = true)

        // Fetch playlist songs from network
        viewModelScope.launch {
            runCatching { playlistRepository.getPlaylist(album.id) }
                .onSuccess { playlist ->
                    _uiState.value = _uiState.value.copy(
                        playlist = playlist.songs,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Log.e("PlaylistViewModel", "Failed to load playlist", error)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }

        // Observe favourite state from Room — emits automatically on DB changes
        viewModelScope.launch {
            favoriteAlbumRepository.isFavoriteAlbum(album.id).collect { isFav ->
                _uiState.value = _uiState.value.copy(isFavorite = isFav)
            }
        }
    }

    fun toggleFavorite(isFavorite: Boolean) {
        val album = _uiState.value.album
        viewModelScope.launch {
            if (isFavorite) {
                favoriteAlbumRepository.favoriteAlbum(album)
            } else {
                favoriteAlbumRepository.unFavoriteAlbum(album)
            }
        }
    }
}

data class PlaylistUiState(
    val album: Album,
    val isFavorite: Boolean = false,
    val playlist: List<Song> = emptyList(),
    val isLoading: Boolean = false
)