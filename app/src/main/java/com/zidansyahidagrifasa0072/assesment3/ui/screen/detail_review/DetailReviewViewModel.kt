package com.zidansyahidagrifasa0072.assesment3.ui.screen.detail_review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zidansyahidagrifasa0072.assesment3.data.repository.AuthRepository
import com.zidansyahidagrifasa0072.assesment3.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailReviewViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    fun isReviewOwner(reviewUserId: String): Boolean {
        return authRepository.currentUser?.uid == reviewUserId
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            reviewRepository.deleteReview(reviewId)
                .collect {
                }
        }
    }

}
