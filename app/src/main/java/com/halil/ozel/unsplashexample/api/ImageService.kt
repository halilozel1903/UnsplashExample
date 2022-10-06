package com.halil.ozel.unsplashexample.api

import com.halil.ozel.unsplashexample.model.ImageItem
import com.halil.ozel.unsplashexample.utils.Constants.ACCEPT_VERSION
import com.halil.ozel.unsplashexample.utils.Constants.AUTHORIZATION
import com.halil.ozel.unsplashexample.utils.Constants.CLIENT_ID
import com.halil.ozel.unsplashexample.utils.Constants.END_POINT
import com.halil.ozel.unsplashexample.utils.Constants.VERSION
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ImageService {
    @Headers("$ACCEPT_VERSION: $VERSION", "$AUTHORIZATION $CLIENT_ID")
    @GET(END_POINT)
    suspend fun getAllImages(): Response<List<ImageItem>>
}