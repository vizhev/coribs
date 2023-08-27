package org.vizhev.coribs.sample.features.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import org.vizhev.coribs.baserib.BaseFrameLayout
import org.vizhev.sample.databinding.ViewStubBinding

@SuppressLint("ViewConstructor")
class HomeView(
    viewBinding: ViewStubBinding,
    viewModel: HomeViewModel,
    context: Context
) : BaseFrameLayout<ViewStubBinding, HomeViewModel>(viewBinding, viewModel, context) {

    @Composable
    override fun ScreenContent() {

        val state by viewModel.state.collectAsState()

        Box(modifier = Modifier.fillMaxSize().background(Color.Cyan)) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = state.text,
                fontSize = 24.sp,
                color = Color.Black
            )
        }
    }
}