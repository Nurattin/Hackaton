package com.smartdev.hackaton.data.network_layer

import com.smartdev.hackaton.data.model.Place
import com.smartdev.hackaton.data.model.TourDetail
import com.smartdev.hackaton.data.model.Tours
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("tour/{id}")
    suspend fun getDetailTour(
        @Path("id") id: Int
    ): TourDetail

    @POST("tour/list")
    suspend fun getAllTours(): Tours
}