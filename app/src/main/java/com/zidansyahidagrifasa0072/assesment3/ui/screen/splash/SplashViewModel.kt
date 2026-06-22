package com.zidansyahidagrifasa0072.assesment3.ui.screen.splash

import androidx.lifecycle.ViewModel
import com.zidansyahidagrifasa0072.assesment3.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return authRepository.currentUser != null
    }
}