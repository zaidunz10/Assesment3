package com.zidansyahidagrifasa0072.assesment3.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zidansyahidagrifasa0072.assesment3.data.model.Review
import com.zidansyahidagrifasa0072.assesment3.data.model.User
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import com.zidansyahidagrifasa0072.assesment3.data.repository.AuthRepository
import com.zidansyahidagrifasa0072.assesment3.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<AppNetworkState<User>>(AppNetworkState.Loading)
    val profileState: StateFlow<AppNetworkState<User>> = _profileState

    private val _myReviewsState = MutableStateFlow<AppNetworkState<List<Review>>>(AppNetworkState.Loading)
    val myReviewsState: StateFlow<AppNetworkState<List<Review>>> = _myReviewsState

    init {
        loadProfileAndReviews()
    }

    fun loadProfileAndReviews() {
        viewModelScope.launch {
            authRepository.getUserProfile().collect { state ->
                _profileState.value = state
            }
        }
        viewModelScope.launch {
            reviewRepository.getMyReviews().collect { state ->
                _myReviewsState.value = state
            }
        }
    }

    fun logout() {
        authRepository.logoutUser()
    }
}