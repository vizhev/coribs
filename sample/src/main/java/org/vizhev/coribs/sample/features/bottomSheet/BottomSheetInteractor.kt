package org.vizhev.coribs.sample.features.bottomSheet

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.vizhev.coribs.baserib.BaseInteractor
import org.vizhev.coribs.sample.data.SomeRepository
import org.vizhev.coribs.sample.features.bottomSheet.models.Intent
import org.vizhev.coribs.sample.features.bottomSheet.models.State

class BottomSheetInteractor(
    private val someRepository: SomeRepository
) : BaseInteractor(), BottomSheetViewModel {

    override val state = MutableStateFlow(State())

    private val router by lazy { getRouter<BottomSheetRouter>() }

    override fun init() {
        super.init()
        interactorScope.launch {
            delay(2700)
            state.value = state.value.copy(
                text = someRepository.getGreeting()
            )
        }
    }

    override fun performIntent(intent: Intent) {
        router.exit()
    }
}