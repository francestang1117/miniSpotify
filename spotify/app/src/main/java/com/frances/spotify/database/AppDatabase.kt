package com.frances.spotify.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.frances.spotify.datamodel.Album

@Database(entities = [Album::class], version = 1)       // version for database migration
abstract class AppDatabase: RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}