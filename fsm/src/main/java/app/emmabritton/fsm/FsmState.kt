package app.emmabritton.fsm

interface FsmState<F : FsmForegroundState> {
    fun getForegroundState(): F
}

interface FsmForegroundState {
    fun getForegroundCommands(): List<Command>
    fun getCommandIdsToCancelOnBackground(): List<CommandId>
}

interface FsmUiState