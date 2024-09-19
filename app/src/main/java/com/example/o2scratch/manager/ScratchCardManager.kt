package com.example.o2scratch.manager

import com.example.o2scratch.api.ApiProvider
import com.example.o2scratch.api.model.ActivationResponse
import com.example.o2scratch.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ScratchCardManager {
    suspend fun activateScratchCard(activationCode: String): ActivationResponse

    companion object {
        const val SCRATCH_CARD_ACTIVATION_THRESHOLD = 277028
    }
}

class ScratchCardManagerImpl @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val apiProvider: ApiProvider,
) : ScratchCardManager {
    override suspend fun activateScratchCard(activationCode: String): ActivationResponse = withContext(dispatcher) {
        apiProvider.scratchCardService.activateCode(activationCode)
    }
}