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
import kotlinx.coroutines.flow.first

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
            _editState.value = AppNetworkState.Loading
            try {
                // Menggunakan .first() memastikan kita hanya mengambil hasil akhir (Success/Error)
                // lalu coroutine langsung SELESAI, tidak menggantung!
                val result = reviewRepository.updateReview(reviewId, placeName, description, rating, newImageUri, oldImageUrl).first { state ->
                    state is AppNetworkState.Success || state is AppNetworkState.Error
                }
                _editState.value = result
            } catch (e: Exception) {
                _editState.value = AppNetworkState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            _deleteState.value = AppNetworkState.Loading
            try {
                // Sama seperti edit, ambil emission pertama yang berupa Success atau Error
                val result = reviewRepository.deleteReview(reviewId).first { state ->
                    state is AppNetworkState.Success || state is AppNetworkState.Error
                }
                _deleteState.value = result
            } catch (e: Exception) {
                _deleteState.value = AppNetworkState.Error(e.localizedMessage ?: "Gagal menghapus review")
            }
        }
    }

    fun resetState() {
        _editState.value = null
        _deleteState.value = null
    }
}