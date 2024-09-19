package com.example.o2scratch

import com.example.o2scratch.api.ApiProvider
import com.example.o2scratch.api.model.ActivationResponse
import com.example.o2scratch.api.service.ScratchCardService
import com.example.o2scratch.manager.ScratchCardManager
import com.example.o2scratch.manager.ScratchCardManagerImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScratchManagerTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val scratchCardService: ScratchCardService = mockk()
    private val apiProvider: ApiProvider = mockk()
    private val scratchCardManager: ScratchCardManager = ScratchCardManagerImpl(coroutineRule.testDispatcher, apiProvider)

    @Before
    fun serUp() {
        every { apiProvider.scratchCardService } returns scratchCardService
    }

    @Test
    fun `test scratch card activation`() = runTest {
        // given
        val mockedResponse = ActivationResponse("1345135")
        coEvery { scratchCardService.activateCode(any()) } returns mockedResponse
        val actualResponse = scratchCardManager.activateScratchCard("123456")
        assert(actualResponse.android == mockedResponse.android)
        coVerify { scratchCardService.activateCode("123456") }
    }


}