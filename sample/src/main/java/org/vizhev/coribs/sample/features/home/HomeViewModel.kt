package org.vizhev.coribs.sample.features.home

import kotlinx.coroutines.flow.StateFlow
import org.vizhev.coribs.baserib.BaseViewModel
import org.vizhev.coribs.sample.features.home.models.Intent
import org.vizhev.coribs.sample.features.home.models.State

interface HomeViewModel : BaseViewModel {

    val state: StateFlow<State>

    fun performIntent(intent: Intent)
}