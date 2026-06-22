package com.zidansyahidagrifasa0072.assesment3.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.zidansyahidagrifasa0072.assesment3.data.model.User
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: com.google.firebase.auth.FirebaseUser?
        get() = firebaseAuth.currentUser

    override fun signInWithGoogle(idToken: String): Flow<AppNetworkState<String>> = flow {
        emit(AppNetworkState.Loading)
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Gagal mendapatkan data user dari Google")

            // Ambil data profil dari akun google
            val uid = firebaseUser.uid
            val name = firebaseUser.displayName ?: "User WisataKu"
            val email = firebaseUser.email ?: ""
            val profileImageUrl = firebaseUser.photoUrl?.toString() ?: ""

            val user = User(uid = uid, name = name, email = email, profileImageUrl = profileImageUrl)

            // Simpan atau update ke Firestore secara otomatis saat login
            firestore.collection("users").document(uid).set(user).await()

            emit(AppNetworkState.Success("Login Google Berhasil"))
        } catch (e: Exception) {
            emit(AppNetworkState.Error(e.localizedMessage ?: "Terjadi kesalahan saat masuk dengan Google"))
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