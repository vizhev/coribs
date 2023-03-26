package org.vizhev.coribs.baserib

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.transition.Transition
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.vizhev.coribs.R
import org.vizhev.coribs.bottomsheet.CustomBottomSheetBehavior

/**
 * Bottom Sheet ViewGroup implementation.
 */
abstract class BaseBottomSheet<VB : ViewBinding, VM : BaseViewModel>(
    protected val viewBinding: VB,
    protected val viewModel: VM,
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleRes), BaseViewGroup<FrameLayout, VM> {

    companion object {
        /**
         * Background color.
         */
        private const val BACKGROUND_COLOR: String = "#99000000"
    }

    private val viewScopeDelegate =
        lazy { CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()) }
    override val viewScope by viewScopeDelegate

    /**
     * Transitions not required
     */
    override val openTransition: Transition? = null
    override val closeTransition: Transition? = null

    /**
     * Sheet margin top. Sheet will never be higher than parent height minus sheetMarginTopPx.
     */
    open val sheetMarginTopPx = 0

    /**
     * If false call {@link #open()} required.
     * */
    open val shouldSheetOpenImmediately: Boolean = true

    /**
     * If false call {@link #dismiss()} required.
     * */
    open val shouldSheetDismissOnClickOutside: Boolean = true

    open val shouldShowDragPinView: Boolean = true

    /**
     *  View that showed in background before call {@link #open()}
     * */
    open val loadingView: View? = null

    /**
     * Layout for hold sheet content.
     */
    private val coordinatorLayout by lazy { CoordinatorLayout(context) }

    /**
     * ViewGroup with sheet content.
     */
    private val sheet: ViewGroup by lazy {
        val contentLayout = FrameLayout(context).apply {
            this.layoutParams = CoordinatorLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM
            }
        }

        contentLayout.addView(viewBinding.root)
        contentLayout.addView(ComposeView(context).apply { setContent { ScreenContent() } })
        //contentLayout.background = ContextCompat.getDrawable(context, R.drawable.bg_cornered_dialog)

        /*val dragPinWidth = resources.getDimension(R.dimen._28sdp).roundToInt()
        val dragPinHeight = resources.getDimension(R.dimen._5sdp).roundToInt()
        val dragPinTopMargin = resources.getDimension(R.dimen._6sdp).roundToInt()
        val dragPinBottomMargin = resources.getDimension(R.dimen._13sdp).roundToInt()
        val dragPinView = View(context).apply {
            background = ContextCompat.getDrawable(context, R.drawable.bottom_sheet_drag_pin)
        }*/

        //TODO
        val dragPinWidth = (37 * resources.displayMetrics.density).toInt()
        val dragPinHeight = (4 * resources.displayMetrics.density).toInt()
        val dragPinTopMargin = (8 * resources.displayMetrics.density).toInt()
        val dragPinBottomMargin = (16 * resources.displayMetrics.density).toInt()
        val dragPinView = View(context).apply {
            background = ContextCompat.getDrawable(context, R.drawable.bottom_sheet_drag_pin)
        }
        if (shouldShowDragPinView) {
            contentLayout.addView(dragPinView, LayoutParams(dragPinWidth, dragPinHeight).apply {
                this.setMargins(0, dragPinTopMargin, 0, dragPinBottomMargin)
                this.gravity = Gravity.CENTER_HORIZONTAL
            })
        }

        return@lazy contentLayout
    }

    /**
     * Content layout behavior for implement Bottom Sheet animations.
     */
    private val sheetBehavior by lazy {
        CustomBottomSheetBehavior.from(sheet).apply { isHideable = true }
    }

    /**
     * Sheet transparent background view.
     */
    private val sheetBackground by lazy {
        FrameLayout(context).apply {
            this.isFocusable = true
            this.isClickable = true
            this.alpha = 0f
            this.isSoundEffectsEnabled = false
            this.background = ColorDrawable(Color.parseColor(BACKGROUND_COLOR))
            if (shouldSheetDismissOnClickOutside) {
                this.setOnClickListener {
                    if (sheetBehavior.state == CustomBottomSheetBehavior.STATE_HIDDEN) {
                        isDismissed = true
                        this.animate()
                            .alpha(0f)
                            .setDuration(190)
                            .withEndAction { dismiss() }
                            .start()
                    } else {
                        dismiss()
                    }
                }
            }
            loadingView?.let {
                it.visibility = View.GONE
                this.addView(it)
            }
        }
    }

    /**
     * Listener which shows background with animation when view is ready to draw animations.
     *
     * Note: View::onLayout() callback isn't good place for it because card animation don't work
     * properly in this case.
     */
    private val onShowBackgroundListener: ViewTreeObserver.OnGlobalLayoutListener =
        ViewTreeObserver.OnGlobalLayoutListener {
            sheetBackground.animate()
                .alpha(1f)
                .setDuration(190)
                .withEndAction {
                    if (sheetBehavior.state == CustomBottomSheetBehavior.STATE_HIDDEN) {
                        loadingView?.visibility = View.VISIBLE
                    }
                }
                .start()

            // Need to be removed when it was triggered once because OnGlobalLayout callback
            // triggers many times.
            removeShowBackgroundListener()
        }

    /**
     * Listener which shows bottom sheet with animation when view is ready to draw animations.
     *
     * Note: View::onLayout() callback isn't good place for it because card animation don't work
     * properly in this case.
     */
    private val onShowSheetBehaviorListener: ViewTreeObserver.OnGlobalLayoutListener =
        ViewTreeObserver.OnGlobalLayoutListener {
            if (sheetBehavior.state == CustomBottomSheetBehavior.STATE_HIDDEN) {
                loadingView?.visibility = View.GONE
                sheetBackground.clearAnimation()
                sheetBehavior.state = CustomBottomSheetBehavior.STATE_EXPANDED
            }

            // Need to be removed when it was triggered once because OnGlobalLayout callback
            // triggers many times. Therefore it can open sheet again when card is hidden on dismiss
            // but view isn't removed at this moment.
            removeShowSheetBehaviorListener()
        }

    /*
    * Flag for stop open sheet while {@link #sheetBackground} animating dismiss
    * */
    private var isDismissed: Boolean = false

    protected var savedState: Bundle? = null

    init {
        isFocusable = true
        isClickable = true
    }

    // FIXME When user minimises app with expanded sheet and he returns to app later sheet expands
    //  with animation again.
    override fun onCreate() {
        addView(sheetBackground, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(coordinatorLayout, LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
            setMargins(0, sheetMarginTopPx, 0, 0)
        })

        coordinatorLayout.addView(sheet, sheet.layoutParams.apply {
            this as CoordinatorLayout.LayoutParams
            this.behavior = CustomBottomSheetBehavior<View>(context, null)
            this.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        })

        sheetBehavior.state = CustomBottomSheetBehavior.STATE_HIDDEN
        sheetBehavior.addBottomSheetCallback(BottomSheetDismissCallback())

        if (shouldSheetOpenImmediately) {
            open()
        } else {
            viewTreeObserver.addOnGlobalLayoutListener(onShowBackgroundListener)
        }
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
        if (viewScopeDelegate.isInitialized()) {
            dispose()
        }
        removeShowBackgroundListener()
        removeShowSheetBehaviorListener()
    }

    override fun asViewGroup() = this

    /**
     * View callback when dismiss animation ends.
     */
    abstract fun onDismissed()

    fun open() {
        if (isDismissed || sheetBehavior.state == CustomBottomSheetBehavior.STATE_EXPANDED) {
            return
        }
        viewTreeObserver.addOnGlobalLayoutListener(onShowSheetBehaviorListener)
        requestLayout()
    }

    /**
     * Do sheet dismiss with animation if sheet expanded.
     */
    fun dismiss() {
        if (sheetBehavior.state == CustomBottomSheetBehavior.STATE_HIDDEN) {
            onDismissed()
            return
        }
        sheetBehavior.state = CustomBottomSheetBehavior.STATE_HIDDEN
    }

    fun isSheetOpen(): Boolean {
        return sheetBehavior.state == CustomBottomSheetBehavior.STATE_EXPANDED
    }

    fun setSwipeEnabled(isEnabled: Boolean) {
        sheetBehavior.isSwipeEnabled = isEnabled
    }

    @Composable
    open fun ScreenContent() {

    }

    protected fun onOpened() {

    }

    protected fun requireActivity(): AppCompatActivity {
        var context: Context = this.context
        while (context is ContextWrapper) {
            if (context is AppCompatActivity) {
                return context
            }
            context = context.baseContext
        }
        throw IllegalStateException("AppCompatActivity not found")
    }

    private fun removeShowSheetBehaviorListener() {
        viewTreeObserver.removeOnGlobalLayoutListener(onShowSheetBehaviorListener)
    }

    private fun removeShowBackgroundListener() {
        viewTreeObserver.removeOnGlobalLayoutListener(onShowBackgroundListener)
    }

    private inner class BottomSheetDismissCallback :
        CustomBottomSheetBehavior.BottomSheetCallback() {

        private var isOpened = false

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == CustomBottomSheetBehavior.STATE_HIDDEN) {
                onDismissed()
            }
            if (!isOpened && newState == CustomBottomSheetBehavior.STATE_EXPANDED) {
                isOpened = true
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (slideOffset == 1f) {
                onOpened()
            }
            if (!shouldSheetOpenImmediately && !isOpened) {
                return
            }
            sheetBackground.alpha = slideOffset + 1f
        }
    }
}