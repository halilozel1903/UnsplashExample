package com.halil.ozel.unsplashexample.repository

import com.halil.ozel.unsplashexample.api.ImageService
import javax.inject.Inject

class ImageRepository @Inject constructor(private val api: ImageService) {
    suspend fun getAllImages() = api.getAllImages()
}