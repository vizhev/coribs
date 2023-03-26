package org.vizhev.coribs.baserib

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.AttributeSet
import android.view.animation.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseFrameLayout<VB : ViewBinding, VM : BaseViewModel>(
    protected val viewBinding: VB,
    protected val viewModel: VM,
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attr, defStyleAttr), BaseViewGroup<FrameLayout, VM> {

    private val viewScopeDelegate =
        lazy { CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()) }
    override val viewScope by viewScopeDelegate

    private var activity: AppCompatActivity? = null
    protected var savedState: Bundle? = null

    init {
        isFocusable = true
        isClickable = true
    }

    override val openTransition: Transition? = Fade(Fade.IN).apply {
        addTarget(this@BaseFrameLayout)
        translationX = 0f
        translationY = 0f
        duration = 170
    }
    override val closeTransition: Transition? = Fade(Fade.OUT).apply {
        addTarget(this@BaseFrameLayout)
        translationX = this@BaseFrameLayout.width.toFloat()
        translationY = this@BaseFrameLayout.height.toFloat()
        duration = 170
    }

    override fun asViewGroup(): FrameLayout {
        return this
    }

    override fun onCreate() {
        addView(viewBinding.root)
        addView(ComposeView(context).apply { setContent { ScreenContent() } })
    }

    override fun onSaveState(): Bundle? {
        if (savedState == null) {
            savedState = Bundle()
        }
        return savedState
    }

    override fun onViewResume() {

    }

    override fun onViewPause() {

    }

    override fun onRestoreState(state: Bundle?) {
        savedState = state
    }

    override fun onDestroy() {
        activity = null
        if (viewScopeDelegate.isInitialized()) {
            dispose()
        }
    }

    /*fun makeOpenCloseAnimation(
        startScale: Float,
        endScale: Float,
        startAlpha: Float,
        endAlpha: Float
    ): AnimationSet {
        val set = AnimationSet(false)
        val scale = ScaleAnimation(
            startScale, endScale, startScale, endScale,
            Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f
        )
        scale.interpolator = DecelerateInterpolator(2.5f)
        scale.duration = 220
        set.addAnimation(scale)
        val alpha = AlphaAnimation(startAlpha, endAlpha)
        alpha.interpolator = DecelerateInterpolator(1.5f)
        alpha.duration = 220
        set.addAnimation(alpha)
        return set
    }

    fun makeOpenAnimation(): AnimationSet {
        val set = AnimationSet(false)
        val scale = ScaleAnimation(
            1.125f, 1.0f, 1.125f, 1.0f,
            Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f
        )
        scale.interpolator = DecelerateInterpolator(2.5f)
        scale.duration = 220
        set.addAnimation(scale)
        val alpha = AlphaAnimation(0f, 1f)
        alpha.interpolator = DecelerateInterpolator(1.5f)
        alpha.duration = 220
        set.addAnimation(alpha)
        return set
    }*/

    fun makeCloseAnimation(startAlpha: Float, endAlpha: Float): AnimationSet {
        val set = AnimationSet(false)
        val scale = ScaleAnimation(
            .975f, 1.0f, .975f, 1.0f,
            Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f
        )
        scale.interpolator = DecelerateInterpolator(2.5f)
        scale.duration = 220
        set.addAnimation(scale)
        val alpha = AlphaAnimation(0f, 1f)
        alpha.interpolator = DecelerateInterpolator(1.5f)
        alpha.duration = 220
        set.addAnimation(alpha)
        return set
    }

    protected fun requireActivity(): AppCompatActivity {
        val activity = this.activity
        if (activity != null) {
            return activity
        }
        var context: Context = this.context
        while (context is ContextWrapper) {
            if (context is AppCompatActivity) {
                this.activity = context
                return context
            }
            context = context.baseContext
        }
        throw IllegalStateException("AppCompatActivity not found")
    }

    @Composable
    open fun ScreenContent() {

    }
}