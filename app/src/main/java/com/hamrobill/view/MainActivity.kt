package com.hamrobill.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.hamrobill.HamrobillApp
import com.hamrobill.R
import com.hamrobill.data.pojo.ActiveOrderItem
import com.hamrobill.data.pojo.Table
import com.hamrobill.databinding.ActivityMainBinding
import com.hamrobill.model.FoodCategory
import com.hamrobill.model.SubItemType
import com.hamrobill.utils.hideProgressDialog
import com.hamrobill.utils.showProgressDialog
import com.hamrobill.view.change_table_dialog_fragment.ChangeTableDialogFragment
import com.hamrobill.view.food_sub_items_fragment.FoodSubItemsFragment
import com.hamrobill.view.merge_table_dialog_fragment.MergeTableDialogFragment
import com.hamrobill.view.table_orders_fragment.TableOrdersFragment
import com.hamrobill.view.tables_fragment.TablesFragment
import com.hamrobill.view_model.SharedViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity(), View.OnClickListener, SearchView.OnQueryTextListener,
    ChangeTableDialogFragment.TableChangeListener, MergeTableDialogFragment.TableMergeListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var mBinding: ActivityMainBinding
    private var mPreviousConnectivityStatus = true
    private lateinit var mViewModel: SharedViewModel
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as HamrobillApp).applicationComponent.getActivityComponentFactory()
            .create(baseContext).inject(this)
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        supportActionBar?.title = getString(R.string.no_table_selected)

        mViewModel = ViewModelProvider(this, factory).get(SharedViewModel::class.java)
        initializeViews()
        setupObservers()
        attachFragment()
    }

    private fun initializeViews() {
        mBinding.kotWrapper.setOnClickListener(this)
        mBinding.botWrapper.setOnClickListener(this)
        mBinding.coffeeWrapper.setOnClickListener(this)
        mBinding.sekuwaWrapper.setOnClickListener(this)
        mBinding.searchBtn.setOnQueryTextListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.main_menu,
            menu
        )
        menu?.setGroupVisible(
            R.id.tableActiveOrdersMenuGroup,
            mViewModel.activeTableOrders.value != null && mViewModel.activeTableOrders.value!!.isNotEmpty()
        )

        menu?.setGroupVisible(
            R.id.changeMergeTableMenuGroup,
            mViewModel.hasActiveTableOrders()
        )

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.tableOrdersMenuItem -> {
                displayTableOrders()
                true
            }
            R.id.changeTableMenuItem -> {
                ChangeTableDialogFragment.getInstance(
                    this,
                    mViewModel.tables.value!!,
                    mViewModel.selectedTable.value
                ).apply {
                    show(supportFragmentManager, tag)
                }
                true
            }

            R.id.mergeTableMenuItem -> {
                MergeTableDialogFragment.getInstance(
                    this,
                    mViewModel.tables.value!!,
                    mViewModel.selectedTable.value
                ).apply {
                    show(supportFragmentManager, tag)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayTableOrders() {
        TableOrdersFragment().also {
            it.show(supportFragmentManager, it.tag)
        }
    }

    private fun setupObservers() {
        mViewModel.isNetworkAvailable.observe(this) {
            displayConnectivityMessage(it)
        }
        mViewModel.isLoading.observe(this) {
            if (it) showProgressDialog() else hideProgressDialog()
        }
        mViewModel.errorMessage.observe(this) {
            val message = when (it) {
                is String -> it
                is Int -> getString(it)
                else -> getString(R.string.something_went_wrong)
            }

            alertDialog?.let { dialog -> if (dialog.isShowing) dialog.dismiss() }
            alertDialog = AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle("INFORMATION")
                .setIcon(R.drawable.information_24)
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        mViewModel.activeTableOrders.observe(this) {
            val kitchenActiveOrdersItem = ArrayList<ActiveOrderItem>()
            val barActiveOrdersItem = ArrayList<ActiveOrderItem>()
            val coffeeActiveOrdersItem = ArrayList<ActiveOrderItem>()
            val sekuwaActiveOrdersItem = ArrayList<ActiveOrderItem>()

            if (!it.isNullOrEmpty()) {
                it.forEach { orderItem ->
                    when {
                        orderItem.isBar && orderItem.isOrder -> barActiveOrdersItem.add(orderItem)
                        orderItem.isCoffee && orderItem.isOrder -> coffeeActiveOrdersItem.add(
                            orderItem
                        )
                        orderItem.isExtraColumn && orderItem.isOrder -> sekuwaActiveOrdersItem.add(
                            orderItem
                        )
                        !orderItem.isCoffee && !orderItem.isExtraColumn && !orderItem.isBar && orderItem.isOrder -> kitchenActiveOrdersItem.add(
                            orderItem
                        )
                    }
                }
            }

            mBinding.kotWrapper.isEnabled = kitchenActiveOrdersItem.size > 0
            mBinding.kotWrapper.isClickable = kitchenActiveOrdersItem.size > 0
            mBinding.botWrapper.isEnabled = barActiveOrdersItem.size > 0
            mBinding.botWrapper.isClickable = barActiveOrdersItem.size > 0
            mBinding.coffeeWrapper.isEnabled = coffeeActiveOrdersItem.size > 0
            mBinding.coffeeWrapper.isClickable = coffeeActiveOrdersItem.size > 0
            mBinding.sekuwaWrapper.isEnabled = sekuwaActiveOrdersItem.size > 0
            mBinding.sekuwaWrapper.isClickable = sekuwaActiveOrdersItem.size > 0

            mBinding.kotCount.text = kitchenActiveOrdersItem.size.toString()
            mBinding.botCount.text = barActiveOrdersItem.size.toString()
            mBinding.coffeeCount.text = coffeeActiveOrdersItem.size.toString()
            mBinding.sekuwaCount.text = sekuwaActiveOrdersItem.size.toString()

            invalidateOptionsMenu()
        }
        mViewModel.searchResult.observe(this) {
            if (!it.isNullOrEmpty()) {
                FoodSubItemsFragment.getInstance(SubItemType.FOOD_ITEM_SEARCH_RESULT)
                    .also { fragment ->
                        fragment.show(supportFragmentManager, fragment.tag)
                    }
            }
        }
        mViewModel.selectedTable.observe(this) {
            mBinding.searchBtn.visibility = if (it != null) View.VISIBLE else View.GONE
        }
    }

    private fun attachFragment() {
        supportFragmentManager.beginTransaction()
            .replace(mBinding.fragmentContainerOne.id, TablesFragment()).commit()
    }

    fun displayConnectivityMessage(isConnected: Boolean) {
        if (!mPreviousConnectivityStatus)
            Handler(Looper.getMainLooper()).postDelayed({
                mBinding.netWorkConnectivity.root.visibility =
                    if (isConnected) View.GONE else View.VISIBLE
            }, 1000)
        else
            mBinding.netWorkConnectivity.root.visibility =
                if (isConnected) View.GONE else View.VISIBLE

        mBinding.netWorkConnectivity.networkStatus.text =
            if (isConnected) getString(R.string.connected) else getString(R.string.no_internet)
        mBinding.netWorkConnectivity.networkStatus.setTextColor(getColor(if (isConnected) R.color.green else R.color.red))
        mPreviousConnectivityStatus = isConnected
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                mBinding.kotWrapper.id -> {
                    mViewModel.saveTableOrders(FoodCategory.KITCHEN_ITEM)
                }
                mBinding.botWrapper.id -> {
                    mViewModel.saveTableOrders(FoodCategory.BAR_ITEM)
                }
                mBinding.coffeeWrapper.id -> {
                    mViewModel.saveTableOrders(FoodCategory.COFFEE_ITEM)
                }
                mBinding.sekuwaWrapper.id -> {
                    mViewModel.saveTableOrders(FoodCategory.SEKUWA_ITEM)
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        mBinding.searchBtn.onActionViewCollapsed()
        if (query != null) mViewModel.searchSubItems(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean = true

    override fun onTableChanged(from: Table, to: Table) {
        mViewModel.changeTableNumber(from, to)
    }

    override fun onTableMerged(from: Table, to: Table) {
        mViewModel.mergeTable(from, to)
    }
}