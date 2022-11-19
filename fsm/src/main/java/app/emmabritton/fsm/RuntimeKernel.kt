package app.emmabritton.fsm

import org.jetbrains.annotations.TestOnly

/**
 * The core of the library, it receives actions and reduces to produce commands,
 * they are passed to the CommandHandler with executes generating more actions,
 * and so on..
 */
open class RuntimeKernel<S, U>(
    /**
     * Executes code on the main thread
     *
     * Primarily used so Android code doesn't alter the UI from a background thread
     */
    protected val marshaller: Marshaller,
    /**
     * Converts state and an action into a new state with optional commands
     */
    protected val reduce: (Action, S) -> Effect<S>,
    /**
     * Called after an action has been reduced with the new state
     */
    protected val render: (U) -> Unit,
    /**
     * Handles where commands are executed
     * see [CommandHandler], [Command], [BaseCommand]
     */
    protected val commandHandler: CommandHandler,
    /**
     * Converts state ([S]) into ui state ([U]) to be rendered
     */
    protected val presentation: Transformer<S, U>,
    /**
     * Initial state for the app
     */
    initState: S
) : ActionReceiver {
    var state = initState
        @TestOnly
        get
        @TestOnly
        set

    protected val logger by lazy { FsmLogger.get(Source.Runtime) }

    protected val stateChangeLock = Any()
    protected val middlewares = mutableListOf<Middleware<S, U>>()

    override fun receive(action: Action) {
        synchronized(stateChangeLock) {
            logger.d("Received ${action.describe()}")
            val processedAction =
                middlewares.fold(action) { act, ware -> ware.onActionReceived(act) }
            logger.d("Processed to ${processedAction.describe()}")

            val effect = reduce(processedAction, state)
            logger.d("Reduced to $effect")

            val processedEffect =
                middlewares.fold(effect) { eff, ware -> ware.onActionProcessed(action, state, eff) }
            logger.d("Processed to $processedEffect")

            state = processedEffect.newState

            for (command in processedEffect.commands) {
                commandHandler.add(command)
            }

            marshaller.run {
                val uiState = presentation.transform(state)
                logger.d("Transformed $state to $uiState")
                val processedUiState = middlewares.fold(uiState) { uiStt, ware ->
                    ware.onStateTransformed(
                        state,
                        uiStt
                    )
                }
                logger.d("Processed to $processedUiState")
                render(processedUiState)
            }
        }
    }

    fun addMiddleware(middleware: Middleware<S, U>) {
        middlewares.add(middleware)
    }

    fun removeMiddleware(middleware: Middleware<S, U>) {
        middlewares.remove(middleware)
    }
}