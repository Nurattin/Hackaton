package com.smartdev.hackaton.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tours(
    @SerializedName("data")
    val `data`: List<Data>
) {
    @Serializable
    data class Data(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("price")
        val price: Int,
        @SerializedName("photos")
        val photos: List<String>,
        @SerializedName("duration")
        val duration: String,
        @SerializedName("categories")
        val categories: Categories
    ) {
        @Serializable
        data class Categories(
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String
        )
    }
}