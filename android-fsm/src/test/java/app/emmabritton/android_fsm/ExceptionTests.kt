package app.emmabritton.android_fsm

import android.os.Build
import app.emmabritton.fsm.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S_V2])
class ExceptionTests {
    class TestException : Exception("Test")

    @Test(expected = TestException::class)
    fun `GIVEN threaded runtime and reducer that will throw WHEN it throws THEN process crashes`() {
        class TestFState : FsmForegroundState {
            override fun getForegroundCommands() = emptyList<Command>()
            override fun getCommandIdsToCancelOnBackground() = emptyList<CommandId>()
        }

        class TestState : FsmState<TestFState> {
            override fun getForegroundState() = TestFState()
        }

        class TestUState : FsmUiState

        val reduce = { _: Action, _: TestState -> throw TestException() }
        val marshaller = AndroidMainThreadMarshaller()
        val handler = FixedThreadPoolExecutorCommandHandler()
        val transform = object : Transformer<TestFState, TestState, TestUState> {
            override fun transform(state: TestState) = TestUState()
        }
        val runtime = RuntimeKernel(marshaller, reduce, {}, handler, transform, TestState())
        runtime.receive(object : Action {})
        handler.waitForAllTasks(1)
    }
}