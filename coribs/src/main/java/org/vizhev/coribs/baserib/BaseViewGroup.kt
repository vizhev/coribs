package org.vizhev.coribs.baserib

import android.os.Bundle
import android.view.ViewGroup
import androidx.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface BaseViewGroup<VG : ViewGroup, VM : BaseViewModel> {

    val viewScope: CoroutineScope

    val openTransition: Transition?
    val closeTransition: Transition?

    fun asViewGroup(): VG

    fun onCreate()

    fun onSaveState(): Bundle?

    fun onViewResume()

    fun onViewPause()

    fun onRestoreState(state: Bundle?)

    fun onDestroy()

    fun dispose() {
        viewScope.coroutineContext.cancelChildren()
        viewScope.cancel()
    }

    fun <T> observe(flow: Flow<T>, action: suspend (T) -> Unit): Job {
        return flow.onEach { action.invoke(it) }.launchIn(viewScope)
    }

    fun disposeObservers() {
        viewScope.coroutineContext.cancelChildren()
    }

    fun removeObservers() {
        viewScope.coroutineContext.cancelChildren()
    }
}