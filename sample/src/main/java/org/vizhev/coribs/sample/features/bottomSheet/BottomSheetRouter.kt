package org.vizhev.coribs.sample.features.bottomSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import org.vizhev.coribs.baserib.BaseRouter
import org.vizhev.coribs.baserib.BaseViewRouter
import org.vizhev.coribs.sample.Dependencies
import org.vizhev.coribs.sample.data.SomeRepository
import org.vizhev.sample.databinding.ViewStubBinding

class BottomSheetRouter(
    parentRouter: BaseRouter<*, Dependencies>
) : BaseViewRouter<BottomSheeetView, BottomSheetViewModel, BottomSheetInteractor, Dependencies>(
    parentRouter = parentRouter
) {

    override fun createInteractor(): BottomSheetInteractor {
        return BottomSheetInteractor(SomeRepository())
    }

    override fun getView(parentViewGroup: ViewGroup): BottomSheeetView {
        val viewBinding = ViewStubBinding.inflate(
            LayoutInflater.from(parentViewGroup.context),
            parentViewGroup,
            false
        )
        return BottomSheeetView(viewBinding, getViewModel(), parentViewGroup.context)
    }

    override fun handleOnBackPressed(): Boolean {
        view?.dismiss() ?: exit()
        return true
    }

    fun exit() {
        navigation.removeViewComponent(this)
    }
}