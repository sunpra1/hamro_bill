package com.hamrobill.view.food_items_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hamrobill.data.pojo.FoodItem
import com.hamrobill.databinding.CategoryListItemBinding
import com.hamrobill.model.FoodCategory
import com.hamrobill.utils.RECYCLER_VIEW_WIDTH_LIMIT
import com.hamrobill.utils.windowWidth
import java.util.*
import kotlin.collections.ArrayList

class FoodCategoryRecyclerViewAdapter(
    foodItems: ArrayList<FoodItem>,
    private val foodRecyclerViewItemClickListener: FoodRecyclerViewAdapter.FoodRecyclerViewItemClickListener
) :
    RecyclerView.Adapter<FoodCategoryRecyclerViewAdapter.ViewHolder>() {

    private val coffeeFoodItems: ArrayList<FoodItem> = ArrayList()
    private val barFoodItems: ArrayList<FoodItem> = ArrayList()
    private val kitchenFoodItems: ArrayList<FoodItem> = ArrayList()
    private val sekuwaFoodItems: ArrayList<FoodItem> = ArrayList()
    private val categories: ArrayList<FoodCategory> = ArrayList()

    init {
        foodItems.forEach {
            when {
                it.isBar -> barFoodItems.add(it)
                it.extraColumn -> sekuwaFoodItems.add(it)
                it.isCoffee -> coffeeFoodItems.add(it)
                else -> kitchenFoodItems.add(it)
            }
        }
        if (coffeeFoodItems.isNotEmpty()) categories.add(FoodCategory.COFFEE_ITEM)
        if (sekuwaFoodItems.isNotEmpty()) categories.add(FoodCategory.SEKUWA_ITEM)
        if (barFoodItems.isNotEmpty()) categories.add(FoodCategory.BAR_ITEM)
        if (kitchenFoodItems.isNotEmpty()) categories.add(FoodCategory.KITCHEN_ITEM)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CategoryListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        val singleCategoryItem = when (category) {
            FoodCategory.BAR_ITEM -> barFoodItems
            FoodCategory.SEKUWA_ITEM -> sekuwaFoodItems
            FoodCategory.COFFEE_ITEM -> coffeeFoodItems
            FoodCategory.KITCHEN_ITEM -> kitchenFoodItems
        }
        holder.updateView(
            category.category.uppercase(Locale.ENGLISH),
            singleCategoryItem,
            foodRecyclerViewItemClickListener
        )
    }

    override fun getItemCount(): Int = categories.size

    class ViewHolder(
        private val view: CategoryListItemBinding,
        private val context: Context = view.root.context
    ) :
        RecyclerView.ViewHolder(view.root) {
        fun updateView(
            categoryName: String,
            foodItems: ArrayList<FoodItem>,
            foodRecyclerViewItemClickListener: FoodRecyclerViewAdapter.FoodRecyclerViewItemClickListener
        ) {
            view.categoryName.text = categoryName
            view.foodItemRV.layoutManager =
                GridLayoutManager(
                    context,
                    if ((context as FragmentActivity).windowWidth() > RECYCLER_VIEW_WIDTH_LIMIT) 5 else 4
                )
            view.foodItemRV.adapter =
                FoodRecyclerViewAdapter(foodItems, foodRecyclerViewItemClickListener)
        }
    }
}
