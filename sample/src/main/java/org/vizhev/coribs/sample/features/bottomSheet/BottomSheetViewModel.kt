package org.vizhev.coribs.sample.features.bottomSheet

import kotlinx.coroutines.flow.StateFlow
import org.vizhev.coribs.baserib.BaseViewModel
import org.vizhev.coribs.sample.features.bottomSheet.models.Intent
import org.vizhev.coribs.sample.features.bottomSheet.models.State

interface BottomSheetViewModel : BaseViewModel {

    val state: StateFlow<State>

    fun performIntent(intent: Intent)
}