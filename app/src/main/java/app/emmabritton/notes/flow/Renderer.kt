package app.emmabritton.notes.flow

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.emmabritton.fsm.ActionReceiver
import app.emmabritton.notes.flow.common.CommonUiState
import app.emmabritton.notes.flow.common.RenderCommonUi
import app.emmabritton.notes.fsm.UiState

@Composable
fun Render(state: UiState, actionReceiver: ActionReceiver, modifier: Modifier = Modifier) {
    when (state) {
        is CommonUiState -> RenderCommonUi(state, actionReceiver, modifier)
        else -> {
            //TODO throw exception?
        }
    }
}