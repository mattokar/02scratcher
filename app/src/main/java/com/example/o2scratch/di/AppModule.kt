package com.example.o2scratch.di

import com.example.o2scratch.manager.ScratchCardManager
import com.example.o2scratch.manager.ScratchCardManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    fun bindScratchCardManager(scratchCardManagerImpl: ScratchCardManagerImpl): ScratchCardManager
}