package com.example.o2scratch.di

import android.content.Context
import com.example.o2scratch.R
import com.example.o2scratch.api.ApiProvider
import com.example.o2scratch.api.ApiProviderImpl
import com.example.o2scratch.api.BaseUrlProvider
import com.example.o2scratch.api.BaseUrlProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultBaseUrl

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {

    companion object {

        @DefaultBaseUrl
        @Provides
        fun provideDefaultBaseUrl(
            @ApplicationContext context: Context
        ): String {
            return context.getString(R.string.o2_api_url)
        }

        @Provides
        @Singleton
        fun provideBaseUrlProvider(
            @DefaultBaseUrl defaultBaseUrl: String
        ): BaseUrlProvider {
            return BaseUrlProviderImpl(defaultBaseUrl)
        }


        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .build()
        }

    }

    @Binds
    @Singleton
    fun bindApiProvider(apiProviderImpl: ApiProviderImpl): ApiProvider

}