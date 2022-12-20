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

    fun cancel(commandId: CommandId)
    fun cancel(commandClass: Class<out Command>)
}

/**
 * Immediately executes commands on the calling thread
 *
 * Used for testing
 */
class ImmediateCommandHandler : CommandHandler {
    private val logger = FsmLogger.get(Source.from(this))
    override lateinit var actionReceiver: ActionReceiver

    override fun add(command: Command) {
        try {
            command.run(actionReceiver)
        } catch (e: Exception) {
            logger.e(e, "Command crashed: ${command.id()}")
            actionReceiver.receive(CommandException(command.id(), e))
        }
    }

    override fun cancel(commandId: CommandId) {
        throw NotImplementedError("ImmediateCommandHandler doesn't support cancelling")
    }

    override fun cancel(commandClass: Class<out Command>) {
        throw NotImplementedError("ImmediateCommandHandler doesn't support cancelling")
    }
}

/**
 * Queues commands to be executed in a fixed thread pool
 */
class FixedThreadPoolExecutorCommandHandler(count: Int = 1) : CommandHandler {
    private val logger = FsmLogger.get(Source.from(this))
    private val executor = Executors.newFixedThreadPool(count)
    override lateinit var actionReceiver: ActionReceiver
    private val commandFutures = mutableListOf<CommandJob>()

    override fun add(command: Command) {
        synchronized(executor) {
            commandFutures.removeAll { it.future.isDone }
            val future = executor.submit {
                try {
                    command.run(actionReceiver)
                } catch (e: Exception) {
                    logger.e(e, "Command crashed: ${command.id()}")
                    actionReceiver.receive(CommandException(command.id(), e))
                }
            }
            commandFutures.add(CommandJob(future, command))
        }
    }

    @TestOnly
    fun waitForAllTasks(seconds: Long) {
        executor.shutdownAndWaitOrThrow("handler", seconds)
    }

    override fun cancel(commandId: CommandId) {
        cancel { it.command.id() == commandId }
    }

    override fun cancel(commandClass: Class<out Command>) {
        cancel { it.command.javaClass == commandClass }
    }

    private fun cancel(predicate: (CommandJob) -> Boolean) {
        synchronized(executor) {
            commandFutures.removeAll { it.future.isDone }
            commandFutures.filter(predicate)
                .forEach { it.command.cancel() }
            commandFutures.removeAll(predicate)
        }
    }

    private inner class CommandJob(
        val future: Future<*>,
        val command: Command
    )
}