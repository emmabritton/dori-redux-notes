package app.emmabritton.notes.flow.common

import app.emmabritton.notes.fsm.UiState

sealed class CommonUiState : UiState {
    object Loading : CommonUiState()
}