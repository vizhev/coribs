package org.vizhev.coribs.baserib

abstract class BaseRouter<I : BaseInteractor, C : BaseComponent<I>>(val interactor: I) {

    enum class Marker {
        MARKER_DEFAULT,
        MARKER_UI,
        MARKER_VIEW
    }

    private var customTag: String? = null
    open val tag: String
        get() = customTag ?: this::class.java.simpleName

    open val marker: Marker = Marker.MARKER_DEFAULT

    open fun onExit() {
        interactor.onCleared()
    }

    fun setTag(tag: String) {
        customTag = tag
    }

    fun attachRouter() {
        interactor.attachRouter(this)
    }
}