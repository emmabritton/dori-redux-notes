package app.emmabritton.notes.flow

import app.emmabritton.fsm.Action
import app.emmabritton.notes.fsm.AppEffect
import app.emmabritton.notes.fsm.AppState

fun reduce(action: Action, state: AppState) : AppEffect {
    //TODO raise exception if unhandled
    return AppEffect(state, emptyList())
}