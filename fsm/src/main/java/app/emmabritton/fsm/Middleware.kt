package app.emmabritton.fsm

/**
 * Used to view events in the [RuntimeKernel] and optionally transform them
 */
interface Middleware<S, U> {
    /**
     * Called when an action is received by the runtime
     */
    fun onActionReceived(action: Action) = action

    /**
     * Called after the action has been reduced
     *
     * @param state the current state that is about to be replaced
     */
    fun onActionProcessed(action: Action, state: S, effect: Effect<S>) = effect

    /**
     * Called after the state has been transformed into ui state and is about to be rendered
     */
    fun onStateTransformed(state: S, uiState: U) = uiState
}

/**
 * Convenience class for [Middleware] that handles returning the original value
 */
abstract class ReadOnlyMiddleware<S, U> : Middleware<S, U> {
    protected open fun internalOnActionReceived(action: Action) {}
    protected open fun internalOnActionProcessed(action: Action, state: S, effect: Effect<S>) {}
    protected open fun internalOnStateTransformed(state: S, uiState: U) {}

    final override fun onActionReceived(action: Action): Action {
        internalOnActionReceived(action)
        return action
    }

    final override fun onActionProcessed(action: Action, state: S, effect: Effect<S>): Effect<S> {
        internalOnActionProcessed(action, state, effect)
        return effect
    }

    final override fun onStateTransformed(state: S, uiState: U): U {
        internalOnStateTransformed(state, uiState)
        return uiState
    }
}