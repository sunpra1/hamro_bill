package com.hamrobill.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import com.hamrobill.utils.EventObserver
import com.hamrobill.utils.hideProgressDialog
import com.hamrobill.utils.showProgressDialog
import com.hamrobill.view.cancellable_table_orders_fragment.CancellableTableOrdersFragment
import com.hamrobill.view.change_table_dialog_fragment.ChangeTableDialogFragment
import com.hamrobill.view.estimated_bill_fragment.EstimatedBillFragment
import com.hamrobill.view.food_items_fragment.FoodItemsFragment
import com.hamrobill.view.food_sub_items_fragment.FoodSubItemsFragment
import com.hamrobill.view.merge_table_dialog_fragment.MergeTableDialogFragment
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
    private var mAlertDialog: AlertDialog? = null
    private var mCancellableTableOrdersFragment: CancellableTableOrdersFragment? = null

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
        mViewModel.getTablesAndFoodItems()
    }

    private fun initializeViews() {
        mBinding.kotWrapper.setOnClickListener(this)
        mBinding.botWrapper.setOnClickListener(this)
        mBinding.coffeeWrapper.setOnClickListener(this)
        mBinding.sekuwaWrapper.setOnClickListener(this)
        mBinding.searchBtn.setOnQueryTextListener(this)
        mBinding.refreshBtn.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.main_menu,
            menu
        )
        menu?.setGroupVisible(
            R.id.estimatedBillMenuGroup,
            mViewModel.activeTableOrders.value != null && mViewModel.activeTableOrders.value!!.isNotEmpty()
        )

        menu?.setGroupVisible(
            R.id.cancellableOrdersMenuGroup,
            mViewModel.cancellableTableOrders.value != null && mViewModel.cancellableTableOrders.value!!.isNotEmpty()
        )

        menu?.setGroupVisible(
            R.id.changeMergeTableMenuGroup,
            mViewModel.hasActiveTableOrders()
        )

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.estimatedBillMenuItem -> {
                displayEstimatedBill()
                true
            }
            R.id.changeTableMenuItem -> {
                ChangeTableDialogFragment.getInstance(
                    this,
                    mViewModel.tables.value!!,
                    mViewModel.selectedTable.value
                ).showNow(supportFragmentManager, ChangeTableDialogFragment::class.java.simpleName)
                true
            }
            R.id.cancellableOrdersMenuItem -> {
                displayCancellableTableOrders()
                true
            }
            R.id.mergeTableMenuItem -> {
                MergeTableDialogFragment.getInstance(
                    this,
                    mViewModel.tables.value!!,
                    mViewModel.selectedTable.value
                ).showNow(supportFragmentManager, MergeTableDialogFragment::class.java.simpleName)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayEstimatedBill() {
        EstimatedBillFragment().showNow(
            supportFragmentManager,
            EstimatedBillFragment::class.java.simpleName
        )
    }

    private fun displayCancellableTableOrders() {
        mCancellableTableOrdersFragment = CancellableTableOrdersFragment().apply {
            showNow(
                supportFragmentManager,
                CancellableTableOrdersFragment::class.java.simpleName
            )
        }
    }

    private fun setupObservers() {
        mViewModel.isNetworkAvailable.observe(this, EventObserver {
            displayConnectivityMessage(it)
        })
        mViewModel.isLoading.observe(this, EventObserver {
            if (it) showProgressDialog() else hideProgressDialog()
        })
        mViewModel.errorMessage.observe(this, EventObserver {
            val message = when (it) {
                is String -> it
                is Int -> getString(it)
                else -> null
            }

            mAlertDialog?.let { dialog -> if (dialog.isShowing) dialog.dismiss() }
            mAlertDialog = AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle("INFORMATION")
                .setIcon(R.drawable.information_24)
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        })

        mViewModel.toast.observe(this, EventObserver {
            val message = when (it) {
                is String -> it
                is Int -> getString(it)
                else -> null
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })

        mViewModel.isInitialDataLoaded.observe(this, EventObserver {
            attachFragment()
        })
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
        mViewModel.cancellableTableOrders.observe(this) {
            invalidateOptionsMenu()
        }
        mViewModel.searchResult.observe(this) {
            if (!it.isNullOrEmpty()) {
                FoodSubItemsFragment.getInstance(SubItemType.FOOD_ITEM_SEARCH_RESULT)
                    .showNow(supportFragmentManager, FoodSubItemsFragment::class.java.simpleName)
            } else {
                Toast.makeText(this, "No Items Found", Toast.LENGTH_LONG).show()
            }
        }
        mViewModel.selectedTable.observe(this) {
            mBinding.searchBtn.visibility = if (it != null) View.VISIBLE else View.GONE
        }
        mViewModel.cancellableTableOrders.observe(this) {
            if (mCancellableTableOrdersFragment != null && it.isNullOrEmpty()) mCancellableTableOrdersFragment!!.dismiss()
        }
    }

    private fun attachFragment() {
        supportFragmentManager.beginTransaction()
            .replace(mBinding.fragmentContainerOne.id, TablesFragment())
            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerTwo, FoodItemsFragment())
            .commit()
    }

    private fun displayConnectivityMessage(isConnected: Boolean) {
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
                mBinding.refreshBtn.id -> {
                    mViewModel.setSelectedTable(null)
                    mViewModel.setActiveTableOrders(null)
                    mViewModel.setCancellableTableOrders(null)
                    mViewModel.setSelectedFoodItem(null)
                    mViewModel.setFoodSubItems(null)
                    mViewModel.getTablesAndFoodItems()
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