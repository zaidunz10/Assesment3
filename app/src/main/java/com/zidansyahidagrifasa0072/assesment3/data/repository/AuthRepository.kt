package com.zidansyahidagrifasa0072.assesment3.data.repository

import com.zidansyahidagrifasa0072.assesment3.data.model.User
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: com.google.firebase.auth.FirebaseUser?
    fun signInWithGoogle(idToken: String): Flow<AppNetworkState<String>>
    fun logoutUser()
    fun getUserProfile(): Flow<AppNetworkState<User>>
    fun isLoggedIn(): Boolean
}