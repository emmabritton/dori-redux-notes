package app.emmabritton.notes.flow.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.emmabritton.fsm.ActionReceiver

@Composable
fun RenderCommonUi(state: CommonUiState, actionReceiver: ActionReceiver, modifier: Modifier = Modifier) {
    when (state) {
        CommonUiState.Loading -> GenericLoadingUi(modifier)
    }
}