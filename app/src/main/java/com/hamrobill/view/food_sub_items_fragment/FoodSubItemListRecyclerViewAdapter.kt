package com.hamrobill.view.food_sub_items_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.hamrobill.R
import com.hamrobill.data.pojo.FoodSubItem
import com.hamrobill.databinding.FoodSubItemListItemBinding
import com.hamrobill.model.OrderItem
import com.hamrobill.utility.getOrderQuantity

class FoodSubItemListRecyclerViewAdapter(
    private val foodSubItems: ArrayList<FoodSubItem>,
    private val foodSubItemOnClickListener: FoodSubItemOnClickListener
) :
    RecyclerView.Adapter<FoodSubItemListRecyclerViewAdapter.ViewHolder>() {

    var tableOrders: ArrayList<OrderItem>? = null

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

    inner class ViewHolder(
        private val view: FoodSubItemListItemBinding,
        private val foodSbItemOnClickListener: FoodSubItemOnClickListener,
        private val context: Context = view.root.context
    ) : RecyclerView.ViewHolder(view.root) {
        init {
            setIsRecyclable(false)
        }

        fun updateView(foodSubItem: FoodSubItem) {
            val orderItem =
                tableOrders?.firstOrNull { it.foodSubItem == foodSubItem }

            view.foodSubItemName.text = foodSubItem.subItemName
            view.foodSubItemPrice.text = context.getString(R.string.price_format)
                .format(context.getString(R.string.currency), foodSubItem.subItemPrice)
            view.foodSubItemName.isChecked = orderItem != null
            view.foodSubItemName.setOnClickListener {
                view.quantityEt.setText("1.0")
                foodSbItemOnClickListener.onFoodSubItemClicked(foodSubItem, adapterPosition)
            }
            view.quantityEt.apply {
                setText(orderItem?.quantity?.toString())
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) setText(text.toString().getOrderQuantity())
                }
                addTextChangedListener {
                    if (hasFocus()) keyListener(foodSubItem)
                }
            }
            view.orderByEt.apply {
                setText(orderItem?.priority?.toString())
                addTextChangedListener {
                    if (hasFocus()) keyListener(foodSubItem)
                }
            }
            view.remarksEt.apply {
                setText(orderItem?.remarks)
                addTextChangedListener {
                    if (hasFocus()) keyListener(foodSubItem)
                }
            }
        }

        private fun keyListener(foodSubItem: FoodSubItem) {
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