package app.emmabritton.fsm

val EmptyEffect = Effect(EmptyState(), emptyList())
fun emptyReducer(method: (Action) -> Unit): (Action, EmptyState) -> Effect<EmptyForegroundState, EmptyState> {
    return { act, _ -> method(act); EmptyEffect }
}

class EmptyForegroundState : FsmForegroundState {
    override fun getForegroundCommands() = emptyList<Command>()

    override fun getCommandIdsToCancelOnBackground() = emptyList<CommandId>()
}

open class EmptyState: FsmState<EmptyForegroundState> {
    override fun toString(): String = javaClass.simpleName
    override fun getForegroundState() = EmptyForegroundState()
}