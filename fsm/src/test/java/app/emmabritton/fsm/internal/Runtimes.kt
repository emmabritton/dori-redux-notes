package app.emmabritton.fsm.internal

import app.emmabritton.fsm.*

object FixedUiState : FsmUiState {
    override fun toString() = "FixedUiState"
}

private class FixedTransform<F : FsmForegroundState, S : FsmState<F>> :
    Transformer<F, S, FixedUiState> {
    override fun transform(state: S) = FixedUiState
}

fun <F : FsmForegroundState, S : FsmState<F>> createImmediateRuntime(
    reduce: (Action, S) -> Effect<F, S>,
    initState: S
) = createCustomRuntime(
    reduce,
    initState,
    ImmediateMarshaller(),
    ImmediateCommandHandler(),
    FixedTransform()
)

fun <F : FsmForegroundState, S : FsmState<F>> createThreadedRuntime(
    reduce: (Action, S) -> Effect<F, S>,
    initState: S
): Triple<RuntimeKernel<F, S, FixedUiState>, SingleThreadExecutorMarshaller, FixedThreadPoolExecutorCommandHandler> {
    val marshaller = SingleThreadExecutorMarshaller()
    val handler = FixedThreadPoolExecutorCommandHandler(3)
    val runtime =
        createCustomRuntime(reduce, initState, marshaller, handler, FixedTransform())
    return Triple(runtime, marshaller, handler)
}

fun <F : FsmForegroundState, S : FsmState<F>, U : FsmUiState> createCustomRuntime(
    reduce: (Action, S) -> Effect<F, S>,
    initState: S,
    marshaller: Marshaller,
    commandHandler: CommandHandler,
    transformer: Transformer<F, S, U>
) = RuntimeKernel(marshaller, reduce, {}, commandHandler, transformer, initState)