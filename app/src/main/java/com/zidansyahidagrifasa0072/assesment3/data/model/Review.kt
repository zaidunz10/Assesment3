package com.zidansyahidagrifasa0072.assesment3.data.model

data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userProfileUrl: String = "",
    val placeName: String = "",
    val description: String = "",
    val rating: Float = 0f,
    val imageUrl: String = "",
    val cloudinaryPublicId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)