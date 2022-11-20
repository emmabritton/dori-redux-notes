package app.emmabritton.fsm

/**
 * Converts state into ui state
 */
interface Transformer<F : ForegroundState, S : State<F>, U : UiState> {
    fun transform(state: S): U
}