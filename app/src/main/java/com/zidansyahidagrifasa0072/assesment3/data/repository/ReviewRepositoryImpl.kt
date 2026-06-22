package com.zidansyahidagrifasa0072.assesment3.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.zidansyahidagrifasa0072.assesment3.data.model.PlaceDetailResponse
import com.zidansyahidagrifasa0072.assesment3.data.model.Review
import com.zidansyahidagrifasa0072.assesment3.data.model.User
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import com.zidansyahidagrifasa0072.assesment3.data.network.OpenTripMapApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val api: OpenTripMapApi
) : ReviewRepository {

    override fun getAllReviews(): Flow<AppNetworkState<List<Review>>> = callbackFlow {
        trySend(AppNetworkState.Loading)
        val listener = firestore.collection("reviews")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(AppNetworkState.Error(error.localizedMessage ?: "Gagal memuat review"))
                    return@addSnapshotListener
                }
                val reviews = snapshot?.toObjects(Review::class.java) ?: emptyList()
                trySend(AppNetworkState.Success(reviews))
            }
        awaitClose { listener.remove() }
    }

    override fun getMyReviews(): Flow<AppNetworkState<List<Review>>> = callbackFlow {
        trySend(AppNetworkState.Loading)
        val uid = auth.currentUser?.uid ?: ""
        val listener = firestore.collection("reviews")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(AppNetworkState.Error(error.localizedMessage ?: "Gagal memuat review milikmu"))
                    return@addSnapshotListener
                }
                val reviews = snapshot?.toObjects(Review::class.java) ?: emptyList()
                val sortedReviews = reviews.sortedByDescending { it.timestamp }
                trySend(AppNetworkState.Success(sortedReviews))
            }
        awaitClose { listener.remove() }
    }

    override fun addReview(placeName: String, description: String, rating: Float, imageUri: Uri): Flow<AppNetworkState<String>> = flow {
        emit(AppNetworkState.Loading)
        try {
            val uid = auth.currentUser?.uid ?: throw Exception("User tidak valid")
            val userSnapshot = firestore.collection("users").document(uid).get().await()
            val user = userSnapshot.toObject(User::class.java) ?: throw Exception("Profil user tidak ditemukan")

            val reviewId = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("review_images/$reviewId.jpg")
            storageRef.putFile(imageUri).await()
            val imageUrl = storageRef.downloadUrl.await().toString()

            val review = Review(
                id = reviewId,
                userId = uid,
                userName = user.name,
                userEmail = user.email,
                userProfileUrl = user.profileImageUrl,
                placeName = placeName,
                description = description,
                rating = rating,
                imageUrl = imageUrl
            )

            firestore.collection("reviews").document(reviewId).set(review).await()
            emit(AppNetworkState.Success("Review berhasil diposting"))
        } catch (e: Exception) {
            emit(AppNetworkState.Error(e.localizedMessage ?: "Gagal memposting review"))
        }
    }

    override fun updateReview(
        reviewId: String,
        placeName: String,
        description: String,
        rating: Float,
        newImageUri: Uri?,
        oldImageUrl: String
    ): Flow<AppNetworkState<String>> = flow {
        emit(AppNetworkState.Loading)
        try {
            var finalImageUrl = oldImageUrl
            if (newImageUri != null) {
                val storageRef = storage.reference.child("review_images/$reviewId.jpg")
                storageRef.putFile(newImageUri).await()
                finalImageUrl = storageRef.downloadUrl.await().toString()
            }

            val updates = mapOf(
                "placeName" to placeName,
                "description" to description,
                "rating" to rating,
                "imageUrl" to finalImageUrl,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("reviews").document(reviewId).update(updates).await()
            emit(AppNetworkState.Success("Review berhasil diperbarui"))
        } catch (e: Exception) {
            emit(AppNetworkState.Error(e.localizedMessage ?: "Gagal memperbarui review"))
        }
    }

    override fun deleteReview(reviewId: String): Flow<AppNetworkState<String>> = flow {
        emit(AppNetworkState.Loading)
        try {
            firestore.collection("reviews").document(reviewId).delete().await()
            try {
                storage.reference.child("review_images/$reviewId.jpg").delete().await()
            } catch (_: Exception) {}
            emit(AppNetworkState.Success("Review berhasil dihapus"))
        } catch (e: Exception) {
            emit(AppNetworkState.Error(e.localizedMessage ?: "Gagal menghapus review"))
        }
    }

    override fun searchAndGetPlaceDetail(placeName: String, apiKey: String): Flow<AppNetworkState<PlaceDetailResponse>> = flow {
        emit(AppNetworkState.Loading)
        try {
            val searchResponse = api.searchPlaceByName(placeName, apiKey)
            if (searchResponse.features.isNotEmpty()) {
                val xid = searchResponse.features.first().properties.xid
                val detailResponse = api.getPlaceDetail(xid, apiKey)
                emit(AppNetworkState.Success(detailResponse))
            } else {
                emit(AppNetworkState.Error("Tempat wisata tidak ditemukan di OpenTripMap"))
            }
        } catch (e: Exception) {
            emit(AppNetworkState.Error("Gagal memuat info API: ${e.localizedMessage}"))
        }
    }
}