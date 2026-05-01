package chat.nami.di

import android.content.Context
import chat.nami.auth.di.AuthModule

class AppModule private constructor(applicationContext: Context) {

    val authModule by lazy {
        AuthModule(applicationContext)
    }

    companion object {
        lateinit var instance: AppModule

        fun create(applicationContext: Context) {
            instance = AppModule(applicationContext)
        }
    }
}

val appModule = AppModule.instance
