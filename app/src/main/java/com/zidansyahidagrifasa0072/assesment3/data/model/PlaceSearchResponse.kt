package com.zidansyahidagrifasa0072.assesment3.data.model

import com.google.gson.annotations.SerializedName

data class PlaceSearchResponse(
    @SerializedName("features") val features: List<PlaceFeature> = emptyList()
)