package com.frances.spotify.datamodel

import com.google.gson.annotations.SerializedName

data class Section(
    @SerializedName("section_title")
    val title: String,
    val albums: List<Album>
)
