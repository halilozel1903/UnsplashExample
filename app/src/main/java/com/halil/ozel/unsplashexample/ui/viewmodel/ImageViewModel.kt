package com.halil.ozel.unsplashexample.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.unsplashexample.model.ImageItem
import com.halil.ozel.unsplashexample.repository.ImageRepository
import com.halil.ozel.unsplashexample.utils.Constants.PER_PAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val repository: ImageRepository) : ViewModel() {
    private val _response = MutableLiveData<List<ImageItem>>()
    val responseImages: LiveData<List<ImageItem>> = _response

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val imageList = mutableListOf<ImageItem>()
    private var currentPage = 1
    private var isRequestInFlight = false
    private var hasReachedEnd = false

    init {
        loadNextPage()
    }

    fun loadNextPage() = viewModelScope.launch {
        if (isRequestInFlight || hasReachedEnd) return@launch

        isRequestInFlight = true
        _isLoading.postValue(true)
        _errorMessage.postValue(null)

        try {
            val response = repository.getImages(currentPage)
            if (response.isSuccessful) {
                val items = response.body().orEmpty()
                if (items.isEmpty()) {
                    hasReachedEnd = true
                    _response.postValue(imageList.toList())
                } else {
                    imageList.addAll(items)
                    _response.postValue(imageList.toList())
                    currentPage++
                    hasReachedEnd = items.size < PER_PAGE
                }
            } else {
                Log.e(TAG, "Error: ${'$'}{response.errorBody()}")
                _errorMessage.postValue("Sunucudan içerik alınamadı.")
            }
        } catch (exception: Exception) {
            Log.e(
                TAG,
                "Exception while loading Unsplash images: ${exception.message}",
                exception
            )
            _errorMessage.postValue("Bağlantı hatası oluştu.")
        } finally {
            isRequestInFlight = false
            _isLoading.postValue(false)
        }
    }

    companion object {
        private const val TAG = "ImageViewModel"
    }
}
