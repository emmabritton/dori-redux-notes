package app.emmabritton.notes.flow

import app.emmabritton.fsm.Transformer
import app.emmabritton.notes.flow.common.CommonState
import app.emmabritton.notes.flow.common.CommonUiState
import app.emmabritton.notes.fsm.AppState
import app.emmabritton.notes.fsm.ForegroundState
import app.emmabritton.notes.fsm.UiState

class PresentationTransformer : Transformer<ForegroundState, AppState, UiState> {
    override fun transform(state: AppState): UiState {
        return when (state.getForegroundState()) {
            CommonState.Loading -> CommonUiState.Loading
            else -> CommonUiState.Loading //TODO replace with error or throw
        }
    }
}