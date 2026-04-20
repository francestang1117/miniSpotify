package com.frances.spotify.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frances.spotify.datamodel.Album
import com.frances.spotify.repository.FavoriteAlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteAlbumRepository: FavoriteAlbumRepository
): ViewModel() {

    private val _uiState = MutableStateFlow((FavoriteUiState(emptyList())))
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        // Room Flow emits a fresh list automatically whenever favourites change —
        // no manual refresh needed.
        viewModelScope.launch {
            favoriteAlbumRepository.fetchFavoriteAlbums().collect { albums ->
                _uiState.value = FavoriteUiState(albums)
            }
        }
    }
}

data class FavoriteUiState(
    val albums: List<Album>
)