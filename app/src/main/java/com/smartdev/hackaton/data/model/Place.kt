package com.smartdev.hackaton.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Place(
    @SerializedName("data")
    val `data`: List<Data>
) {
    @Serializable
    data class Data(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("info")
        val info: String,
        @SerializedName("geo")
        val geo: Geo,
        @SerializedName("photos")
        val photos: List<String>,
        @SerializedName("price")
        val price: Int,
        @SerializedName("categories")
        val categories: Categories
    ) {
        @Serializable
        data class Geo(
            @SerializedName("longitude")
            val longitude: Double,
            @SerializedName("latitude")
            val latitude: Double
        )

        @Serializable
        data class Categories(
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String
        )
    }
}