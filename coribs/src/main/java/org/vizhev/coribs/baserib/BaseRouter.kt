package org.vizhev.coribs.baserib

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class BaseRouter<I : BaseInteractor, D : BaseDependencies>(
    parentRouter: BaseRouter<*, D>? = null,
    protected val dependencies: D? = parentRouter?.dependencies,
    protected val navigation: Navigation = parentRouter?.navigation ?: Navigation(),
    protected val events: MutableSharedFlow<Any> = parentRouter?.events ?: MutableSharedFlow(
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
) {

    @Suppress("UNCHECKED_CAST")
    protected val interactor: I by lazy {
        createInteractor().attachRouter(this).apply { init() } as I
    }

    /*
    * Mark router for navigation:
    * MARKER_DEFAULT - router with some feature logic (Interactor) without view and not depend of UI lifecycle.
    * Not remove from stack if Activity was closed.
    * MARKER_UI - router with some feature logic (Interactor) without view but depend of UI lifecycle.
    * Will be removed from stack if Activity was closed.
    * MARKER_VIEW - router with some feature logic (Interactor) and with View + implement ViewModel.
    * Will be removed from stack if Activity was closed. View will be attached and detached from activity lifecycle.
    * */
    enum class Marker {
        MARKER_DEFAULT,
        MARKER_UI,
        MARKER_VIEW
    }

    private var customTag: String? = null
    open val tag: String
        get() = customTag ?: this::class.qualifiedName!!

    open val marker: Marker = Marker.MARKER_DEFAULT

    open fun onExit() {
        interactor.onCleared()
    }

    fun setTag(tag: String) {
        customTag = tag
    }

    abstract fun createInteractor(): I
}