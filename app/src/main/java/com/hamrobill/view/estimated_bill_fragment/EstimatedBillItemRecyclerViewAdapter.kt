package com.hamrobill.view.estimated_bill_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamrobill.R
import com.hamrobill.data.pojo.ActiveOrderItem
import com.hamrobill.databinding.EstimatedBillListItemBinding

class EstimatedBillItemRecyclerViewAdapter(
    private val activeTableOrders: ArrayList<ActiveOrderItem>
) :
    RecyclerView.Adapter<EstimatedBillItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            EstimatedBillListItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateView(activeTableOrders[position])
    }

    override fun getItemCount(): Int = activeTableOrders.size

    inner class ViewHolder(
        private val view: EstimatedBillListItemBinding,
        private val context: Context = view.root.context
    ) : RecyclerView.ViewHolder(view.root) {
        fun updateView(activeOrderItem: ActiveOrderItem) {
            view.apply {
                srNO.text = (adapterPosition + 1).toString()
                item.text = activeOrderItem.subItemName
                qty.text = activeOrderItem.quantity.toString()
                rate.text = context.getString(
                    R.string.amount_format,
                    activeOrderItem.subItemPrice.toString()
                )
                when {
                    activeOrderItem.isExtraColumn -> {
                        kot.text = null
                        bar.text = null
                        coffee.text = null
                        sekuwa.text = context.getString(
                            R.string.amount_format,
                            (activeOrderItem.quantity * activeOrderItem.subItemPrice).toString()
                        )
                    }
                    activeOrderItem.isCoffee -> {
                        kot.text = null
                        bar.text = null
                        coffee.text = context.getString(
                            R.string.amount_format,
                            (activeOrderItem.quantity * activeOrderItem.subItemPrice).toString()
                        )
                        sekuwa.text = null
                    }
                    activeOrderItem.isBar -> {
                        kot.text = null
                        bar.text = context.getString(
                            R.string.amount_format,
                            (activeOrderItem.quantity * activeOrderItem.subItemPrice).toString()
                        )
                        coffee.text = null
                        sekuwa.text = null
                    }
                    else -> {
                        kot.text = context.getString(
                            R.string.amount_format,
                            (activeOrderItem.quantity * activeOrderItem.subItemPrice).toString()
                        )
                        bar.text = null
                        coffee.text = null
                        sekuwa.text = null
                    }
                }
            }
        }
    }
}