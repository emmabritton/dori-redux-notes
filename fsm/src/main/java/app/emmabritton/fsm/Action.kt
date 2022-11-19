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

class CommandException(val name: String, val cause: Exception) : Action {
    override fun describe(): String {
        return "CommandException: $name: ${cause.javaClass.simpleName}"
    }
}