package com.frances.spotify.repository

import com.frances.spotify.database.DatabaseDao
import com.frances.spotify.datamodel.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Inject the DatabaseDao
class FavoriteAlbumRepository @Inject constructor(private val databaseDao: DatabaseDao) {

    // change the context where the flow is executed
    fun isFavoriteAlbum(id: Int): Flow<Boolean> = databaseDao.isFavoriteAlbum(id).flowOn(Dispatchers.IO)

    fun fetchFavoriteAlbums(): Flow<List<Album>> = databaseDao.fetchFavoriteAlbum().flowOn(Dispatchers.IO)

    suspend fun favoriteAlbum(album: Album) = withContext(Dispatchers.IO) {
        databaseDao.favoriteAlbum(album)
    }

    suspend fun unFavoriteAlbum(album: Album) = withContext(Dispatchers.IO) {
        databaseDao.unFavoriteAlbum(album)
    }
}