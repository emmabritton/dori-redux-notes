package app.emmabritton.notes

import android.app.Application
import app.emmabritton.fsm.FsmLogger
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class NotesApp : Application() {
    override fun onCreate() {
        super.onCreate()

        FsmLogger.debug = { Timber.d(it) }
        FsmLogger.error = { Timber.e(it) }
        FsmLogger.exception = { ex,msg -> Timber.e(ex, msg) }

        startKoin {
            logger(AndroidLogger())
        }
    }
}