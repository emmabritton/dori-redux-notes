package app.emmabritton.fsm

import app.emmabritton.fsm.internal.createImmediateRuntime
import app.emmabritton.fsm.internal.createThreadedRuntime
import app.emmabritton.fsm.internal.mockAction
import org.junit.Test

class ExceptionTests {
    class TestException : Exception()

    @Test(expected = TestException::class)
    fun `GIVEN immediate runtime and reducer that will throw WHEN it throws THEN process crashes`() {
        val reduce = {_: Action, _: EmptyState -> throw TestException()}
        val runtime = createImmediateRuntime(reduce, EmptyState())
        runtime.receive(mockAction())
    }

    @Test()
    fun `GIVEN threaded runtime and reducer that will throw WHEN it throws THEN process continues`() {
        val reduce = {_: Action, _: EmptyState -> throw TestException()}
        val (runtime, marshaller,handler) = createThreadedRuntime(reduce, EmptyState())
        runtime.receive(mockAction())
        marshaller.waitForAllTasks(1)
        handler.waitForAllTasks(1)
    }
}