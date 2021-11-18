package com.hamrobill.view.food_sub_items_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamrobill.R
import com.hamrobill.data.pojo.FoodSubItem
import com.hamrobill.databinding.FoodSubItemListItemBinding

class FoodSubItemListRecyclerViewAdapter(
        private val foodSubItems: ArrayList<FoodSubItem>,
        private val foodSubItemOnClickListener: FoodSubItemOnClickListener
) :
        RecyclerView.Adapter<FoodSubItemListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                FoodSubItemListItemBinding.inflate(LayoutInflater.from(parent.context)),
                foodSubItemOnClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateView(foodSubItems[position])
    }

    override fun getItemCount(): Int = foodSubItems.size

    class ViewHolder(
            private val view: FoodSubItemListItemBinding,
            private val foodSbItemOnClickListener: FoodSubItemOnClickListener,
            private val context: Context = view.root.context
    ) : RecyclerView.ViewHolder(view.root) {
        fun updateView(foodSubItem: FoodSubItem) {
            view.foodSubItemName.text = foodSubItem.subItemName
            view.foodSubItemPrice.text = context.getString(R.string.price_format)
                    .format(context.getString(R.string.currency), foodSubItem.subItemPrice)
            view.foodSubItemName.setOnClickListener {
                foodSbItemOnClickListener.onFoodSubItemClicked(foodSubItem, adapterPosition)
            }
            val onKeyListener = View.OnKeyListener { _, _, _ ->
                val quantity =
                        if (view.quantityEt.text.isNullOrBlank()) 0f else view.quantityEt.text.toString()
                                .toFloat()
                val priority =
                        if (view.orderByEt.text.isNullOrBlank()) null else view.orderByEt.text.toString()
                                .toInt()
                val remarks =
                        if (view.remarksEt.text.isNullOrBlank()) null else view.remarksEt.text.toString()
                view.foodSubItemName.isChecked = quantity > 0
                foodSbItemOnClickListener.onFoodSubItemEdited(
                        foodSubItem,
                        quantity,
                        priority,
                        remarks,
                        adapterPosition
                )
                false
            }
            view.quantityEt.setOnKeyListener(onKeyListener)
            view.orderByEt.setOnKeyListener(onKeyListener)
            view.remarksEt.setOnKeyListener(onKeyListener)
        }
    }

    interface FoodSubItemOnClickListener {
        fun onFoodSubItemClicked(foodSubItem: FoodSubItem, position: Int)
        fun onFoodSubItemEdited(
                foodSubItem: FoodSubItem,
                quantity: Float,
                priority: Int?,
                remarks: String?,
                position: Int
        )
    }
}