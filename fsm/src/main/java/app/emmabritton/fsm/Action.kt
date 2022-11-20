package app.emmabritton.fsm

/**
 * Represents a thing that happened, such UserSearchedForFood, AccessTokenWasRejected
 */
interface Action {
    /**
     * Used for debugging
     */
    fun describe(): String = this.javaClass.simpleName
}

class CommandException(val id: String, val cause: Exception) : Action {
    override fun describe(): String {
        return "CommandException: $id: ${cause.javaClass.simpleName}"
    }
}