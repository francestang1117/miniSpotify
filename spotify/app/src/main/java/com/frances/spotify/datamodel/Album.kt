package com.frances.spotify.datamodel

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity     // make the Album class to be Room Entity
data class Album(
    @PrimaryKey
    val id: Int,
    @SerializedName("album")        // name in json
    val name: String,
    val year: String,
    val cover: String,
    val artists: String,
    val description: String
): Serializable {
    companion object {  // static
        fun empty(): Album {
            return Album(
                id = -1,
                "",
                "",
                "",
                "",
                "",
            )
        }
    }
}
