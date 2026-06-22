package com.zidansyahidagrifasa0072.assesment3.data.repository

import android.net.Uri
import com.zidansyahidagrifasa0072.assesment3.data.model.PlaceDetailResponse
import com.zidansyahidagrifasa0072.assesment3.data.model.Review
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getAllReviews(): Flow<AppNetworkState<List<Review>>>
    fun getMyReviews(): Flow<AppNetworkState<List<Review>>>
    fun addReview(placeName: String, description: String, rating: Float, imageUri: Uri): Flow<AppNetworkState<String>>
    fun updateReview(reviewId: String, placeName: String, description: String, rating: Float, newImageUri: Uri?, oldImageUrl: String): Flow<AppNetworkState<String>>
    fun deleteReview(reviewId: String): Flow<AppNetworkState<String>>
    fun searchAndGetPlaceDetail(placeName: String, apiKey: String): Flow<AppNetworkState<PlaceDetailResponse>>
}