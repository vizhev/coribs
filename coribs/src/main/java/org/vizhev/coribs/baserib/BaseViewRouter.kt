package org.vizhev.coribs.baserib

import android.os.Bundle
import android.view.ViewGroup
import androidx.transition.TransitionManager

abstract class BaseViewRouter<V : BaseViewGroup<*, VM>, VM : BaseViewModel, I : BaseInteractor, D : BaseDependencies>(
    parentRouter: BaseRouter<*, D>?
) : BaseRouter<I, D>(
    parentRouter
) {

    override val marker: Marker = Marker.MARKER_VIEW

    protected var view: V? = null
    private var viewState: Bundle? = null

    open fun attachView(
        parentViewGroup: ViewGroup,
        isInitView: Boolean = false,
        withTransition: Boolean = true
    ) {
        val baseViewGroup = getView(parentViewGroup).apply { onCreate() }
        val viewGroup = baseViewGroup.asViewGroup().apply { tag = this@BaseViewRouter.tag }
        if (withTransition) {
            baseViewGroup.openTransition?.let { transition ->
                TransitionManager.beginDelayedTransition(parentViewGroup, transition)
            }
        }
        if (isInitView) {
            parentViewGroup.addView(viewGroup, 0)
        } else {
            parentViewGroup.addView(viewGroup)
        }
        baseViewGroup.onRestoreState(viewState)
        view = baseViewGroup
    }

    open fun resumeView() {
        view?.onViewResume()
    }

    open fun pauseView() {
        view?.onViewPause()
    }

    open fun detachView(isSaveState: Boolean = true) {
        val baseViewGroup = view ?: return
        if (isSaveState) {
            viewState = baseViewGroup.onSaveState()
            baseViewGroup.onDestroy()
        }
        view = null
    }

    open fun removeView(parentViewGroup: ViewGroup, withTransition: Boolean = true) {
        view = null
        val foundView = parentViewGroup.findViewWithTag<ViewGroup>(tag)
        if (foundView is BaseViewGroup<*, *>) {
            if (withTransition) {
                foundView.closeTransition?.let { transition ->
                    TransitionManager.beginDelayedTransition(parentViewGroup, transition)
                }
            }
            foundView.onDestroy()
            parentViewGroup.removeView(foundView)
        }
    }

    abstract fun getView(parentViewGroup: ViewGroup): V

    abstract fun handleOnBackPressed(): Boolean

    @Suppress("UNCHECKED_CAST")
    protected fun getViewModel(): VM {
        return interactor as VM
    }
}