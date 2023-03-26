package org.vizhev.sample

import android.app.Application
import org.vizhev.coribs.baserib.Navigation

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Navigation.setAppContext(this)
    }
}