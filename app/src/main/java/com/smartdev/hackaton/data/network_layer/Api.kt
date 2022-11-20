package com.smartdev.hackaton.data.network_layer

import com.smartdev.hackaton.data.model.CategoryDto
import com.smartdev.hackaton.data.model.TourDetail
import com.smartdev.hackaton.data.model.Tours
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @GET("tour/{id}")
    suspend fun getDetailTour(
        @Path("id") id: Int
    ): TourDetail

    @POST("tour/list")
    suspend fun getAllTours(): Tours


    @GET("tour/get-categories")
    suspend fun getCategory(): CategoryDto

    @GET("place/get-categories")
    suspend fun getCategoryForPlace(): CategoryDto
}