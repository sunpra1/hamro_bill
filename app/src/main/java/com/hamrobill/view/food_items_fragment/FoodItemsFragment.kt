package com.hamrobill.view.food_items_fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hamrobill.HamrobillApp
import com.hamrobill.R
import com.hamrobill.data.pojo.FoodItem
import com.hamrobill.databinding.FragmentFoodItemsBinding
import com.hamrobill.model.SubItemType
import com.hamrobill.utils.showToast
import com.hamrobill.view.food_sub_items_fragment.FoodSubItemsFragment
import com.hamrobill.view_model.SharedViewModel
import com.hamrobill.view_model.factory.ViewModelFactory
import javax.inject.Inject

class FoodItemsFragment : Fragment(),
    FoodRecyclerViewAdapter.FoodRecyclerViewItemClickListener {

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: SharedViewModel
    private lateinit var mBinding: FragmentFoodItemsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewModel =
            ViewModelProvider(requireActivity(), mViewModelFactory).get(SharedViewModel::class.java)
        mBinding = FragmentFoodItemsBinding.inflate(inflater, container, false)
        setupObservers()
        mViewModel.getFoodItems()
        return mBinding.root
    }

    private fun setupObservers() {
        mViewModel.foodItems.observe(requireActivity()) {
            mBinding.foodItemsRV.layoutManager = LinearLayoutManager(context)
            mBinding.foodItemsRV.adapter = FoodCategoryRecyclerViewAdapter(it, this)
        }
        mViewModel.foodSubItems.observe(requireActivity()) { foodSubItems ->
            if (!foodSubItems.isNullOrEmpty()) {
                FoodSubItemsFragment.getInstance(SubItemType.FOOD_ITEM_SUB_TYPES).also {
                    it.show(childFragmentManager, it.tag)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as HamrobillApp).applicationComponent.getActivityComponentFactory()
            .create(requireActivity()).getFragmentSubComponent().inject(this)
        super.onAttach(context)
    }

    override fun onFoodItemClick(foodItem: FoodItem, position: Int) {
        if (mViewModel.selectedTable.value != null) {
            mViewModel.setSelectedFoodItem(foodItem)
            mViewModel.getFoodSubItems()
        } else requireActivity().showToast(getString(R.string.no_table_selected))
    }

}