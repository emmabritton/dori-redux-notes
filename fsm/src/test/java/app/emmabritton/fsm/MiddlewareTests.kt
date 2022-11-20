package app.emmabritton.fsm

import app.emmabritton.fsm.internal.FixedUiState
import app.emmabritton.fsm.internal.createImmediateRuntime
import app.emmabritton.fsm.internal.mockAction
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MiddlewareTests {
    @Test
    fun `GIVEN runtime with received action changing middleware WHEN action is received THEN action is changed`(){
        lateinit var receivedAction: Action
        lateinit var middlewareAction: Action
        val targetAction = mockAction()
        val reducer = emptyReducer { receivedAction = it }
        val middleware = object: Middleware<EmptyForegroundState, EmptyState, FixedUiState> {
            override fun onActionReceived(action: Action): Action {
                middlewareAction = action
                return targetAction
            }
        }
        val runtime = createImmediateRuntime(reducer, EmptyState())
        runtime.addMiddleware(middleware)
        val action = mockAction()
        runtime.receive(action)

        assertEquals(action, middlewareAction)
        assertEquals(targetAction, receivedAction)
    }
}