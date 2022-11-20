package app.emmabritton.fsm

import org.jetbrains.annotations.TestOnly
import java.util.concurrent.Executors

/**
 * Controls the thread where the [RuntimeKernel.render] method is called is executed
 *
 * See [ImmediateMarshaller] and [SingleThreadExecutorMarshaller]
 */
interface Marshaller {
    fun run(method: () -> Unit)
}

/**
 * Executes submitted method immediately on the current thread
 *
 * Primarily for testing
 */
class ImmediateMarshaller : Marshaller {
    override fun run(method: () -> Unit) = method()
}

/**
 * Queues method to be executed on a single worker thread
 *
 * This marshaller has no exception handling and so any exceptions thrown
 * in [run] are lost
 */
class SingleThreadExecutorMarshaller : Marshaller {
    private val executor = Executors.newSingleThreadExecutor()

    override fun run(method: () -> Unit) {
        executor.execute { method() }
    }

    @TestOnly
    fun waitForAllTasks(seconds: Long) {
        executor.shutdownAndWaitOrThrow("marshaller", seconds)
    }
}