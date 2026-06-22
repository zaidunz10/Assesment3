package com.zidansyahidagrifasa0072.assesment3.data.model

import com.google.gson.annotations.SerializedName

data class WikipediaExtracts(
    @SerializedName("title") val title: String = "",
    @SerializedName("text") val text: String = ""
)

data class PlacePreview(
    @SerializedName("source") val source: String = "",
    @SerializedName("width") val width: Int = 0,
    @SerializedName("height") val height: Int = 0
)