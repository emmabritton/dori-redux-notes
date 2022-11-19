package app.emmabritton.fsm.internal

import app.emmabritton.fsm.Action
import app.emmabritton.fsm.Command
import app.emmabritton.fsm.Effect
import app.emmabritton.fsm.ReadOnlyMiddleware
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
    every { command.name() } answers  { "mock-command" }
    every { command.run(any()) } answers  { }
    return command
}

inline fun <reified S : Any> mockReadOnlyMiddleware(): ReadOnlyMiddleware<S, S> {
    val middleware = mockk<ReadOnlyMiddleware<S,S>>()
    val actionSlot = slot<Action>()
    val effectSlot = slot<Effect<S>>()
    val stateSlot = slot<S>()

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

    return middleware
}