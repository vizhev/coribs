package org.vizhev.coribs.sample

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import org.vizhev.coribs.baserib.Navigation
import org.vizhev.coribs.baserib.android.ViewContainer
import org.vizhev.coribs.sample.features.home.HomeRouter

class MainActivity : AppCompatActivity(),
    ViewContainer, Navigation.RouterContainer<Dependencies> {

    override val router by lazy { createRouter() }

    override val activity: AppCompatActivity = this

    override val rootView: ViewGroup by lazy {
        FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    private val navigation by lazy { (application as Navigation.NavigationApp).navigation }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootView)
        navigation.setRootViewComponent(router)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigation.onBackPressed()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        navigation.attachViews(this)
    }

    override fun onStop() {
        super.onStop()
        navigation.detachViews()
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            navigation.destroyViewsComponents()
        }
        super.onDestroy()
        if (navigation.isRestartActivity) {
            navigation.isRestartActivity = false
            val intent = Intent(this, this::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    private fun createRouter(): HomeRouter {
        val parentRouter = (application as Navigation.RouterContainer<Dependencies>).router
        return HomeRouter(parentRouter)
    }
}