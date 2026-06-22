package com.zidansyahidagrifasa0072.assesment3.ui.screen.edit_review

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import com.zidansyahidagrifasa0072.assesment3.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _editState = MutableStateFlow<AppNetworkState<String>?>(null)
    val editState: StateFlow<AppNetworkState<String>?> = _editState

    private val _deleteState = MutableStateFlow<AppNetworkState<String>?>(null)
    val deleteState: StateFlow<AppNetworkState<String>?> = _deleteState

    fun updateReview(reviewId: String, placeName: String, description: String, rating: Float, newImageUri: Uri?, oldImageUrl: String) {
        if (placeName.isBlank() || description.isBlank()) {
            _editState.value = AppNetworkState.Error("Nama tempat dan deskripsi tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            reviewRepository.updateReview(reviewId, placeName, description, rating, newImageUri, oldImageUrl).collect { state ->
                _editState.value = state
            }
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            reviewRepository.deleteReview(reviewId).collect { state ->
                _deleteState.value = state
            }
        }
    }

    fun resetState() {
        _editState.value = null
        _deleteState.value = null
    }
}