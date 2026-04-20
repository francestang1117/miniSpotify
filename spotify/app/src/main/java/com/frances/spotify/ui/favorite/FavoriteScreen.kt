package com.frances.spotify.ui.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.frances.spotify.R
import com.frances.spotify.datamodel.Album

@Composable     // fundamental building blocks of an application built with Compose
fun FavoriteScreen(viewModel: FavoriteViewModel, onTap: (Album) -> Unit) {

    // convert viewModel uiState to compose state
    val uiState by viewModel.uiState.collectAsState()
    FavoriteUiScreenContent(albums = uiState.albums, onTap = onTap)
}

@Composable
private fun FavoriteUiScreenContent(albums: List<Album>, onTap: (Album) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = stringResource(id = R.string.menu_favorite),
                style = MaterialTheme.typography.h4,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (albums.isEmpty()) {
            item {
                Text(
                    text = "No favourites yet. Heart an album to save it here.",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }
        } else {
            items(items = albums, key = { it.id }) { album ->
                FavoriteAlbumRow(album = album, onTap = onTap)
            }
        }
    }
}

@Composable
private fun FavoriteAlbumRow(album: Album, onTap: (Album) -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .clickable { onTap(album) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = album.cover,
            contentDescription = "${album.name} cover",
            modifier = Modifier
                .width(60.dp)
                .aspectRatio(1.0f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .weight(1.0f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = album.name,
                style = MaterialTheme.typography.body2,
                color = Color.White
            )

            Text(
                text = stringResource(id = R.string.album_info, album.artists, album.year),
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }
    }
}