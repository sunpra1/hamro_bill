package com.hamrobill.view.cancellable_table_orders_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamrobill.R
import com.hamrobill.data.pojo.CancellableOrderItem
import com.hamrobill.databinding.TableOrderListItemBinding

class CancellableTableOrderListRecyclerViewAdapter(
    private val activeTableOrders: ArrayList<CancellableOrderItem>,
    private val onTableOrderListItemCheckedListener: OnTableOrderListItemCheckedListener
) :
    RecyclerView.Adapter<CancellableTableOrderListRecyclerViewAdapter.ViewHolder>() {

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
        fun updateView(cancellableOrderItem: CancellableOrderItem) {
            view.srNO.text = (adapterPosition + 1).toString()
            view.foodName.text = cancellableOrderItem.subItemName
            view.qty.text = cancellableOrderItem.quantity.toString()
            view.price.text = cancellableOrderItem.subItemPrice.toString()
            view.totalPrice.text =
                context.getString(R.string.price_format)
                    .format("Rs.", cancellableOrderItem.totalPrice)
            view.status.setImageResource(if (cancellableOrderItem.isOrder) R.drawable.check_circle_red_18 else R.drawable.check_circle_green_18)
            view.deleteCheckBox.visibility =
                if (!cancellableOrderItem.isOrder) View.VISIBLE else View.INVISIBLE
            view.deleteCheckBox.isChecked = selection == adapterPosition
            view.deleteCheckBox.setOnClickListener {
                selection = adapterPosition
                onTableOrderListItemCheckedListener.onTableOrderListItemChecked(
                    cancellableOrderItem,
                    adapterPosition,
                    view.deleteCheckBox.isChecked
                )
            }
        }
    }

    interface OnTableOrderListItemCheckedListener {
        fun onTableOrderListItemChecked(
            cancellableOrderItem: CancellableOrderItem,
            position: Int,
            isChecked: Boolean
        )
    }
}