package com.zidansyahidagrifasa0072.assesment3.module

import com.zidansyahidagrifasa0072.assesment3.data.network.OpenTripMapApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.zidansyahidagrifasa0072.assesment3.data.network.CloudinaryApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenTripMapApi(okHttpClient: OkHttpClient): OpenTripMapApi {
        return Retrofit.Builder()
            .baseUrl("https://api.opentripmap.com/0.1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenTripMapApi::class.java)
    }
    @Provides
    @Singleton
    fun provideCloudinaryApi(
        okHttpClient: OkHttpClient
    ): CloudinaryApi {

        return Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }
}
