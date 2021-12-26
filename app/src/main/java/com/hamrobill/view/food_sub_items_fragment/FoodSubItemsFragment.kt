package com.hamrobill.view.food_sub_items_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hamrobill.R
import com.hamrobill.data.pojo.FoodSubItem
import com.hamrobill.databinding.FragmentFoodSubItemsBinding
import com.hamrobill.di.subcomponent.FragmentComponent
import com.hamrobill.model.SubItemType
import com.hamrobill.utility.*
import com.hamrobill.view_model.SharedViewModel
import javax.inject.Inject

class FoodSubItemsFragment private constructor() : DICompactBottomSheetDialogFragment(),
    View.OnClickListener,
    FoodSubItemListRecyclerViewAdapter.FoodSubItemOnClickListener {
    private lateinit var mSubItemType: SubItemType

    companion object {
        @JvmStatic
        fun getInstance(subItemType: SubItemType): FoodSubItemsFragment {
            return FoodSubItemsFragment().apply { mSubItemType = subItemType }
        }
    }

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mViewModel: SharedViewModel
    private lateinit var mBinding: FragmentFoodSubItemsBinding
    private var mBottomSheet: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG", "onCreateView: view created")
        mBinding = FragmentFoodSubItemsBinding.inflate(inflater)
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
        mViewModel.tableOrders.observe(requireActivity()) {
            if (isAdded) {
                mBinding.btnSave.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
                (mBinding.tableOrderItemsRV.adapter as FoodSubItemListRecyclerViewAdapter).tableOrders =
                    it
            }
        }
    }

    private fun initializeViews() {
        /*
        mobile device height: 1017, width: 1794
        tablet device height: 1640, width: 2560
        onePlus device height: 958, width: 2332
        * */

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

        mBinding.foodItemName.text = getString(
            R.string.bn_food_sub_item_title_format,
            mViewModel.selectedTable.value!!.tableName,
            when (mSubItemType) {
                SubItemType.FOOD_ITEM_SEARCH_RESULT -> getString(R.string.search_result)
                SubItemType.FOOD_ITEM_SUB_TYPES -> mViewModel.selectedFoodItem.value!!.itemName.uppercase()
            }

        )
        mBinding.btnClose.setOnClickListener(this)
        mBinding.btnSave.setOnClickListener(this)
        mBinding.btnSave.visibility = View.GONE
        mBinding.tableOrderItemsRV.layoutManager = LinearLayoutManager(requireContext())
        mBinding.tableOrderItemsRV.adapter =
            FoodSubItemListRecyclerViewAdapter(
                when (mSubItemType) {
                    SubItemType.FOOD_ITEM_SEARCH_RESULT -> mViewModel.searchResult.value!!
                    SubItemType.FOOD_ITEM_SUB_TYPES -> mViewModel.foodSubItems.value!!
                },
                this
            )
    }

    override fun configureDependencyInjection(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                mBinding.btnClose.id -> {
                    mViewModel.setTableOrders(null)
                    mViewModel.setFoodSubItems(null)
                    dismiss()
                }
                mBinding.btnSave.id -> {
                    mViewModel.placeTableOrders()
                }
            }
        }
    }

    override fun onFoodSubItemClicked(foodSubItem: FoodSubItem, position: Int) {
        mViewModel.addNewOrder(foodSubItem)
    }

    override fun onFoodSubItemEdited(
        foodSubItem: FoodSubItem,
        quantity: Float,
        priority: Int?,
        remarks: String?,
        position: Int
    ) {
        mViewModel.editOrder(foodSubItem, quantity, priority, remarks)
    }
}