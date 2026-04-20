package com.frances.spotify.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.frances.spotify.datamodel.Album
import kotlinx.coroutines.flow.Flow

@Dao            // for database access
interface DatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun favoriteAlbum(album: Album)

    @Query("SELECT EXISTS(SELECT * FROM Album WHERE id = :id)")
    fun isFavoriteAlbum(id: Int): Flow<Boolean>

    @Query("SELECT * FROM Album")
    fun fetchFavoriteAlbum(): Flow<List<Album>> // flow: long connection, listen to the database

    @Delete
    fun unFavoriteAlbum(album: Album)
}