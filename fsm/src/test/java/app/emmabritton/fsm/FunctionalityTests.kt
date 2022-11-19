package app.emmabritton.fsm

import app.emmabritton.fsm.internal.createImmediateRuntime
import app.emmabritton.fsm.internal.mockAction
import app.emmabritton.fsm.internal.mockCommand
import app.emmabritton.fsm.internal.mockReadOnlyMiddleware
import io.mockk.*
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FunctionalityTests {

    @Test
    fun `GIVEN basic immediate runtime WHEN action is received THEN reducer is called`() {
        var reducerCalled = false
        val reducer = emptyReducer { reducerCalled = true }
        val runtime = createImmediateRuntime(reducer, EmptyState)

        runtime.receive(mockAction())

        assert(reducerCalled)
    }

    @Test
    fun `GIVEN basic immediate runtime WHEN action with command is received THEN command is executed`() {
        val command = mockCommand()
        val reducer: (Action, EmptyState) -> Effect<EmptyState> = {_,_ -> Effect(EmptyState, listOf(command))}
        val runtime = createImmediateRuntime(reducer, EmptyState)

        runtime.receive(mockAction())

        verify(exactly = 1) { command.run(runtime) }
    }

    @Test
    fun `GIVEN basic immediate runtime WHEN action is received THEN exact action is reduced`() {
        lateinit var actionReceived: Action
        val actionToBeSent = mockAction()
        val reducer = emptyReducer { actionReceived = it }
        val runtime = createImmediateRuntime(reducer, EmptyState)

        runtime.receive(actionToBeSent)

        assert(actionReceived === actionToBeSent)
    }

    @Test
    fun `GIVEN basic immediate runtime WHEN action is received THEN middleware is called`() {
        val reducer = emptyReducer { }
        val actionToBeSent = mockAction()
        val middleware = mockReadOnlyMiddleware<EmptyState>()
        val runtime = createImmediateRuntime(reducer, EmptyState)
        runtime.addMiddleware(middleware)

        runtime.receive(actionToBeSent)

        verify(exactly = 1) { middleware.onActionReceived(actionToBeSent) }
        verify(exactly = 1) { middleware.onActionProcessed(actionToBeSent, any(), EmptyEffect) }
        verify(exactly = 1) { middleware.onStateTransformed(any(), any()) }

        confirmVerified(middleware)
    }

    @Test
    fun `GIVEN runtime WHEN action is received THEN logger outputs correctly`() {
        val log = StringBuffer()
        FsmLogger.debug = { log.append('\n'); log.append(it) }

        val reducer = emptyReducer { }
        val runtime = createImmediateRuntime(reducer, EmptyState)
        val action = mockAction()
        runtime.receive(action)

        assertEquals(
            log.toString(), """
[RT] Received ${action.describe()}
[RT] Processed to ${action.describe()}
[RT] Reduced to State: EmptyState - No commands
[RT] Processed to State: EmptyState - No commands
[RT] Transformed EmptyState to EmptyState
[RT] Processed to EmptyState"""
        )
    }
}