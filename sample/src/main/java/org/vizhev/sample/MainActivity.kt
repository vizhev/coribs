package org.vizhev.sample

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import org.vizhev.coribs.baserib.Navigation
import org.vizhev.coribs.baserib.android.ViewContainer

class MainActivity : AppCompatActivity(), ViewContainer {

    override val activity: AppCompatActivity = this

    override val parent: ViewGroup = FrameLayout(this).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Navigation.onBackPressed()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Navigation.attachViews(this)
    }

    override fun onStop() {
        super.onStop()
        Navigation.detachViews()
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            Navigation.destroyViewsComponents()
        }
        super.onDestroy()
    }
}