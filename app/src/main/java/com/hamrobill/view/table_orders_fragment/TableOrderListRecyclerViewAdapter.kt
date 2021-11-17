package com.hamrobill.view.table_orders_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamrobill.R
import com.hamrobill.data.pojo.ActiveOrderItem
import com.hamrobill.databinding.TableOrderListItemBinding

class TableOrderListRecyclerViewAdapter(
    private val activeTableOrders: ArrayList<ActiveOrderItem>
) :
    RecyclerView.Adapter<TableOrderListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TableOrderListItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateView(activeTableOrders[position])
    }

    override fun getItemCount(): Int = activeTableOrders.size

    class ViewHolder(
        private val view: TableOrderListItemBinding,
        private val context: Context = view.root.context
    ) : RecyclerView.ViewHolder(view.root) {
        fun updateView(activeOrderItem: ActiveOrderItem) {
            view.foodName.text = activeOrderItem.subItemName
            view.calculation.text = context.getString(R.string.calculation_format)
                .format(activeOrderItem.quantity, "Plate", "Rs.", activeOrderItem.subItemPrice)
            view.totalPrice.text =
                context.getString(R.string.price_format).format("Rs.", activeOrderItem.totalPrice)
        }
    }
}