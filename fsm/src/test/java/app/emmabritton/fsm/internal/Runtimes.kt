package app.emmabritton.fsm.internal

import app.emmabritton.fsm.*

private class PassThoughTransform<S> : Transformer<S, S> {
    override fun transform(state: S) = state
}

fun <S> createImmediateRuntime(
    reduce: (Action, S) -> Effect<S>,
    initState: S
) = createCustomRuntime(
    reduce,
    initState,
    ImmediateMarshaller(),
    ImmediateCommandHandler(),
    PassThoughTransform()
)

fun <S> createThreadedRuntime(
    reduce: (Action, S) -> Effect<S>,
    initState: S
): Triple<RuntimeKernel<S, S>, SingleThreadExecutorMarshaller, FixedThreadPoolExecutorCommandHandler> {
    val marshaller = SingleThreadExecutorMarshaller()
    val handler = FixedThreadPoolExecutorCommandHandler()
    val runtime = createCustomRuntime(reduce, initState, marshaller, handler, PassThoughTransform())
    return Triple(runtime, marshaller, handler)
}

fun <S, U> createCustomRuntime(
    reduce: (Action, S) -> Effect<S>,
    initState: S,
    marshaller: Marshaller,
    commandHandler: CommandHandler,
    transformer: Transformer<S, U>
) = object : RuntimeKernel<S, U>(marshaller, reduce, {}, commandHandler, transformer, initState) {
    init {
        commandHandler.actionReceiver = this
    }
}