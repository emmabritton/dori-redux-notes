package app.emmabritton.fsm

typealias CommandId = String

/**
 * Represents something that is going to happen, such as SubmitFoodSearch, Logout
 *
 * [BaseCommand] should be used as the parent rather than this interface
 */
interface Command {
    /**
     * Execute this command, it's results (success or failure) will be passed to actionReceiver as actions
     */
    fun run(actionReceiver: ActionReceiver)

    /**
     * Cancels the command
     * This prevents it from executing is hasn't started, or from emitting actions if it has
     */
    fun cancel()

    /**
     * Returns true if the command has been cancelled see [cancel]
     */
    fun isCancelled(): Boolean

    /**
     * Unique name of command, typically Class name and ID
     */
    fun id(): CommandId
}

/**
 * Base impl of Command that automatically skips execution and ignores actions if cancelled
 *
 * Override [internalRun] instead of [run] when using this class
 */
abstract class BaseCommand : Command {
    protected var cancelled = false

    protected val id by lazy { System.identityHashCode(this) }
    protected val logger by lazy { FsmLogger.get(Source.from(this)) }

    protected abstract fun internalRun(actionReceiver: ActionReceiver)

    override fun id() = "${javaClass.simpleName}#$id"

    final override fun run(actionReceiver: ActionReceiver) {
        if (isCancelled()) {
            logger.d("Ignoring run() for ${id()} as it's been cancelled")
        } else {
            internalRun(CancellableActionReceiver(actionReceiver))
        }
    }

    override fun cancel() {
        cancelled = true
    }

    final override fun isCancelled() = cancelled

    private inner class CancellableActionReceiver(private val receiver: ActionReceiver) :
        ActionReceiver {
        override fun receive(action: Action) {
            if (isCancelled()) {
                logger.d("Ignoring ${action.describe()} for ${id()} as it's been cancelled")
            } else {
                receiver.receive(action)
            }
        }
    }
}
