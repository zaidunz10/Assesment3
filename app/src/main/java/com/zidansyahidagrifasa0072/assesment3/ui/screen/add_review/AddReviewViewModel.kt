package com.zidansyahidagrifasa0072.assesment3.ui.screen.add_review

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zidansyahidagrifasa0072.assesment3.data.model.PlaceDetailResponse
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import com.zidansyahidagrifasa0072.assesment3.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _addReviewState = MutableStateFlow<AppNetworkState<String>?>(null)
    val addReviewState: StateFlow<AppNetworkState<String>?> = _addReviewState

    private val _apiPlaceState = MutableStateFlow<AppNetworkState<PlaceDetailResponse>?>(null)
    val apiPlaceState: StateFlow<AppNetworkState<PlaceDetailResponse>?> = _apiPlaceState

    // API Key OpenTripMap (Ganti dengan API Key milikmu sendiri)
    private val apiKey = "5ae2e3f221c38a28845f05b6b6cb908075f8f8303fdf5b11c97f482d"

    fun searchPlaceInfo(placeName: String) {
        if (placeName.isBlank()) return
        viewModelScope.launch {
            reviewRepository.searchAndGetPlaceDetail(placeName, apiKey).collect { state ->
                _apiPlaceState.value = state
            }
        }
    }

    fun addReview(placeName: String, description: String, rating: Float, imageUri: Uri?) {
        if (placeName.isBlank() || description.isBlank() || imageUri == null) {
            _addReviewState.value = AppNetworkState.Error("Nama tempat, deskripsi, dan foto wajib diisi")
            return
        }
        viewModelScope.launch {
            reviewRepository.addReview(placeName, description, rating, imageUri).collect { state ->
                _addReviewState.value = state
            }
        }
    }

    fun resetState() {
        _addReviewState.value = null
        _apiPlaceState.value = null
    }
}