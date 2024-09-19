package com.example.o2scratch.api.service

import com.example.o2scratch.api.model.ActivationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ScratchCardService {

    companion object {
        private const val VERSION_ENDPOINT = "/version"
    }

    @GET(VERSION_ENDPOINT)
    suspend fun activateCode(
        @Query("code") code: String,
    ): ActivationResponse
}