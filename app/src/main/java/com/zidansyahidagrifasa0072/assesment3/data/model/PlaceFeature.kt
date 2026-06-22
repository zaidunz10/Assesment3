package com.zidansyahidagrifasa0072.assesment3.data.model

import com.google.gson.annotations.SerializedName

data class PlaceFeature(
    @SerializedName("type") val type: String = "",
    @SerializedName("properties") val properties: PlaceProperties