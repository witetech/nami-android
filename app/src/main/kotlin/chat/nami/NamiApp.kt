package chat.nami

import android.app.Application
import chat.nami.di.AppModule

class NamiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule.create(this)
    }
}
