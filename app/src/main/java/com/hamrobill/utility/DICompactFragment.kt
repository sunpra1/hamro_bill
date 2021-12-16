package com.hamrobill.utility

import android.content.Context
import androidx.fragment.app.Fragment
import com.hamrobill.HamrobillApp
import com.hamrobill.di.subcomponent.FragmentComponent

abstract class DICompactFragment : Fragment() {
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