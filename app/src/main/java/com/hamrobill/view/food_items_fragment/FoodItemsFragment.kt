package com.hamrobill.view.food_items_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hamrobill.R
import com.hamrobill.data.pojo.FoodItem
import com.hamrobill.databinding.FragmentFoodItemsBinding
import com.hamrobill.di.subcomponent.FragmentComponent
import com.hamrobill.model.SubItemType
import com.hamrobill.utility.DICompactFragment
import com.hamrobill.utility.EventObserver
import com.hamrobill.utility.showToast
import com.hamrobill.view.food_sub_items_fragment.FoodSubItemsFragment
import com.hamrobill.view_model.SharedViewModel
import javax.inject.Inject

class FoodItemsFragment : DICompactFragment(),
    FoodRecyclerViewAdapter.FoodRecyclerViewItemClickListener {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mViewModel: SharedViewModel
    private lateinit var mBinding: FragmentFoodItemsBinding
    private var mFoodSubItemsFragment: FoodSubItemsFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewModel =
            ViewModelProvider(requireActivity(), mViewModelFactory).get(SharedViewModel::class.java)
        mBinding = FragmentFoodItemsBinding.inflate(inflater, container, false)
        setupObservers()
        return mBinding.root
    }

    private fun setupObservers() {
        mViewModel.foodItems.observe(requireActivity()) {
            if (isAdded) {
                mBinding.foodItemsRV.layoutManager = LinearLayoutManager(context)
                mBinding.foodItemsRV.adapter = FoodCategoryRecyclerViewAdapter(it, this)
            }
        }
        mViewModel.foodSubItems.observe(requireActivity()) { foodSubItems ->
            if (isAdded && !foodSubItems.isNullOrEmpty()) {
                mFoodSubItemsFragment =
                    FoodSubItemsFragment.getInstance(SubItemType.FOOD_ITEM_SUB_TYPES).also {
                        it.showNow(
                            childFragmentManager,
                            FoodSubItemsFragment::class.java.simpleName
                        )
                    }
            }
        }
        mViewModel.isOrderPlaced.observe(requireActivity(), EventObserver {
            if (isAdded && mFoodSubItemsFragment != null) mFoodSubItemsFragment!!.dismiss()
        })
    }

    override fun configureDependencyInjection(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun onFoodItemClick(foodItem: FoodItem, position: Int) {
        if (mViewModel.selectedTable.value != null) {
            mViewModel.setSelectedFoodItem(foodItem)
            mViewModel.getFoodSubItems()
        } else requireActivity().showToast(getString(R.string.no_table_selected))
    }

}