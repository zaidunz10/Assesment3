package com.zidansyahidagrifasa0072.assesment3.ui.screen.detail_review

import androidx.lifecycle.ViewModel
import com.zidansyahidagrifasa0072.assesment3.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailReviewViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun isReviewOwner(reviewUserId: String): Boolean {
        return authRepository.currentUser?.uid == reviewUserId
    }
}