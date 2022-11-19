package app.emmabritton.fsm

/**
 * Converts object of one type into another
 *
 * Used in [RuntimeKernel] to convert app state into ui state
 */
interface Transformer<S,U> {
    fun transform(state: S): U
}