package com.hamrobill.di.subcomponent

import com.hamrobill.di.scope.FragmentScope
import com.hamrobill.view.change_table_dialog_fragment.ChangeTableDialogFragment
import com.hamrobill.view.food_items_fragment.FoodItemsFragment
import com.hamrobill.view.food_sub_items_fragment.FoodSubItemsFragment
import com.hamrobill.view.table_orders_fragment.TableOrdersFragment
import com.hamrobill.view.tables_fragment.TablesFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface FragmentComponent {
    fun inject(tablesFragment: TablesFragment)
    fun inject(foodItemsFragment: FoodItemsFragment)
    fun inject(foodSubItemFragment: FoodSubItemsFragment)
    fun inject(foodSubItemFragment: TableOrdersFragment)
    fun inject(changeTableDialogFragment: ChangeTableDialogFragment)
}