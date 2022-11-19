package com.smartdev.hackaton.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TourDetail(
    @SerialName("data")
    val `data`: Data,
    @SerialName("status")
    val status: Boolean
) {
    @Serializable
    data class Data(
        @SerialName("id")
        val id: Int,
        @SerialName("name")
        val name: String,
        @SerialName("info")
        val info: String,
        @SerialName("price")
        val price: Int,
        @SerialName("photos")
        val photos: List<String>,
        @SerialName("date_start")
        val dateStart: String,
        @SerialName("duration")
        val duration: String,
        @SerialName("places")
        val places: List<Place>,
        @SerialName("categories")
        val categories: Categories
    ) {
        @Serializable
        data class Place(
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String,
            @SerialName("info")
            val info: String,
            @SerialName("geo")
            val geo: Geo,
            @SerialName("categories")
            val categories: Categories
        ) {
            @Serializable
            data class Geo(
                @SerialName("latitude")
                val latitude: Double,
                @SerialName("longitude")
                val longitude: Double
            )

            @Serializable
            data class Categories(
                @SerialName("id")
                val id: Int,
                @SerialName("name")
                val name: String
            )
        }

        @Serializable
        data class Categories(
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String
        )
    }
}