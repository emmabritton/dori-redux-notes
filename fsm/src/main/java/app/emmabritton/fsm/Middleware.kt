package app.emmabritton.fsm

/**
 * Used to view events in the [RuntimeKernel] and optionally transform them
 */
interface Middleware<F : FsmForegroundState, S : FsmState<F>, U : FsmUiState> {
    /**
     * Called when an action is received by the runtime
     */
    fun onActionReceived(action: Action) = action

    /**
     * Called after the action has been reduced
     *
     * @param state the current state that is about to be replaced
     */
    fun onActionProcessed(action: Action, state: S, effect: Effect<F, S>) = effect

    /**
     * Called after the state has been transformed into ui state and is about to be rendered
     */
    fun onStateTransformed(state: S, uiState: U) = uiState

    /**
     * Called when a command is submitted to be run
     */
    fun onCommandSubmitted(isForegroundCommand: Boolean, command: Command) = command

    /**
     * Called was a command is cancelled
     *
     * In [RuntimeKernel] if any middleware returns false then the command will be left alive
     */
    fun onCommandCancelled(wasAutomaticallyCancelled: Boolean, id: CommandId) = true

    /**
     * Called was all commands of a certain type are cancelled
     *
     * In [RuntimeKernel] if any middleware returns false then the commands will be left alive
     */
    fun <C : Command> onCommandCancelled(
        wasAutomaticallyCancelled: Boolean,
        commandClass: Class<C>
    ) = true
}

/**
 * Convenience class for [Middleware] that handles returning the original value
 */
abstract class ReadOnlyMiddleware<F : FsmForegroundState, S : FsmState<F>, U : FsmUiState> :
    Middleware<F, S, U> {
    protected open fun internalOnActionReceived(action: Action) {}
    protected open fun internalOnActionProcessed(action: Action, state: S, effect: Effect<F, S>) {}
    protected open fun internalOnStateTransformed(state: S, uiState: U) {}
    protected open fun internalOnCommandSubmitted(isForegroundCommand: Boolean, command: Command) {}
    protected open fun internalOnCommandCancelled(
        wasAutomaticallyCancelled: Boolean,
        id: CommandId
    ) {
    }

    protected open fun <C : Command> internalOnCommandCancelled(
        wasAutomaticallyCancelled: Boolean,
        commandClass: Class<C>
    ): Boolean {
        return super.onCommandCancelled(wasAutomaticallyCancelled, commandClass)
    }

    final override fun onActionReceived(action: Action): Action {
        internalOnActionReceived(action)
        return action
    }

    final override fun onActionProcessed(
        action: Action,
        state: S,
        effect: Effect<F, S>
    ): Effect<F, S> {
        internalOnActionProcessed(action, state, effect)
        return effect
    }

    final override fun onStateTransformed(state: S, uiState: U): U {
        internalOnStateTransformed(state, uiState)
        return uiState
    }

    final override fun onCommandSubmitted(isForegroundCommand: Boolean, command: Command): Command {
        internalOnCommandSubmitted(isForegroundCommand, command)
        return command
    }

    final override fun onCommandCancelled(
        wasAutomaticallyCancelled: Boolean,
        id: CommandId
    ): Boolean {
        internalOnCommandCancelled(wasAutomaticallyCancelled, id)
        return true
    }

    final override fun <C : Command> onCommandCancelled(
        wasAutomaticallyCancelled: Boolean,
        commandClass: Class<C>
    ): Boolean {
        return super.onCommandCancelled(wasAutomaticallyCancelled, commandClass)
    }
}