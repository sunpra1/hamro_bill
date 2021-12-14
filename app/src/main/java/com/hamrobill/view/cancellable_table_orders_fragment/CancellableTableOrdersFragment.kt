package com.hamrobill.view.cancellable_table_orders_fragment

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
import com.hamrobill.data.pojo.CancellableOrderItem
import com.hamrobill.databinding.FragmentCancellableTableOrdersBinding
import com.hamrobill.utility.*
import com.hamrobill.view.cancel_order_dialog_fragment.CancelOrderDialogFragment
import com.hamrobill.view_model.SharedViewModel
import javax.inject.Inject

class CancellableTableOrdersFragment : BottomSheetDialogFragment(), View.OnClickListener,
    CancellableTableOrderListRecyclerViewAdapter.OnTableOrderListItemCheckedListener,
    CancelOrderDialogFragment.OnOrderDeleteListener {
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mViewModel: SharedViewModel
    private lateinit var mBinding: FragmentCancellableTableOrdersBinding
    private var mBottomSheet: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCancellableTableOrdersBinding.inflate(inflater)
        mViewModel =
            ViewModelProvider(requireActivity(), mViewModelFactory).get(SharedViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.cancellableTableOrders.observe(requireActivity()) {
            if (isAdded && !it.isNullOrEmpty()) {
                mBinding.activeTableOrderRV.swapAdapter(
                    CancellableTableOrderListRecyclerViewAdapter(it, this),
                    true
                )
            }
        }
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

    override fun onTableOrderListItemChecked(
        cancellableOrderItem: CancellableOrderItem,
        position: Int
    ) {
        mViewModel.setCancelOrderItem(cancellableOrderItem)
        CancelOrderDialogFragment.getInstance(cancellableOrderItem, position, this)
            .showNow(childFragmentManager, CancelOrderDialogFragment::class.java.simpleName)
    }

    override fun onOrderCancelled(
        cancellableOrderItem: CancellableOrderItem,
        position: Int,
        remarks: String?
    ) {
        mViewModel.cancelTableOrder(remarks)
    }

    override fun onCancellationCancelled(
        cancellableOrderItem: CancellableOrderItem,
        position: Int
    ) {
        mViewModel.setCancelOrderItem(null)
    }
}