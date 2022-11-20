package app.emmabritton.fsm

import app.emmabritton.fsm.internal.createImmediateRuntime
import app.emmabritton.fsm.internal.createThreadedRuntime
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.lang.Thread.sleep
import java.util.concurrent.Executors

class ThreadingTest {
    @Test
    fun `GIVEN immediate runtime WHEN multiple threads submit actions THEN result is correct`() {
        data class State(val count: Int) : EmptyState()
        val Increment = object : Action {}
        val reduce = { _: Action, state: State ->
            Effect(state.copy(count = state.count + 1), emptyList())
        }
        val runtime = createImmediateRuntime(reduce, State(0))

        val executor = Executors.newFixedThreadPool(50)
        for (i in 0..100) {
            executor.execute {
                sleep((110-i).toLong())
                runtime.receive(Increment)
                sleep((110-i).toLong())
                runtime.receive(Increment)
            }
        }
        executor.shutdownAndWaitOrThrow("executor", 2)

        assertEquals(runtime.state, State(202))
    }

    @Test
    fun `GIVEN threaded runtime WHEN multiple threads submit actions THEN result is correct`() {
        data class State(val count: Int): EmptyState()
        val Increment = object : Action {}
        val reduce = { _: Action, state: State ->
            Effect(state.copy(count = state.count + 1), emptyList())
        }
        val (runtime, marshaller, handler) = createThreadedRuntime(reduce, State(0))
        val executor = Executors.newFixedThreadPool(50)
        for (i in 0..100) {
            executor.execute {
                sleep((110-i).toLong())
                runtime.receive(Increment)
                sleep((110-i).toLong())
                runtime.receive(Increment)
            }
        }
        executor.shutdownAndWaitOrThrow("executor", 2)
        marshaller.waitForAllTasks(2)
        handler.waitForAllTasks(2)

        assertEquals(runtime.state, State(202))
    }
}