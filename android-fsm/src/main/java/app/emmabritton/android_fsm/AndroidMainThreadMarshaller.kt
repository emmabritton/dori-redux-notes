package app.emmabritton.android_fsm

import android.os.Handler
import android.os.Looper
import app.emmabritton.fsm.Marshaller

class AndroidMainThreadMarshaller : Marshaller {
    override fun run(method: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            method()
        } else {
            Handler(Looper.getMainLooper()).post { method() }
        }
    }
}