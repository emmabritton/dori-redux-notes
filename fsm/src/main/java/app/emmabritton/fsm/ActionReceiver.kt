package app.emmabritton.fsm

/**
 * Receives actions
 */
interface ActionReceiver {
    fun receive(action: Action)
}