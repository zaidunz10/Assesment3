package com.zidansyahidagrifasa0072.assesment3.data.network

import com.zidansyahidagrifasa0072.assesment3.data.model.CloudinaryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CloudinaryApi {

    @Multipart
    @POST("v1_1/dpldg0fh2/image/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody
    ): retrofit2.Response<CloudinaryResponse>

    @FormUrlEncoded
    @POST("v1_1/dpldg0fh2/image/destroy")
    suspend fun deleteImage(
        @Field("public_id") publicId: String,
        @Field("api_key") apiKey: String,
        @Field("timestamp") timestamp: Long,
        @Field("signature") signature: String
    ): ResponseBody
}