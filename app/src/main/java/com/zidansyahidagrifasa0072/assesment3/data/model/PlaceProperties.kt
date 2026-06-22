package com.zidansyahidagrifasa0072.assesment3.data.model

import com.google.gson.annotations.SerializedName

data class PlaceProperties(
    @SerializedName("xid") val xid: String,
    @SerializedName("name") val name: String,
    @SerializedName("dist") val dist: Double = 0.0,
    @SerializedName("kinds") val kinds: String = ""
)