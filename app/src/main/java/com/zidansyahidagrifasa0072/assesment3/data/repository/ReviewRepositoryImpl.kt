package com.zidansyahidagrifasa0072.assesment3.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.zidansyahidagrifasa0072.assesment3.data.model.PlaceDetailResponse
import com.zidansyahidagrifasa0072.assesment3.data.model.Review
import com.zidansyahidagrifasa0072.assesment3.data.model.User
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import com.zidansyahidagrifasa0072.assesment3.data.network.CloudinaryApi
import com.zidansyahidagrifasa0072.assesment3.data.network.OpenTripMapApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.zidansyahidagrifasa0072.assesment3.util.CloudinaryConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


class ReviewRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val api: OpenTripMapApi,
    private val cloudinaryApi: CloudinaryApi

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
    private suspend fun uploadImageToCloudinary(
        imageUri: Uri
    ): String {

        android.util.Log.d("UPLOAD_DEBUG", "A")

        val inputStream =
            context.contentResolver
                .openInputStream(imageUri)
                ?: throw Exception("Gagal membaca gambar")

        android.util.Log.d("UPLOAD_DEBUG", "B")

        val bytes = inputStream.readBytes()

        android.util.Log.d("UPLOAD_DEBUG", "C size=${bytes.size}")

        val requestBody =
            bytes.toRequestBody(
                "image/jpeg".toMediaType()
            )

        android.util.Log.d("UPLOAD_DEBUG", "D")

        val multipart =
            MultipartBody.Part.createFormData(
                "file",
                "review.jpg",
                requestBody
            )

        android.util.Log.d("UPLOAD_DEBUG", "E")

        val preset =
            "Wisataku".toRequestBody(
                "text/plain".toMediaType()
            )

        android.util.Log.d("UPLOAD_DEBUG", "F")

        val response =
            cloudinaryApi.uploadImage(
                multipart,
                preset
            )

        android.util.Log.d("UPLOAD_DEBUG", "G")

        if (!response.isSuccessful) {
            throw Exception(
                response.errorBody()?.string()
            )
        }

        android.util.Log.d("UPLOAD_DEBUG", "H")

        return response.body()?.secure_url
            ?: throw Exception("Upload gagal")
    }

    override fun addReview(
        placeName: String,
        description: String,
        rating: Float,
        imageUri: Uri
    ): Flow<AppNetworkState<String>> = flow {

        emit(AppNetworkState.Loading)
        try {

            android.util.Log.d("REVIEW_DEBUG", "1. Mulai Add Review")

            val uid = auth.currentUser?.uid
                ?: throw Exception("User tidak valid")

            android.util.Log.d("REVIEW_DEBUG", "2. User OK")

            val userSnapshot =
                firestore.collection("users")
                    .document(uid)
                    .get()
                    .await()

            android.util.Log.d("REVIEW_DEBUG", "3. User Snapshot OK")

            val user =
                userSnapshot.toObject(User::class.java)
                    ?: throw Exception("Profil user tidak ditemukan")

            val reviewId = UUID.randomUUID().toString()

            android.util.Log.d("REVIEW_DEBUG", "4. Upload Cloudinary Mulai")

            val imageUrl =
                uploadImageToCloudinary(imageUri)

            android.util.Log.d(
                "REVIEW_DEBUG",
                "5. Upload Berhasil = $imageUrl"
            )

            val review = Review(
                id = reviewId,
                userId = uid,
                userName = user.name,
                userEmail = user.email,
                userProfileUrl = user.profileImageUrl,
                placeName = placeName,
                description = description,
                rating = rating,
                imageUrl = imageUrl,
                timestamp = System.currentTimeMillis()
            )
            android.util.Log.d(
                "REVIEW_DEBUG",
                "5.2 Review Object Berhasil"
            )

            android.util.Log.d(
                "REVIEW_DEBUG",
                "5.3 Mau Simpan Firestore"
            )

            // Ganti baris .set(review).await() dengan ini sementara untuk debug:
            firestore.collection("reviews")
                .document(reviewId)
                .set(review)
                .addOnSuccessListener {
                    android.util.Log.d("REVIEW_DEBUG", "6. Firestore Berhasil (Callback)")
                }
                .addOnFailureListener { exception ->
                    android.util.Log.e("REVIEW_DEBUG", "Firestore Gagal", exception)
                }

            emit(
                AppNetworkState.Success(
                    "Review berhasil diposting"
                )
            )

            android.util.Log.d(
                "REVIEW_DEBUG",
                "7. SUCCESS EMIT"
            )

        } catch (e: Exception) {

            android.util.Log.e(
                "REVIEW_DEBUG",
                "ERROR",
                e
            )

            emit(
                AppNetworkState.Error(
                    e.localizedMessage
                        ?: "Gagal memposting review"
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    override fun updateReview(
        reviewId: String,
        placeName: String,
        description: String,
        rating: Float,
        newImageUri: Uri?,
        oldImageUrl: String
    ): Flow<AppNetworkState<String>> = callbackFlow { // Ubah jadi callbackFlow agar sinkronisasi aman
        trySend(AppNetworkState.Loading)

        val scope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                var finalImageUrl = oldImageUrl
                if (newImageUri != null) {
                    finalImageUrl = uploadImageToCloudinary(newImageUri)
                }

                val updates = mapOf(
                    "placeName" to placeName,
                    "description" to description,
                    "rating" to rating,
                    "imageUrl" to finalImageUrl,
                    "timestamp" to System.currentTimeMillis()
                )

                firestore.collection("reviews").document(reviewId).update(updates)
                    .addOnSuccessListener {
                        trySend(AppNetworkState.Success("Review berhasil diperbarui"))
                        close() // menutup stream callbackFlow dengan sukses
                    }
                    .addOnFailureListener { exception ->
                        trySend(AppNetworkState.Error(exception.localizedMessage ?: "Gagal memperbarui review"))
                        close(exception)
                    }
            } catch (e: Exception) {
                trySend(AppNetworkState.Error(e.localizedMessage ?: "Gagal memproses gambar"))
                close(e)
            }
        }
        awaitClose { }
    }

    override fun deleteReview(
        reviewId: String
    ): Flow<AppNetworkState<String>> = callbackFlow { // Ubah jadi callbackFlow
        trySend(AppNetworkState.Loading)

        firestore.collection("reviews")
            .document(reviewId)
            .delete()
            .addOnSuccessListener {
                trySend(AppNetworkState.Success("Review berhasil dihapus"))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(AppNetworkState.Error(exception.localizedMessage ?: "Gagal menghapus review"))
                close(exception)
            }

        awaitClose { }
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