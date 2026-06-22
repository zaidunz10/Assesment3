package com.zidansyahidagrifasa0072.assesment3.data.model

import com.google.gson.annotations.SerializedName

data class PlaceDetailResponse(
    @SerializedName("xid") val xid: String,
    @SerializedName("name") val name: String,
    @SerializedName("wikipedia_extracts") val wikipediaExtracts: WikipediaExtracts? = null,
    @SerializedName("preview") val preview: PlacePreview? = null,
    @SerializedName("kinds") val kinds: String = ""
)