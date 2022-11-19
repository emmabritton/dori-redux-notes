package app.emmabritton.fsm

val EmptyEffect = Effect(EmptyState, emptyList())
fun emptyReducer(method: (Action) -> Unit): (Action, EmptyState) -> Effect<EmptyState> {
    return { act, _ -> method(act); EmptyEffect }
}

object EmptyState {
    override fun toString(): String = javaClass.simpleName
}