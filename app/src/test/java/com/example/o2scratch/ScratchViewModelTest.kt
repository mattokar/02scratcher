package com.example.o2scratch

import app.cash.turbine.test
import com.example.o2scratch.api.model.ActivationResponse
import com.example.o2scratch.manager.ScratchCardManager
import com.example.o2scratch.ui.compose.ScratchCardContract
import com.example.o2scratch.ui.compose.ScratchCardViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScratchViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val scratchCardManager: ScratchCardManager = mockk()
    private lateinit var viewModel: ScratchCardViewModel

    @Before
    fun setup() {
        viewModel = ScratchCardViewModel(scratchCardManager)
    }

    @Test
    fun `test reveal code`() = runTest {
        assert(viewModel.viewStateFlow.value.code == "XXXXXX")
        viewModel.executeAction(ScratchCardContract.Action.RevealCode)
        assert(viewModel.viewStateFlow.value.loading)
        delay(2100)
        assert(!viewModel.viewStateFlow.value.loading)
        assert(viewModel.viewStateFlow.value.code != "XXXXXX")
    }

    @Test
    fun `test reveal code interrupted`() = runTest {
        assert(viewModel.viewStateFlow.value.code == "XXXXXX")
        viewModel.executeAction(ScratchCardContract.Action.RevealCode)
        assert(viewModel.viewStateFlow.value.loading)
        delay(1000)
        viewModel.executeAction(ScratchCardContract.Action.CancelRevealCode)
        assert(!viewModel.viewStateFlow.value.loading)
        assert(viewModel.viewStateFlow.value.code == "XXXXXX")
    }

    @Test
    fun `test code activation`() = runTest {
        val response = ActivationResponse("500000")
        coEvery { scratchCardManager.activateScratchCard(any()) } returns response
        viewModel.executeAction(ScratchCardContract.Action.RevealCode)
        delay(2100)
        viewModel.executeAction(ScratchCardContract.Action.Activate)
        assert(viewModel.viewStateFlow.value.activated)
    }

    @Test
    fun `test code activation wrong response`() = runTest {
        val response = ActivationResponse("10000")
        coEvery { scratchCardManager.activateScratchCard(any()) } returns response
        viewModel.executeAction(ScratchCardContract.Action.RevealCode)
        delay(2100)
        viewModel.executeAction(ScratchCardContract.Action.Activate)
        assert(!viewModel.viewStateFlow.value.activated)

        viewModel.viewEventsFlow.test {
            val event = awaitItem()
            assert(event is ScratchCardContract.Event.Error)
        }
    }

    @Test
    fun `test code activation error response`() = runTest {
        val exception = Exception("Error")
        coEvery { scratchCardManager.activateScratchCard(any()) } throws exception
        viewModel.executeAction(ScratchCardContract.Action.RevealCode)
        delay(2100)
        viewModel.executeAction(ScratchCardContract.Action.Activate)
        assert(!viewModel.viewStateFlow.value.activated)

        viewModel.viewEventsFlow.test {
            val event = awaitItem()
            assert(event is ScratchCardContract.Event.Error)
        }
    }
}