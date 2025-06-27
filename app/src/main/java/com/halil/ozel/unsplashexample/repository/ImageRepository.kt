package com.halil.ozel.unsplashexample.repository

import com.halil.ozel.unsplashexample.api.ImageService
import javax.inject.Inject

class ImageRepository @Inject constructor(private val api: ImageService) {
    suspend fun getImages(page: Int) = api.getImages(page)
}
