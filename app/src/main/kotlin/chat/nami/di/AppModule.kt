package chat.nami.di

import android.content.Context
import chat.nami.auth.di.AuthModule
import chat.nami.chat.di.ChatModule
import chat.nami.chat.history.di.ChatHistoryModule
import chat.nami.settings.di.SettingsModule

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

    val settingsModule by lazy {
        SettingsModule(applicationContext)
    }

    companion object {
        lateinit var instance: AppModule

        fun create(applicationContext: Context) {
            instance = AppModule(applicationContext)
        }
    }
}

val appModule = AppModule.instance
