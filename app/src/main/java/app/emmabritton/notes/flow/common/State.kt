package app.emmabritton.notes.flow.common

import app.emmabritton.fsm.Command
import app.emmabritton.fsm.CommandId
import app.emmabritton.notes.fsm.ForegroundState

sealed class CommonState : ForegroundState {
    object Loading : CommonState() {
        override fun getForegroundCommands() = emptyList<Command>()
        override fun getCommandIdsToCancelOnBackground() = emptyList<CommandId>()
    }
}