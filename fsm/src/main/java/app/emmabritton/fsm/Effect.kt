package app.emmabritton.fsm

/**
 * After reducing an action, this contains the new state and any commands that need to be executed
 */
open class Effect<S>(val newState: S, val commands: List<Command>) {
    override fun toString(): String {
        return "State: ${newState.toString()}" + if (commands.isEmpty()) {
            " - No commands"
        } else {
            " - Commands: " + commands.joinToString(",") { it.name() }
        }
    }
}