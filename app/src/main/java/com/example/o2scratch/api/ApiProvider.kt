package com.example.o2scratch.api

import android.util.Log
import com.example.o2scratch.api.service.ScratchCardService
import com.example.o2scratch.di.DefaultBaseUrl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject

interface ApiProvider {
    val scratchCardService: ScratchCardService
    var baseUrl: String
}

class ApiProviderImpl @Inject constructor(
    @DefaultBaseUrl private val defaultBaseUrl: String,
    private val baseUrlProvider: BaseUrlProvider,
    private val okHttpClient: OkHttpClient,
) : ApiProvider {

    private var _scratchCardService: ScratchCardService? = null

    override var baseUrl: String = baseUrlProvider.baseUrl
        set(value) {
            if (value.isNotBlank()) {
                _scratchCardService = null
                baseUrlProvider.baseUrl = value
                field = value
                Log.d("MobileApiProvider", "baseUrl changed to $value")
            }
        }

    override val scratchCardService: ScratchCardService
        get() = _scratchCardService ?: synchronized(this) {
            createRetrofit(baseUrl)
                .create(ScratchCardService::class.java)
                .also { _scratchCardService = it }
        }

    private fun createRetrofit(baseUrl: String): Retrofit {
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .client(okHttpClient)
            .build()
    }
}