package app.emmabritton.fsm

import org.jetbrains.annotations.TestOnly
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Receives commands to be executed, and them runs them on the correct thread
 */
interface CommandHandler {
    var actionReceiver: ActionReceiver

    fun add(command: Command)

    fun cancel(commandClass: Class<Command>)
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

    override fun cancel(commandClass: Class<Command>) {
        throw NotImplementedError("ImmediateCommandHandler doesn't support cancelling")
    }
}

/**
 * Queues commands to be executed in a fixed thread pool
 */
class FixedThreadPoolExecutorCommandHandler(count: Int = 1) : CommandHandler {
    private val executor = Executors.newFixedThreadPool(count)
    override lateinit var actionReceiver: ActionReceiver
    private val commandFutures = mutableListOf<CommandJob>()

    override fun add(command: Command) {
        synchronized(executor) {
            commandFutures.removeAll { it.future.isDone }
            val future = executor.submit { command.run(actionReceiver) }
            commandFutures.add(CommandJob(future, command, command::class.java))
        }
    }

    @TestOnly
    fun waitForAllTasks(seconds: Long) {
        executor.shutdownAndWaitOrThrow("handler", seconds)
    }

    override fun cancel(commandClass: Class<Command>) {
        synchronized(executor) {
            commandFutures.removeAll { it.future.isDone }
            commandFutures.filter { it.commandClass == commandClass }
                .forEach { it.command.cancel() }
            commandFutures.removeAll { it.commandClass == commandClass }
        }
    }

    private inner class CommandJob(
        val future: Future<*>,
        val command: Command,
        val commandClass: Class<out Command>
    )
}