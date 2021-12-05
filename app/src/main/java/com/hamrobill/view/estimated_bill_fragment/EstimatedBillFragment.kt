package com.hamrobill.view.estimated_bill_fragment

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
import com.hamrobill.data.pojo.ActiveOrderItem
import com.hamrobill.databinding.FragmentEstimatedBillBinding
import com.hamrobill.utils.*
import com.hamrobill.view_model.SharedViewModel
import java.util.*
import javax.inject.Inject

class EstimatedBillFragment : BottomSheetDialogFragment(), View.OnClickListener {
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mSharedPreferenceStorage: SharedPreferenceStorage
    private lateinit var mViewModel: SharedViewModel
    private lateinit var mBinding: FragmentEstimatedBillBinding
    private var mBottomSheet: BottomSheetDialog? = null
    private var mTimer: Timer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEstimatedBillBinding.inflate(inflater)
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
        mViewModel.activeTableOrders.observe(requireActivity()) {
            if (!it.isNullOrEmpty()) {
                if (isAdded) {
                    mBinding.estimatedBillItemsRV.swapAdapter(
                        EstimatedBillItemRecyclerViewAdapter(it),
                        true
                    )
                    calculateBillTotal(it)
                }
            }
        }
    }

    private fun calculateBillTotal(orderItems: ArrayList<ActiveOrderItem>) {
        var total = 0.0f
        orderItems.forEach { total += it.quantity * it.subItemPrice }
        mBinding.totalAmount.text = getString(R.string.amount_format, total.toString())
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

        mBinding.title.text = getString(
            R.string.estimated_bill_title_format, mViewModel.selectedTable.value!!.tableName,
            mViewModel.activeTableOrders.value!!.last().billNumber.toString()
        )
        mBinding.billNumber.text = getString(
            R.string.bill_number_format,
            mViewModel.activeTableOrders.value!!.last().billNumber.toString()
        )
        mBinding.cashierName.text =
            getString(R.string.cashier_format, mSharedPreferenceStorage.loggedUserName)
        mBinding.tableNumber.text =
            getString(R.string.table_number_format, mViewModel.selectedTable.value!!.tableName)
        val delay = (60 - Calendar.getInstance().get(Calendar.SECOND)).toLong()
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                val calendar = Calendar.getInstance()
                mBinding.date.text =
                    getString(R.string.date_format, calendar.getFormattedStringDate())
                mBinding.time.text = getString(
                    R.string.time_format,
                    calendar.get(Calendar.HOUR).toString(),
                    calendar.get(Calendar.MINUTE).toString()
                )
            }
        }, delay, (60 * 100).toLong())
        mBinding.estimatedBillItemsRV.layoutManager = LinearLayoutManager(requireContext())
        mBinding.btnClose.setOnClickListener(this)
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

    override fun onStop() {
        super.onStop()
        mTimer.cancel()
    }
}