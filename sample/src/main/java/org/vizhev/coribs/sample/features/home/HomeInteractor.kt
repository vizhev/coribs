package org.vizhev.coribs.sample.features.home

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.vizhev.coribs.baserib.BaseInteractor
import org.vizhev.coribs.sample.data.SomeRepository
import org.vizhev.coribs.sample.features.home.models.State

class HomeInteractor(
    private val someRepository: SomeRepository
) : BaseInteractor(), HomeViewModel {

    override val state = MutableStateFlow(State("Loading data..."))

    override fun init() {
        super.init()
        interactorScope.launch {
            delay(2700)
            state.value = state.value.copy(
                text = someRepository.getGreeting()
            )
        }
    }
}