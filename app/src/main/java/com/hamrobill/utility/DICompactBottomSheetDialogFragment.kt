package com.hamrobill.utility

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hamrobill.HamrobillApp
import com.hamrobill.di.subcomponent.FragmentComponent

abstract class DICompactBottomSheetDialogFragment : BottomSheetDialogFragment() {
    lateinit var fragmentComponent: FragmentComponent

    override fun onAttach(context: Context) {
        fragmentComponent =
            (requireActivity().application as HamrobillApp).applicationComponent.getActivityComponentFactory()
                .create(requireActivity()).getFragmentSubComponent()
        configureDependencyInjection(fragmentComponent)
        super.onAttach(context)
    }

    abstract fun configureDependencyInjection(fragmentComponent: FragmentComponent)
}