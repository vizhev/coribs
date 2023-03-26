package org.vizhev.coribs.baserib.android

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

interface ViewContainer {

    val activity: AppCompatActivity

    val parent: ViewGroup
}