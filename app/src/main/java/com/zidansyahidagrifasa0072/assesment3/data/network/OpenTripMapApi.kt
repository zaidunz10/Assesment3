package com.zidansyahidagrifasa0072.assesment3.data.network

import com.zidansyahidagrifasa0072.assesment3.data.model.PlaceDetailResponse
import com.zidansyahidagrifasa0072.assesment3.data.model.PlaceSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenTripMapApi {
    @GET("en/places/geoname")
    suspend fun searchPlaceByName(
        @Query("name") name: String,
        @Query("apikey") apiKey: String
    ): PlaceSearchResponse

    @GET("en/places/xid/{xid}")
    suspend fun getPlaceDetail(
        @Path("xid") xid: String,
        @Query("apikey") apiKey: String
    ): PlaceDetailResponse
}