package org.vizhev.coribs.sample.features.bottomSheet

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vizhev.coribs.baserib.BaseBottomSheet
import org.vizhev.coribs.sample.features.bottomSheet.models.Intent
import org.vizhev.sample.databinding.ViewStubBinding

@SuppressLint("ViewConstructor")
class BottomSheeetView(
    viewBinding: ViewStubBinding,
    viewModel: BottomSheetViewModel,
    context: Context
) : BaseBottomSheet<ViewStubBinding, BottomSheetViewModel>(viewBinding, viewModel, context) {

    override val shouldSheetOpenImmediately = false
    override val sheetMarginTopPx by lazy { resources.dpToPixelsInt(42) }
    override val loadingView = ComposeView(context).apply {
        layoutParams = LayoutParams(
            resources.dpToPixelsInt(60),
            resources.dpToPixelsInt(60)
        ).apply { gravity = Gravity.CENTER }
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    color = Color.Green
                )
            }
        }
    }

    @Composable
    override fun ScreenContent() {

        val state by viewModel.state.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Magenta)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = state.text.orEmpty(),
                fontSize = 24.sp,
                color = Color.Black
            )
        }

        LaunchedEffect(key1 = state.text != null) {
            if (state.text != null) {
                open()
            }
        }
    }

    override fun onDismissed() {
        viewModel.performIntent(Intent.OnDismissed)
    }
}

fun Resources.dpToPixelsInt(dp: Int): Int {
    return (dp * displayMetrics.density + 0.5f).toInt()
}
