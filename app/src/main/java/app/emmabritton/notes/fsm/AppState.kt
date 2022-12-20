package app.emmabritton.notes.fsm

import app.emmabritton.fsm.Effect
import app.emmabritton.fsm.FsmForegroundState
import app.emmabritton.fsm.FsmState
import app.emmabritton.fsm.FsmUiState
import app.emmabritton.notes.flow.common.CommonState

typealias AppState = State<ForegroundState>
typealias AppEffect = Effect<ForegroundState, State<ForegroundState>>

interface ForegroundState : FsmForegroundState

interface UiState : FsmUiState

data class State<F: ForegroundState>(var currentForegroundState: F) : FsmState<F> {
    override fun getForegroundState() = currentForegroundState

    companion object {
        fun init(): AppState {
            return State(CommonState.Loading)
        }
    }
}