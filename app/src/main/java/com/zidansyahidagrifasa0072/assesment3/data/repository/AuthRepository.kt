package com.zidansyahidagrifasa0072.assesment3.data.repository

import android.net.Uri
import com.zidansyahidagrifasa0072.assesment3.data.model.User
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: com.google.firebase.auth.FirebaseUser?
    fun registerUser(name: String, email: String, password: String, imageUri: Uri?): Flow<AppNetworkState<String>>
    fun loginUser(email: String, password: String): Flow<AppNetworkState<String>>
    fun logoutUser()
    fun getUserProfile(): Flow<AppNetworkState<User>>
}