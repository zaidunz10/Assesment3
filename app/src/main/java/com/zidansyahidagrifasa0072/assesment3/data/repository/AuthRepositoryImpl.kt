package com.zidansyahidagrifasa0072.assesment3.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.zidansyahidagrifasa0072.assesment3.data.model.User
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AuthRepository {

    override val currentUser: com.google.firebase.auth.FirebaseUser?
        get() = firebaseAuth.currentUser

    override fun registerUser(
        name: String,
        email: String,
        password: String,
        imageUri: Uri?
    ): Flow<AppNetworkState<String>> = flow {
        emit(AppNetworkState.Loading)
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User ID tidak ditemukan")

            var imageUrl = ""
            if (imageUri != null) {
                val storageRef = storage.reference.child("profile_images/$uid.jpg")
                storageRef.putFile(imageUri).await()
                imageUrl = storageRef.downloadUrl.await().toString()
            }

            val user = User(uid = uid, name = name, email = email, profileImageUrl = imageUrl)
            firestore.collection("users").document(uid).set(user).await()

            emit(AppNetworkState.Success("Registrasi Berhasil"))
        } catch (e: Exception) {
            emit(AppNetworkState.Error(e.localizedMessage ?: "Terjadi kesalahan saat registrasi"))
        }
    }

    override fun loginUser(email: String, password: String): Flow<AppNetworkState<String>> = flow {
        emit(AppNetworkState.Loading)
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(AppNetworkState.Success("Login Berhasil"))
        } catch (e: Exception) {
            emit(AppNetworkState.Error(e.localizedMessage ?: "Email atau Password salah"))
        }
    }

    override fun logoutUser() {
        firebaseAuth.signOut()
    }

    override fun getUserProfile(): Flow<AppNetworkState<User>> = flow {
        emit(AppNetworkState.Loading)
        try {
            val uid = firebaseAuth.currentUser?.uid ?: throw Exception("User belum login")
            val snapshot = firestore.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
            if (user != null) {
                emit(AppNetworkState.Success(user))
            } else {
                emit(AppNetworkState.Error("Data user tidak ditemukan"))
            }
        } catch (e: Exception) {
            emit(AppNetworkState.Error(e.localizedMessage ?: "Gagal mengambil data profil"))
        }
    }
}