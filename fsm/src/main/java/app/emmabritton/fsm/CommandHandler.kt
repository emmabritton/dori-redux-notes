package app.emmabritton.fsm

import org.jetbrains.annotations.TestOnly
import java.util.concurrent.Executors

/**
 * Receives commands to be executed, and them runs them on the correct thread
 */
interface CommandHandler {
    var actionReceiver: ActionReceiver

    fun add(command: Command)
}

/**
 * Immediately executes commands on the calling thread
 *
 * Used for testing
 */
class ImmediateCommandHandler : CommandHandler {
    override lateinit var actionReceiver: ActionReceiver

    override fun add(command: Command) {
        command.run(actionReceiver)
    }
}

/**
 * Queues commands to be executed on a single worker thread
 */
class SingleThreadExecutorCommandHandler : CommandHandler {
    private val executor = Executors.newSingleThreadExecutor()
    override lateinit var actionReceiver: ActionReceiver

    override fun add(command: Command) {
        executor.execute { command.run(actionReceiver) }
    }

    @TestOnly
    fun waitForAllTasks(seconds: Long) {
        executor.shutdownAndWaitOrThrow("handler", seconds)
    }
}