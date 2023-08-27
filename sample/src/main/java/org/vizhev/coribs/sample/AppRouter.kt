package org.vizhev.coribs.sample

import org.vizhev.coribs.baserib.BaseRouter
import org.vizhev.coribs.baserib.Navigation
//import org.vizhev.coribs.sample.features.home.HomeRouter

class AppRouter(navigation: Navigation) : Az, BaseRouter<AppInteractor, Dependencies>(
    navigation = navigation
) {

    override fun createInteractor(): AppInteractor {
        return AppInteractor()
    }
}

interface Az