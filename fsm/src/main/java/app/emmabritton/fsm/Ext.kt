package app.emmabritton.fsm

import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun ExecutorService.shutdownAndWait(seconds: Long): Boolean {
    shutdown()
    return awaitTermination(seconds, TimeUnit.SECONDS)
}

fun ExecutorService.shutdownAndWaitOrThrow(name: String, seconds: Long) {
    if (!shutdownAndWait(seconds)) {
        throw TimeoutException("Timeout occurred after waiting ${seconds}s for $name to complete")
    }
}