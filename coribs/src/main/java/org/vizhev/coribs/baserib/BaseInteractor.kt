package org.vizhev.coribs.baserib

import kotlinx.coroutines.*

abstract class BaseInteractor {

    private val interactorScopeDelegate =
        lazy { CoroutineScope(Dispatchers.Default + SupervisorJob()) }
    val interactorScope: CoroutineScope by interactorScopeDelegate

    private lateinit var router: BaseRouter<*, *>

    fun attachRouter(router: BaseRouter<*, *>): BaseInteractor {
        this.router = router
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <R : BaseRouter<*, *>> getRouter(): R {
        return router as R
    }

    open fun init() {

    }

    open fun onCleared() {
        if (interactorScopeDelegate.isInitialized()) {
            interactorScope.coroutineContext.cancelChildren()
            interactorScope.cancel()
        }
    }
}