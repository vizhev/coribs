package org.vizhev.coribs.sample.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import org.vizhev.coribs.baserib.BaseRouter
import org.vizhev.coribs.baserib.BaseViewRouter
import org.vizhev.coribs.sample.Dependencies
import org.vizhev.coribs.sample.data.SomeRepository
import org.vizhev.sample.databinding.ViewStubBinding

class HomeRouter(
    parentRouter: BaseRouter<*, Dependencies>
) : BaseViewRouter<HomeView, HomeViewModel, HomeInteractor, Dependencies>(
    parentRouter = parentRouter
) {

    override fun getView(parentViewGroup: ViewGroup): HomeView {
        val viewBinding = ViewStubBinding.inflate(
            LayoutInflater.from(parentViewGroup.context),
            parentViewGroup,
            false
        )
        return HomeView(viewBinding, getViewModel(), parentViewGroup.context)
    }

    override fun handleOnBackPressed(): Boolean {
        navigation.activity?.finish()
        return true
    }

    override fun createInteractor(): HomeInteractor {
        return HomeInteractor(SomeRepository())
    }
}