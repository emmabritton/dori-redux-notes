package app.emmabritton.fsm

import org.jetbrains.annotations.TestOnly

/**
 * The core of the library, it receives actions and reduces to produce commands,
 * they are passed to the CommandHandler with executes generating more actions,
 * and so on..
 */
open class RuntimeKernel<F : FsmForegroundState, S : FsmState<F>, U : FsmUiState>(
    /**
     * Executes code on the main thread
     *
     * Primarily used so Android code doesn't alter the UI from a background thread
     */
    private val marshaller: Marshaller,
    /**
     * Converts state and an action into a new state with optional commands
     */
    private val reduce: (Action, S) -> Effect<F, S>,
    /**
     * Called after an action has been reduced with the new state
     */
    private val render: (U) -> Unit,
    /**
     * Handles where commands are executed
     * see [CommandHandler], [Command], [BaseCommand]
     */
    private val commandHandler: CommandHandler,
    /**
     * Converts state ([S]) into ui state ([U]) to be rendered
     */
    private val presentation: Transformer<F, S, U>,
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

    private val logger by lazy { FsmLogger.get(Source.Runtime) }

    private val stateChangeLock = Any()
    private val middlewares = mutableListOf<Middleware<F, S, U>>()

    init {
        commandHandler.actionReceiver = this
    }

    override fun receive(action: Action) {
        //catch exceptions in transform?
        //if so, prevent loops
        synchronized(stateChangeLock) {
            marshaller.run {
                val effect = reduceAction(action)

                cancelForegroundCommands()
                state = effect.newState
                submitNewCommands(effect)

                present()
            }
        }
    }

    private fun reduceAction(action: Action): Effect<F, S> {
        logger.d("Received ${action.describe()}")
        val processedAction =
            middlewares.fold(action) { act, ware -> ware.onActionReceived(act) }
        logger.d("Processed to ${processedAction.describe()}")

        val effect = reduce(processedAction, state)
        logger.d("Reduced to $effect")

        val processedEffect =
            middlewares.fold(effect) { eff, ware -> ware.onActionProcessed(action, state, eff) }
        logger.d("Processed to $processedEffect")

        return processedEffect
    }

    private fun present() {
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

    private fun cancelForegroundCommands() {
        for (id in state.getForegroundState().getCommandIdsToCancelOnBackground()) {
            val cancel = middlewares.all { it.onCommandCancelled(true, id) }
            if (cancel) {
                logger.d("Automatically cancelling $id")
                commandHandler.cancel(id)
            } else {
                logger.d("Keeping foreground $id alive")
            }
        }
    }

    private fun submitNewCommands(effect: Effect<F, S>) {
        for (command in state.getForegroundState().getForegroundCommands()) {
            logger.d("Adding foreground ${command.id()}")
            val processedCommand =
                middlewares.fold(command) { cmd, ware -> ware.onCommandSubmitted(true, cmd) }
            logger.d("Transformed to ${processedCommand.id()}")
            commandHandler.add(processedCommand)
        }
        for (command in effect.commands) {
            logger.d("Adding ${command.id()}")
            val processedCommand =
                middlewares.fold(command) { cmd, ware -> ware.onCommandSubmitted(false, cmd) }
            logger.d("Transformed to ${processedCommand.id()}")
            commandHandler.add(processedCommand)
        }
    }

    fun cancelCommand(id: CommandId) {
        val cancel = middlewares.all { it.onCommandCancelled(false, id) }
        if (cancel) {
            logger.d("Cancelling $id")
            commandHandler.cancel(id)
        } else {
            logger.d("Keeping $id alive")
        }
    }

    fun <C : Command> cancelCommands(commandClass: Class<C>) {
        val cancel = middlewares.all { it.onCommandCancelled(false, commandClass) }
        if (cancel) {
            logger.d("Cancelling $commandClass")
            commandHandler.cancel(commandClass)
        } else {
            logger.d("Keeping $commandClass alive")
        }
    }

    fun addMiddleware(middleware: Middleware<F, S, U>) {
        middlewares.add(middleware)
    }

    fun removeMiddleware(middleware: Middleware<F, S, U>) {
        middlewares.remove(middleware)
    }
}