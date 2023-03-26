package org.vizhev.coribs.baserib

interface BaseComponent<I : BaseInteractor> {

    fun getInteractor(): I

}