package app.emmabritton.notes.fsm

import app.emmabritton.android_fsm.AndroidMainThreadMarshaller
import app.emmabritton.fsm.FixedThreadPoolExecutorCommandHandler
import app.emmabritton.fsm.RuntimeKernel
import app.emmabritton.notes.flow.PresentationTransformer
import app.emmabritton.notes.flow.reduce

class AppRuntime(render: (UiState) -> Unit) : RuntimeKernel<ForegroundState, AppState, UiState>(
    AndroidMainThreadMarshaller(),
    ::reduce,
    render,
    FixedThreadPoolExecutorCommandHandler(2),
    PresentationTransformer(),
    AppState.init()
)