package org.vizhev.coribs.baserib

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import org.vizhev.coribs.baserib.android.ViewContainer
import java.util.concurrent.CopyOnWriteArrayList

@SuppressLint("StaticFieldLeak")
object Navigation {

    lateinit var appContext: Context
        private set

    var activity: AppCompatActivity? = null
        private set

    private val components = CopyOnWriteArrayList<BaseRouter<*, *>>()
    private var rootViewGroup: ViewGroup? = null

    fun setAppContext(appContext: Context) {
        this.appContext = appContext
    }

    fun setRootViewComponent(viewRouter: BaseViewRouter<*, *, *, *>) {
        val viewsRoutersList = components.filterIsInstance<BaseViewRouter<*, *, *, *>>()
        rootViewGroup?.let { parentView ->
            viewRouter.attachView(parentView, true)
        }
        val isNoViews = viewsRoutersList.isEmpty()
        if (isNoViews) {
            viewRouter.resumeView()
            components.add(viewRouter)
        } else {
            val viewRouterIndex = components.indexOf(viewsRoutersList[0])
            components.add(viewRouterIndex, viewRouter)
        }
    }

    @JvmOverloads
    fun addComponent(router: BaseRouter<*, *>, isSingleInstance: Boolean = true) {
        if (isSingleInstance && isContainsComponent(router.tag)) {
            return
        }
        if (router is BaseViewRouter<*, *, *, *>) {
            val parentView = rootViewGroup
            if (parentView != null) {
                // Activity is visible now
                val componentLength = components.size - 1
                for (i in componentLength downTo 0) {
                    val baseRouter = components[i]
                    if (baseRouter.marker == BaseRouter.Marker.MARKER_VIEW) {
                        (baseRouter as BaseViewRouter<*, *, *, *>).pauseView()
                        break
                    }
                }
                router.attachView(parentView)
                router.resumeView()
            }
        }
        components.add(router)
    }

    //TODO need refactoring
    fun replaceViewComponent(router: BaseViewRouter<*, *, *, *>) {
        val componentLength = components.size - 1
        for (i in componentLength downTo 0) {
            val baseRouter = components[i]
            if (baseRouter is BaseViewRouter<*, *, *, *>) {
                removeViewComponent(baseRouter)
                continue
            }
            if (baseRouter.marker == BaseRouter.Marker.MARKER_UI) {
                removeComponent(baseRouter)
            }
        }
        addComponent(router)
    }

    fun backToViewComponent(tag: String): Boolean {
        val requiredRouter = components.find { it.tag == tag }
        if (requiredRouter == null || requiredRouter !is BaseViewRouter<*, *, *, *>) {
            return false
        }
        val componentLength = components.size - 1
        var isNeedAnimation = false
        for (i in componentLength downTo 0) {
            if (i >= components.size) {
                return false
            }
            val baseRouter = components[i]
            if (baseRouter is BaseViewRouter<*, *, *, *>) {
                if (baseRouter.tag != tag) {
                    if (isNeedAnimation) {
                        isNeedAnimation = false
                    }
                    removeViewComponent(baseRouter, isNeedAnimation)
                    continue
                }
                return true
            }
            if (baseRouter.marker == BaseRouter.Marker.MARKER_UI) {
                removeComponent(baseRouter)
            }
        }
        return false
    }

    fun removeViewComponent(
        router: BaseViewRouter<*, *, *, *>,
        withTransitionAnimation: Boolean = true
    ) {
        router.pauseView()
        router.detachView(false)
        rootViewGroup?.let { parentView ->
            router.removeView(parentView, withTransitionAnimation)
        }
        router.onExit()
        var isRemoved = false
        val componentLength = components.size - 1
        for (i in componentLength downTo 0) {
            val baseRouter = components[i]
            if (isRemoved && baseRouter is BaseViewRouter<*, *, *, *>) {
                baseRouter.resumeView()
                return
            }
            if (baseRouter.tag == router.tag) {
                components.removeAt(i)
                isRemoved = true
            }
        }
    }

    fun removeViewComponent(tag: String) {
        val requiredRouter = findComponent(tag) ?: return
        if (requiredRouter is BaseViewRouter<*, *, *, *>) {
            removeViewComponent(requiredRouter)
        }
    }

    fun removeComponent(tag: String) {
        val requiredRouter = findComponent(tag) ?: return
        removeComponent(requiredRouter)
    }

    fun removeComponent(router: BaseRouter<*, *>) {
        if (router is BaseViewRouter<*, *, *, *>) {
            throw IllegalArgumentException("For BaseViewRouter must be use removeViewComponent() function")
        }
        router.onExit()
        components.remove(router)
    }

    fun findComponent(tag: String): BaseRouter<*, *>? {
        return components.find { it.tag == tag }
    }

    fun isContainsComponent(tag: String): Boolean {
        return components.findLast { it.tag == tag } != null
    }

    fun isCurrentViewComponent(tag: String): Boolean {
        val componentLength = components.size - 1
        for (i in componentLength downTo 0) {
            val baseRouter = components[i]
            if (baseRouter is BaseViewRouter<*, *, *, *>) {
                return baseRouter.tag == tag
            }
            continue
        }
        return false
    }

    fun attachViews(viewContainer: ViewContainer) {
        this.activity = viewContainer.activity
        this.rootViewGroup = viewContainer.parent
        var lastBaseViewRouter: BaseViewRouter<*, *, *, *>? = null
        components.forEach { baseRouter ->
            if (baseRouter is BaseViewRouter<*, *, *, *>) {
                lastBaseViewRouter = baseRouter
                baseRouter.attachView(viewContainer.parent, withTransition = false)
            }
        }
        lastBaseViewRouter?.resumeView()
    }

    fun detachViews() {
        var isViewPaused = false
        val componentLength = components.size - 1
        for (i in componentLength downTo 0) {
            val baseRouter = components[i]
            if (baseRouter is BaseViewRouter<*, *, *, *>) {
                if (!isViewPaused) {
                    baseRouter.pauseView()
                    isViewPaused = true
                }
                baseRouter.detachView()
            }
        }
        rootViewGroup?.removeAllViews()
        rootViewGroup = null
        //activity = null
    }

    fun destroyViewsComponents() {
        val componentLength = components.size - 1
        for (i in componentLength downTo 0) {
            val baseRouter = components[i]
            if (baseRouter is BaseViewRouter<*, *, *, *>) {
                baseRouter.pauseView()
                baseRouter.detachView(false)
                baseRouter.onExit()
                components.removeAt(i)
                continue
            }
            if (baseRouter.marker == BaseRouter.Marker.MARKER_UI) {
                baseRouter.onExit()
                components.removeAt(i)
            }
        }
        rootViewGroup?.removeAllViews()
        rootViewGroup = null
        activity = null
    }

    fun destroyComponents() {
        destroyViewsComponents()
        components.forEach { baseRouter ->
            baseRouter.onExit()
        }
        components.clear()
    }

    fun onBackPressed() {
        val componentLength = components.size - 1
        for (i in componentLength downTo 0) {
            val baseRouter = components[i]
            if (baseRouter is BaseViewRouter<*, *, *, *> && baseRouter.handleOnBackPressed()) {
                return
            }
        }
    }

    fun lockUi() {
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
    }

    fun unlockUi() {
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
    }
}