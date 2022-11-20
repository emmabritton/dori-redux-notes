package app.emmabritton.fsm.internal

import app.emmabritton.fsm.*

object FixedUiState : UiState {
    override fun toString() = "FixedUiState"
}

private class FixedTransform<F : ForegroundState, S : State<F>> :
    Transformer<F, S, FixedUiState> {
    override fun transform(state: S) = FixedUiState
}

fun <F : ForegroundState, S : State<F>> createImmediateRuntime(
    reduce: (Action, S) -> Effect<F, S>,
    initState: S
) = createCustomRuntime(
    reduce,
    initState,
    ImmediateMarshaller(),
    ImmediateCommandHandler(),
    FixedTransform()
)

fun <F : ForegroundState, S : State<F>> createThreadedRuntime(
    reduce: (Action, S) -> Effect<F, S>,
    initState: S
): Triple<RuntimeKernel<F, S, FixedUiState>, SingleThreadExecutorMarshaller, FixedThreadPoolExecutorCommandHandler> {
    val marshaller = SingleThreadExecutorMarshaller()
    val handler = FixedThreadPoolExecutorCommandHandler()
    val runtime =
        createCustomRuntime(reduce, initState, marshaller, handler, FixedTransform())
    return Triple(runtime, marshaller, handler)
}

fun <F : ForegroundState, S : State<F>, U : UiState> createCustomRuntime(
    reduce: (Action, S) -> Effect<F, S>,
    initState: S,
    marshaller: Marshaller,
    commandHandler: CommandHandler,
    transformer: Transformer<F, S, U>
) = RuntimeKernel(marshaller, reduce, {}, commandHandler, transformer, initState)