package com.hamrobill.view.tables_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hamrobill.R
import com.hamrobill.data.pojo.Table
import com.hamrobill.databinding.FragmentTablesBinding
import com.hamrobill.di.subcomponent.FragmentComponent
import com.hamrobill.utility.DICompactFragment
import com.hamrobill.utility.RECYCLER_VIEW_WIDTH_LIMIT
import com.hamrobill.utility.windowWidth
import com.hamrobill.view.MainActivity
import com.hamrobill.view_model.SharedViewModel
import javax.inject.Inject

class TablesFragment : DICompactFragment(),
    TableListRecyclerViewAdapter.TableItemClickListener {
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mViewModel: SharedViewModel
    private lateinit var mBinding: FragmentTablesBinding
    private lateinit var mRecyclerViewAdapter: TableListRecyclerViewAdapter

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
        return mBinding.root
    }

    private fun setupObservers() {
        mViewModel.tables.observe(requireActivity()) {
            if (isAdded) {
                requireActivity().invalidateOptionsMenu()
                mRecyclerViewAdapter = TableListRecyclerViewAdapter(it, this)
                mBinding.tableRV.layoutManager =
                    GridLayoutManager(
                        context,
                        if (requireActivity().windowWidth() > RECYCLER_VIEW_WIDTH_LIMIT) 3 else 2
                    )
                mBinding.tableRV.swapAdapter(mRecyclerViewAdapter, true)
            }
        }
        mViewModel.tableItemChanged.observe(requireActivity()) {
            if (isAdded)
                mRecyclerViewAdapter.tableItemChanged(it)
        }
        mViewModel.selectedTable.observe(requireActivity()) {
            if (isAdded) {
                if (it != null) {
                    (requireActivity() as MainActivity).supportActionBar?.title =
                        getString(R.string.selected_table_format, it.tableName)
                    mViewModel.getTableActiveOrdersAndCancelableOrderItems()
                } else {
                    (requireActivity() as MainActivity).supportActionBar?.title =
                        getString(R.string.not_table_selected)
                    mViewModel.setActiveTableOrders(null)
                    mViewModel.setCancellableTableOrders(null)
                    (mBinding.tableRV.adapter as TableListRecyclerViewAdapter).selection = -1
                }
            }
        }
    }

    override fun configureDependencyInjection(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun onTableItemClick(table: Table, position: Int) {
        if (mViewModel.selectedTable.value == table) {
            mViewModel.setSelectedTable(null)
        } else {
            mViewModel.setSelectedTable(table)
        }
    }
}