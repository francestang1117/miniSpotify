package com.frances.spotify.ui.playlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.frances.spotify.R
import com.frances.spotify.datamodel.Album
import com.frances.spotify.datamodel.Song
import com.frances.spotify.player.PlayerUiState
import com.frances.spotify.player.PlayerViewModel

@Composable
fun PlaylistScreen(playlistViewModel: PlaylistViewModel, playerViewModel: PlayerViewModel) {
    val playlistUiState by playlistViewModel.uiState.collectAsState()
    val playerUiState by playerViewModel.uiState.collectAsState()

    PlaylistScreenContent(
        playlistUiState = playlistUiState,
        playerUiState = playerUiState,
        onTapFavorite = {
            playlistViewModel.toggleFavorite(it)
        },

        // Once click the song, the album will be loaded
        onTapSong = {
            // Once load and play, the player ui state will be updated
            playerViewModel.load(it, playlistUiState.album)
            // play the song
            playerViewModel.play()
        }
    )
}

@Composable
private fun PlaylistScreenContent(
    playlistUiState: PlaylistUiState,
    onTapFavorite: (Boolean) -> Unit,
    playerUiState: PlayerUiState,
    onTapSong: (Song) -> Unit
) {
    // The cover component
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Cover(
            album = playlistUiState.album,
            isFavorite = playlistUiState.isFavorite,
            onTapFavorite = onTapFavorite
        )

        // Add playlist header
        PlaylistHeader(album = playlistUiState.album)

        when {
            playlistUiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Green)
                }
            }
            playlistUiState.playlist.isEmpty() -> {
                Text(
                    text = "No tracks available.",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            else -> {
                // Add the playlist content
                // After the song is chosen, we can get the song from the state
                PlaylistContent(
                    playlist = playlistUiState.playlist,
                    currentSong = playerUiState.song,
                    onTapSong = onTapSong
                )
            }
        }
    }
}

@Composable
private fun PlaylistContent(
    playlist: List<Song>,
    currentSong: Song?,
    onTapSong: (Song) -> Unit
) {
    val stateState = rememberLazyListState()
    LazyColumn(state = stateState) {
        // Use src as the stable key — it is the natural unique identifier
        // For a song since each track has a distinct audio URL.
        // This allows Compose to efficiently diff the list without a surrogate id
        items(playlist, key = { it.src }) { song ->
            SongRow(
                song = song,
                // Compare by src (audio URL) — unique per song, no id field needed
                isPlaying = currentSong?.src == song.src,
                onTapSong = onTapSong
            )
        }
        
        item { 
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SongRow(song: Song, isPlaying: Boolean, onTapSong: (Song) -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { onTapSong(song) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                text = song.name,
                style = MaterialTheme.typography.body2,
                color = if (isPlaying) Color.Green else Color.White
            )

            Text(
                text = song.lyric,
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        } 
        
        Text(
            text = song.length,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.body2,
            color = Color.LightGray
        )
    }
}

@Composable
private fun PlaylistHeader(album: Album) {
    Column {
        Text(
            text = album.name,
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 16.dp),
            color = Color.White
        )

        Text(
            text = stringResource(id = R.string.album_info, album.artists, album.year),
            style = MaterialTheme.typography.body2,
            color = Color.LightGray
        )
    }
}

@Composable
private fun Cover(
    album: Album,
    isFavorite: Boolean,
    onTapFavorite: (Boolean) -> Unit        // add onTapFavorite function
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                // Place the favorite icon on the right upper corner of the screen
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onTapFavorite(!isFavorite) },
                painter = painterResource(
                    id = if (isFavorite) R.drawable.ic_favorite_24
                    else R.drawable.ic_unfavorite_24
                ),
                tint = if (isFavorite) Color.Green else Color.Gray,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1.0f)
                    .align(Alignment.Center)
            ) {
                // Background
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.vinyl_background),
                    contentDescription = null
                )

                AsyncImage(
                    model = album.cover,
                    contentDescription = "${album.name} cover",
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .aspectRatio(1.0f)
                        .align(Alignment.Center)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds
                )
            }
        }

        // Add the description text under the image
        Text(
            text = album.description,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.caption,
            color = Color.LightGray
        )
    }
}