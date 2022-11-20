package app.emmabritton.fsm

interface State<F : ForegroundState> {
    fun getForegroundState(): F
}

interface ForegroundState {
    fun getForegroundCommands(): List<Command>
    fun getCommandIdsToCancelOnBackground(): List<CommandId>
}

interface UiState