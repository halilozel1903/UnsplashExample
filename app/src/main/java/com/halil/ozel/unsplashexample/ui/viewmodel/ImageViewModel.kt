package com.halil.ozel.unsplashexample.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.unsplashexample.model.ImageItem
import com.halil.ozel.unsplashexample.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val repository: ImageRepository) : ViewModel() {
    private val _response = MutableLiveData<List<ImageItem>>()
    val responseImages: LiveData<List<ImageItem>> = _response

    init {
        getAllImages()
    }

    private fun getAllImages() = viewModelScope.launch {
        try {
            val response = repository.getAllImages()
            if (response.isSuccessful) {
                _response.postValue(response.body())
            } else {
                Log.e(TAG, "Error: ${'$'}{response.errorBody()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${'$'}{e.localizedMessage}")
        }
    }

    companion object {
        private const val TAG = "ImageViewModel"
    }
}