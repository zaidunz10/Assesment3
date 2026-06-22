package com.zidansyahidagrifasa0072.assesment3.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zidansyahidagrifasa0072.assesment3.data.model.Review
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import com.zidansyahidagrifasa0072.assesment3.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _reviewsState = MutableStateFlow<AppNetworkState<List<Review>>>(AppNetworkState.Loading)
    val reviewsState: StateFlow<AppNetworkState<List<Review>>> = _reviewsState

    init {
        getAllReviews()
    }

    fun getAllReviews() {
        viewModelScope.launch {
            reviewRepository.getAllReviews().collect { state ->
                _reviewsState.value = state
            }
        }
    }
}