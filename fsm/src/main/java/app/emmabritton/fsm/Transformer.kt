package app.emmabritton.fsm

/**
 * Converts state into ui state
 */
interface Transformer<F : FsmForegroundState, S : FsmState<F>, U : FsmUiState> {
    fun transform(state: S): U
}