package com.hamrobill.view.merge_table_dialog_fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDialogFragment
import com.hamrobill.R
import com.hamrobill.data.pojo.Table
import com.hamrobill.databinding.FragmentMergeTableDialogBinding
import com.hamrobill.utils.DIALOG_WIDTH_LIMIT
import com.hamrobill.utils.DIALOG_WIDTH_RATIO_BIG
import com.hamrobill.utils.DIALOG_WIDTH_RATIO_SMALL
import com.hamrobill.utils.windowWidth

class MergeTableDialogFragment private constructor() : AppCompatDialogFragment(),
    AdapterView.OnItemSelectedListener, View.OnClickListener {

    private lateinit var mBinding: FragmentMergeTableDialogBinding
    private var mSelectedTable: Table? = null
    private lateinit var mBookedTables: ArrayList<Table>
    private lateinit var mTableMergeListener: TableMergeListener

    companion object {
        @JvmStatic
        fun getInstance(
            tableMergeListener: TableMergeListener,
            tables: ArrayList<Table>,
            selectedTable: Table?
        ): MergeTableDialogFragment {
            return MergeTableDialogFragment().apply {
                mBookedTables = tables.filter { it.isBooked } as ArrayList<Table>
                mTableMergeListener = tableMergeListener
                mSelectedTable = selectedTable
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMergeTableDialogBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setTitle(getString(R.string.merge_table))
        }
        return dialog
    }

    private fun initializeViews() {
        val windowWidth = requireActivity().windowWidth()
        dialog?.setCancelable(false)
        dialog?.window?.setLayout(
            if (windowWidth > DIALOG_WIDTH_LIMIT) (windowWidth * DIALOG_WIDTH_RATIO_BIG).toInt()
            else (windowWidth * DIALOG_WIDTH_RATIO_SMALL).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val bookedTables = arrayListOf("SELECT TABLE")
            .also {
                mBookedTables.forEach { table ->
                    it.add(table.tableName)
                }
            }
        val bookedTablesAdapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            bookedTables
        ) {
            override fun isEnabled(position: Int): Boolean {
                return when (position) {
                    0 -> false
                    else -> super.isEnabled(position)
                }
            }

            override fun areAllItemsEnabled(): Boolean = false
        }

        mBinding.currentTableSpn.apply {
            onItemSelectedListener = this@MergeTableDialogFragment
            this.adapter = bookedTablesAdapter
        }
        mBinding.destinationTableSpn.apply {
            onItemSelectedListener = this@MergeTableDialogFragment
            this.adapter = bookedTablesAdapter
        }
        mSelectedTable?.let { selectedTable ->
            mBinding.currentTableSpn.setSelection(
                mBookedTables.indexOf(
                    selectedTable
                ) + 1
            )
        }
        mBinding.cancelButton.setOnClickListener(this)
        mBinding.okButton.setOnClickListener(this)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            when (parent.id) {
                mBinding.currentTableSpn.id -> {
                    if (position > 0)
                        mBinding.currentTableTil.isErrorEnabled = false
                }
                mBinding.destinationTableSpn.id -> if (position > 0) mBinding.destinationTableTil.isErrorEnabled =
                    false
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                mBinding.okButton.id -> if (validate()) {
                    mTableMergeListener.onTableMerged(
                        mBookedTables[mBinding.currentTableSpn.selectedItemPosition - 1],
                        mBookedTables[mBinding.destinationTableSpn.selectedItemPosition - 1]
                    )
                    dismiss()
                }
                mBinding.cancelButton.id -> dismiss()
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        if (!validateCurrentTable()) isValid = false
        if (!validateDestinationTable()) isValid = false
        return isValid
    }

    private fun validateCurrentTable(): Boolean = mBinding.currentTableSpn.let {
        when (it.selectedItemPosition) {
            0 -> {
                mBinding.currentTableTil.apply {
                    isErrorEnabled = true
                    error = "Please select current table"
                }
                false
            }
            else -> {
                mBinding.currentTableTil.isErrorEnabled = false
                true
            }
        }
    }

    private fun validateDestinationTable(): Boolean = mBinding.destinationTableSpn.let {
        when (it.selectedItemPosition) {
            0 -> {
                mBinding.destinationTableTil.apply {
                    isErrorEnabled = true
                    error = "Please select destination table"
                }
                false
            }
            mBinding.currentTableSpn.selectedItemPosition -> {
                mBinding.destinationTableTil.apply {
                    isErrorEnabled = true
                    error = "Current and destination tables are same"
                }
                false
            }
            else -> {
                mBinding.destinationTableTil.isErrorEnabled = false
                true
            }
        }
    }

    interface TableMergeListener {
        fun onTableMerged(from: Table, to: Table)
    }
}