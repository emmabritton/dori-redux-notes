package app.emmabritton.fsm.internal

import app.emmabritton.fsm.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

fun mockAction(): Action {
    val mock = mockk<Action>()
    every { mock.describe() } answers { "mock-action" }
    return mock
}

fun mockCommand(): Command {
    val command = mockk<Command>()
    every { command.id() } answers { "mock-command" }
    every { command.run(any()) } answers { }
    return command
}

inline fun <reified F : FsmForegroundState, reified S : FsmState<F>, reified U : FsmUiState> mockReadOnlyMiddleware(): ReadOnlyMiddleware<F, S, U> {
    val middleware = mockk<ReadOnlyMiddleware<F, S, U>>()
    val actionSlot = slot<Action>()
    val effectSlot = slot<Effect<F, S>>()
    val stateSlot = slot<U>()
    val commandSlot = slot<Command>()

    every { middleware.onActionReceived(capture(actionSlot)) } answers { actionSlot.captured }

    every {
        middleware.onActionProcessed(
            any(),
            any(),
            capture(effectSlot)
        )
    } answers { effectSlot.captured }

    every {
        middleware.onStateTransformed(
            any(),
            capture(stateSlot)
        )
    } answers { stateSlot.captured }

    every {
        middleware.onCommandCancelled(any(), any())
    } answers { true }

    every {
        middleware.onCommandSubmitted(any(), capture(commandSlot))
    } answers {
        commandSlot.captured
    }

    return middleware
}