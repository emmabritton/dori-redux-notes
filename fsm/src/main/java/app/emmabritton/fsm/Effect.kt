package app.emmabritton.fsm

/**
 * After reducing an action, this contains the new state and any commands that need to be executed
 */
class Effect<F : FsmForegroundState, S : FsmState<F>>(val newState: S, val commands: List<Command>) {
    override fun toString(): String {
        return "State: $newState" + if (commands.isEmpty()) {
            " - No commands"
        } else {
            " - Commands: " + commands.joinToString(",") { it.id() }
        }
    }
}