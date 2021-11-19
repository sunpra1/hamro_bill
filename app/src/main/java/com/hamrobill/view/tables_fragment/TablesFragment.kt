package com.hamrobill.view.tables_fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hamrobill.HamrobillApp
import com.hamrobill.R
import com.hamrobill.data.pojo.Table
import com.hamrobill.databinding.FragmentTablesBinding
import com.hamrobill.utils.RECYCLER_VIEW_WIDTH_LIMIT
import com.hamrobill.utils.windowWidth
import com.hamrobill.view.MainActivity
import com.hamrobill.view.food_items_fragment.FoodItemsFragment
import com.hamrobill.view_model.SharedViewModel
import javax.inject.Inject

class TablesFragment : Fragment(),
    TableListRecyclerViewAdapter.TableItemClickListener {
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mViewModel: SharedViewModel
    private lateinit var mBinding: FragmentTablesBinding
    private lateinit var mRecyclerViewAdapter: TableListRecyclerViewAdapter
    private var mIsFoodItemsFragmentAttached = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        mViewModel =
            ViewModelProvider(requireActivity(), mViewModelFactory).get(SharedViewModel::class.java)
        mBinding = FragmentTablesBinding.inflate(inflater, container, false)

        setupObservers()
        mViewModel.getTables()
        return mBinding.root
    }

    private fun setupObservers() {
        mViewModel.isNetworkAvailable.observe(requireActivity()) {
            (requireActivity() as MainActivity).displayConnectivityMessage(it)
        }
        mViewModel.tables.observe(requireActivity()) {
            requireActivity().invalidateOptionsMenu()
            mRecyclerViewAdapter = TableListRecyclerViewAdapter(it, this)
            mBinding.tableRV.layoutManager =
                GridLayoutManager(
                    context,
                    if (requireActivity().windowWidth() > RECYCLER_VIEW_WIDTH_LIMIT) 3 else 2
                )
            mBinding.tableRV.swapAdapter(mRecyclerViewAdapter, true)

            if (!mIsFoodItemsFragmentAttached) {
                mIsFoodItemsFragmentAttached = true
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerTwo, FoodItemsFragment())
                    .commit()
            }
        }
        mViewModel.tableItemChanged.observe(requireActivity()) {
            mRecyclerViewAdapter.tableItemChanged(it)
        }
        mViewModel.selectedTable.observe(requireActivity()) {
            if (it != null) {
                val position = mViewModel.tables.value!!.indexOf(it)
                mRecyclerViewAdapter.selection = position
                (requireActivity() as MainActivity).supportActionBar?.title =
                    getString(R.string.selected_table_format, it.tableName)
                mViewModel.getTableActiveOrders()
            } else {
                mRecyclerViewAdapter.selection = -1
                (requireActivity() as MainActivity).supportActionBar?.title =
                    getString(R.string.not_table_selected)
                mViewModel.setActiveTableOrders(null)
            }
        }
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as HamrobillApp).applicationComponent.getActivityComponentFactory()
            .create(requireActivity()).getFragmentSubComponent().inject(this)
        super.onAttach(context)
    }

    override fun onTableItemClick(table: Table, position: Int) {
        if (mViewModel.selectedTable.value != null && mViewModel.selectedTable.value!!.tableID == table.tableID) {
            mViewModel.setSelectedTable(null)
        } else {
            mViewModel.setSelectedTable(table)
        }
    }
}