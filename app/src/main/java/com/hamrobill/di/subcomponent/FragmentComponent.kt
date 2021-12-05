package com.hamrobill.di.subcomponent

import com.hamrobill.di.scope.FragmentScope
import com.hamrobill.view.cancellable_table_orders_fragment.CancellableTableOrdersFragment
import com.hamrobill.view.change_table_dialog_fragment.ChangeTableDialogFragment
import com.hamrobill.view.estimated_bill_fragment.EstimatedBillFragment
import com.hamrobill.view.food_items_fragment.FoodItemsFragment
import com.hamrobill.view.food_sub_items_fragment.FoodSubItemsFragment
import com.hamrobill.view.tables_fragment.TablesFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface FragmentComponent {
    fun inject(tablesFragment: TablesFragment)
    fun inject(foodItemsFragment: FoodItemsFragment)
    fun inject(foodSubItemFragment: FoodSubItemsFragment)
    fun inject(foodSubItemFragmentCancellable: CancellableTableOrdersFragment)
    fun inject(changeTableDialogFragment: ChangeTableDialogFragment)
    fun inject(estimatedBillFragment: EstimatedBillFragment)
}