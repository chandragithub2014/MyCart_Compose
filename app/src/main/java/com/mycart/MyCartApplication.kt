package com.mycart

import android.app.Application
import com.mycart.data.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import com.facebook.stetho.Stetho

class MyCartApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Koin
        startKoin {
            androidContext(this@MyCartApplication)
            modules(appModule)
        }
        Stetho.initializeWithDefaults(this)
    }

    override fun onTerminate() {
        super.onTerminate()

        // Stop Koin
        stopKoin()
    }
}