package com.hamrobill.view.table_orders_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hamrobill.R
import com.hamrobill.data.pojo.ActiveOrderItem
import com.hamrobill.databinding.TableOrderListItemBinding

class TableOrderListRecyclerViewAdapter(
    private val activeTableOrders: ArrayList<ActiveOrderItem>,
    private val onTableOrderListItemCheckedListener: OnTableOrderListItemCheckedListener
) :
    RecyclerView.Adapter<TableOrderListRecyclerViewAdapter.ViewHolder>() {

    var selection = -1
        set(value) {
            val previousSelection = field
            field = if (previousSelection == value) -1 else value
            if (previousSelection > -1 && previousSelection != field)
                notifyItemChanged(previousSelection)
            notifyItemChanged(field)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TableOrderListItemBinding.inflate(LayoutInflater.from(parent.context)),
            onTableOrderListItemCheckedListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateView(activeTableOrders[position])
    }

    override fun getItemCount(): Int = activeTableOrders.size

    inner class ViewHolder(
        private val view: TableOrderListItemBinding,
        private val onTableOrderListItemCheckedListener: OnTableOrderListItemCheckedListener,
        private val context: Context = view.root.context
    ) : RecyclerView.ViewHolder(view.root) {
        fun updateView(activeOrderItem: ActiveOrderItem) {
            view.srNO.text = (adapterPosition + 1).toString()
            view.foodName.text = activeOrderItem.subItemName
            view.qty.text = activeOrderItem.quantity.toString()
            view.price.text = activeOrderItem.subItemPrice.toString()
            view.totalPrice.text =
                context.getString(R.string.price_format).format("Rs.", activeOrderItem.totalPrice)
            view.status.setImageResource(if (activeOrderItem.isOrder) R.drawable.check_circle_red_18 else R.drawable.check_circle_green_18)
            view.deleteCheckBox.visibility = if(!activeOrderItem.isOrder) View.VISIBLE else View.INVISIBLE
            view.deleteCheckBox.isChecked = selection == adapterPosition
            view.deleteCheckBox.setOnClickListener {
                selection = adapterPosition
                onTableOrderListItemCheckedListener.onTableOrderListItemChecked(
                    activeOrderItem,
                    adapterPosition,
                    view.deleteCheckBox.isChecked
                )
            }
        }
    }

    interface OnTableOrderListItemCheckedListener {
        fun onTableOrderListItemChecked(activeOrderItem: ActiveOrderItem, position: Int, isChecked: Boolean)
    }
}