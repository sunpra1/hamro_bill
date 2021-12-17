package com.hamrobill.view.change_table_dialog_fragment

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
import com.hamrobill.databinding.FragmentChangeTableDialogBinding
import com.hamrobill.utility.DIALOG_WIDTH_LIMIT
import com.hamrobill.utility.DIALOG_WIDTH_RATIO_BIG
import com.hamrobill.utility.DIALOG_WIDTH_RATIO_SMALL
import com.hamrobill.utility.windowWidth

class ChangeTableDialogFragment private constructor() : AppCompatDialogFragment(),
    AdapterView.OnItemSelectedListener, View.OnClickListener {

    private lateinit var mBinding: FragmentChangeTableDialogBinding
    private var mSelectedTable: Table? = null
    private var mBookedTables: ArrayList<Table> = ArrayList()
    private var mNotBookedTables: ArrayList<Table> = ArrayList()
    private lateinit var mTableChangeListener: TableChangeListener

    companion object {
        @JvmStatic
        fun getInstance(
            tableChangeListener: TableChangeListener,
            tables: ArrayList<Table>,
            selectedTable: Table?
        ): ChangeTableDialogFragment {
            return ChangeTableDialogFragment().apply {
                tables.forEach {
                    if (it.isBooked) mBookedTables.add(it)
                    else mNotBookedTables.add(it)
                }
                mTableChangeListener = tableChangeListener
                mSelectedTable = selectedTable
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChangeTableDialogBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setStyle(STYLE_NO_TITLE, 0)
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
        val notBookedTables = arrayListOf("SELECT TABLE")
            .also {
                mNotBookedTables.forEach { table ->
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
        val notBookedTablesAdapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            notBookedTables
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
            onItemSelectedListener = this@ChangeTableDialogFragment
            this.adapter = bookedTablesAdapter
        }
        mBinding.destinationTableSpn.apply {
            onItemSelectedListener = this@ChangeTableDialogFragment
            this.adapter = notBookedTablesAdapter
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
                    mTableChangeListener.onTableChanged(
                        mBookedTables[mBinding.currentTableSpn.selectedItemPosition - 1],
                        mNotBookedTables[mBinding.destinationTableSpn.selectedItemPosition - 1]
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
            else -> {
                mBinding.destinationTableTil.isErrorEnabled = false
                true
            }
        }
    }

    interface TableChangeListener {
        fun onTableChanged(from: Table, to: Table)
    }
}