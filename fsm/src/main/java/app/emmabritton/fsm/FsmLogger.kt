package app.emmabritton.fsm

/**
 * Global static logger used in the FSM to bypass DI or passing it everywhere
 *
 * Will do nothing if not initialised so it's safe in tests
 *
 * To setup, write something like
 * ```
 * FsmLogger.debug = { Timber.d(it) }
 * FsmLogger.error = { Timber.e(it) }
 * FsmLogger.exception = { ex,msg -> Timber.e(ex, msg) }
 * ```
 *
 * In [Command][app.emmabritton.fsm.Command]s, [Middleware][app.emmabritton.fsm.Middleware]s, etc use [get] to get an instance, e.g.
 * `FsmLogger.get(Source.from(this))`
 */
object FsmLogger {
    var debug: (String) -> Unit = {}
    var error: (String) -> Unit = {}
    var exception: (Throwable?, String) -> Unit = { _, _ -> }

    /**
     * Creates a [Logger] instance that is setup to format messages correctly for the target class
     */
    fun get(source: Source) = Logger(source, debug, error, exception)
}

class Logger(
    private val source: Source,
    private val debug: (String) -> Unit,
    private val error: (String) -> Unit,
    private val exception: (Throwable?, String) -> Unit
) {
    /**
     * Log debug message
     */
    fun d(msg: String) = debug(format(msg))
    /**
     * Log error message
     */
    fun e(msg: String) = error(format(msg))
    /**
     * Log error message with optional throwable
     */
    fun e(throwable: Throwable?, msg: String) = exception(throwable, format(msg))

    private fun format(msg: String) = "[$source] $msg"
}

/**
 * Source of log message
 * Used for formatting in logs
 */
sealed class Source {
    object Runtime : Source()
    class Command(val name: String) : Source()
    class Middleware(val name: String) : Source()

    override fun toString(): String {
        return when (this) {
            is Command -> "CD-$name"
            is Middleware -> "MW-$name"
            Runtime -> "RT"
        }
    }

    companion object {
        fun from(command: app.emmabritton.fsm.Command) = Command(command.name())
        fun from(middleware: app.emmabritton.fsm.Middleware<*,*>) = Middleware(middleware::class.java.simpleName)
    }
}