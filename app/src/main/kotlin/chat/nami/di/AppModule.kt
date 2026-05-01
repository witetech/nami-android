package chat.nami.di

import android.content.Context
import chat.nami.auth.di.AuthModule
import chat.nami.chat.di.ChatModule
import chat.nami.chat.history.di.ChatHistoryModule

class AppModule private constructor(applicationContext: Context) {

    val authModule by lazy {
        AuthModule(applicationContext)
    }

    val chatModule by lazy {
        ChatModule(applicationContext)
    }

    val chatHistoryModule by lazy {
        ChatHistoryModule(applicationContext)
    }

    companion object {
        lateinit var instance: AppModule

        fun create(applicationContext: Context) {
            instance = AppModule(applicationContext)
        }
    }
}

val appModule = AppModule.instance
