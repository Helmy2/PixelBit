package com.example.pixelbit

import android.app.Application
import com.example.pixelbit.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class PixelBitApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PixelBitApplication)
            modules(appModule)
        }
    }
}
