package com.hamrobill.view.food_items_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamrobill.data.pojo.FoodItem
import com.hamrobill.databinding.FoodListItemBinding

class FoodRecyclerViewAdapter(
    private val foodItems: ArrayList<FoodItem>,
    private val foodRecyclerViewItemClickListener: FoodRecyclerViewItemClickListener
) :
    RecyclerView.Adapter<FoodRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FoodListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateView(
            foodItems[position],
            foodRecyclerViewItemClickListener
        )
    }

    override fun getItemCount(): Int = foodItems.size

    class ViewHolder(private val view: FoodListItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun updateView(
            foodItem: FoodItem,
            foodRecyclerViewItemClickListener: FoodRecyclerViewItemClickListener
        ) {
            view.foodName.text = foodItem.itemName
            view.cardView.setOnClickListener {
                foodRecyclerViewItemClickListener.onFoodItemClick(foodItem, adapterPosition)
            }
        }
    }

    interface FoodRecyclerViewItemClickListener {
        fun onFoodItemClick(foodItem: FoodItem, position: Int)
    }
}
