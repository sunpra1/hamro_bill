package com.hamrobill.view.tables_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamrobill.R
import com.hamrobill.data.pojo.Table
import com.hamrobill.databinding.TableListItemBinding
import com.hamrobill.model.TableItemChanged

class TableListRecyclerViewAdapter(
    private var tableData: ArrayList<Table>,
    private val tableItemClickListener: TableItemClickListener
) :
    RecyclerView.Adapter<TableListRecyclerViewAdapter.ViewHolder>() {

    var selection = -1
        set(value) {
            val previousSelection = field
            field = if (previousSelection == value) -1 else value
            if (previousSelection > -1 && previousSelection != field)
                notifyItemChanged(previousSelection)
            notifyItemChanged(field)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TableListItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateView(tableData[position], tableItemClickListener)
    }

    override fun getItemCount() = tableData.size

    fun tableItemChanged(tableItemChanged: TableItemChanged) {
        tableData[tableItemChanged.updatedTableItemPosition] = tableItemChanged.updatedTableItem
        notifyItemChanged(tableItemChanged.updatedTableItemPosition)
    }

    inner class ViewHolder(
        private val view: TableListItemBinding,
        private val context: Context = view.root.context
    ) : RecyclerView.ViewHolder(view.root) {
        fun updateView(
            tableData: Table,
            tableItemClickListener: TableItemClickListener
        ) {
            view.cardView.setOnClickListener {
                selection = adapterPosition
                tableItemClickListener.onTableItemClick(
                    tableData,
                    adapterPosition
                )
            }
            view.tableName.text = tableData.tableName
            view.tableCapacity.text = context.getString(
                R.string.format_capacity_or_occupied,
                tableData.maxSites.toString()
            )
            view.customerOccupancy.text = context.getString(
                R.string.format_capacity_or_occupied,
                tableData.customerCount.toString()
            )

            when {
                tableData.isBooked && (selection > -1 && selection == adapterPosition) -> {
                    view.cardView.setBackgroundColor(context.getColor(R.color.magenta))
                    view.borderView.setBackgroundResource(R.drawable.white_border)
                    view.tableName.setTextColor(context.getColor(R.color.white))
                    view.tableCapacity.setTextColor(context.getColor(R.color.white))
                    view.customerOccupancy.setTextColor(context.getColor(R.color.white))
                }
                tableData.isBooked -> {
                    view.cardView.setBackgroundColor(context.getColor(R.color.red))
                    view.borderView.setBackgroundResource(R.drawable.white_border)
                    view.tableName.setTextColor(context.getColor(R.color.white))
                    view.tableCapacity.setTextColor(context.getColor(R.color.white))
                    view.customerOccupancy.setTextColor(context.getColor(R.color.white))
                }
                selection > -1 && selection == adapterPosition -> {
                    view.cardView.setBackgroundColor(context.getColor(R.color.blue))
                    view.borderView.setBackgroundResource(R.drawable.white_border)
                    view.tableName.setTextColor(context.getColor(R.color.white))
                    view.tableCapacity.setTextColor(context.getColor(R.color.white))
                    view.customerOccupancy.setTextColor(context.getColor(R.color.white))
                }
                else -> {
                    view.cardView.setBackgroundColor(context.getColor(R.color.white))
                    view.borderView.setBackgroundResource(R.drawable.black_border)
                    view.tableName.setTextColor(context.getColor(R.color.black))
                    view.tableCapacity.setTextColor(context.getColor(R.color.black))
                    view.customerOccupancy.setTextColor(context.getColor(R.color.black))
                }
            }
        }
    }

    interface TableItemClickListener {
        fun onTableItemClick(table: Table, position: Int)
    }
}