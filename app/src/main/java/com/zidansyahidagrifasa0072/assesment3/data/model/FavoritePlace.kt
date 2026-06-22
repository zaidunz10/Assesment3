package com.zidansyahidagrifasa0072.assesment3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_places")
data class FavoritePlace(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Double,
    val addedAt: Long = System.currentTimeMillis()
)