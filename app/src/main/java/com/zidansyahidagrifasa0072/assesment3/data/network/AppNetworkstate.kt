package com.zidansyahidagrifasa0072.assesment3.data.network

sealed class AppNetworkState<out T> {
    object Loading : AppNetworkState<Nothing>()
    data class Success<out T>(val data: T) : AppNetworkState<T>()
    data class Error(val message: String) : AppNetworkState<Nothing>()
}