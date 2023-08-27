package org.vizhev.coribs.sample

import android.app.Application
import org.vizhev.coribs.baserib.Navigation

class App : Application(), Navigation.NavigationApp,
    Navigation.RouterContainer<Dependencies> {

    override val navigation = Navigation()

    override val router: AppRouter by lazy { createRouter() }

    override fun onCreate() {
        super.onCreate()
        navigation.setAppContext(this)
    }

    private fun createRouter(): AppRouter {
        return AppRouter(navigation)
    }
}