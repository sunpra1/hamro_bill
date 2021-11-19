package com.hamrobill.view.table_orders_fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hamrobill.HamrobillApp
import com.hamrobill.R
import com.hamrobill.databinding.FragmentTableOrdersBinding
import com.hamrobill.utils.*
import com.hamrobill.view_model.SharedViewModel
import javax.inject.Inject

class TableOrdersFragment : BottomSheetDialogFragment(), View.OnClickListener {
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mViewModel: SharedViewModel
    private lateinit var mBinding: FragmentTableOrdersBinding
    private var mBottomSheet: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTableOrdersBinding.inflate(inflater)
        mViewModel =
            ViewModelProvider(requireActivity(), mViewModelFactory).get(SharedViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {
        val windowHeight = requireActivity().windowHeight()
        val windowWidth = requireActivity().windowWidth()
        mBottomSheet = dialog as? BottomSheetDialog
        mBottomSheet?.setCancelable(false)
        mBottomSheet?.apply {
            behavior.peekHeight = windowHeight
            behavior.maxWidth =
                if (windowWidth > DIALOG_WIDTH_LIMIT) (windowWidth * DIALOG_WIDTH_RATIO_BIG).toInt()
                else (windowWidth * DIALOG_WIDTH_RATIO_SMALL).toInt()
            mBinding.root.layoutParams.width =
                if (windowWidth > DIALOG_WIDTH_LIMIT) (windowWidth * DIALOG_WIDTH_RATIO_BIG).toInt()
                else (windowWidth * DIALOG_WIDTH_RATIO_SMALL).toInt()
        }

        mBinding.tableName.text = getString(
            R.string.table_order_title_format, mViewModel.selectedTable.value!!.tableName,
            mViewModel.activeTableOrders.value!!.last().billNumber.toString()
        )
        mBinding.btnClose.setOnClickListener(this)
        mBinding.activeTableOrderRV.layoutManager = LinearLayoutManager(requireContext())
        mBinding.activeTableOrderRV.adapter =
            TableOrderListRecyclerViewAdapter(mViewModel.activeTableOrders.value!!)
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as HamrobillApp).applicationComponent.getActivityComponentFactory()
            .create(requireActivity()).getFragmentSubComponent().inject(this)
        super.onAttach(context)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                mBinding.btnClose.id -> {
                    dismiss()
                }
            }
        }
    }
}