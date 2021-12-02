package com.hamrobill.view.delete_order_dialog_fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDialogFragment
import com.hamrobill.R
import com.hamrobill.data.pojo.ActiveOrderItem
import com.hamrobill.databinding.FragmentDeleteOrderDialogBinding
import com.hamrobill.utils.DIALOG_WIDTH_LIMIT
import com.hamrobill.utils.DIALOG_WIDTH_RATIO_BIG
import com.hamrobill.utils.DIALOG_WIDTH_RATIO_SMALL
import com.hamrobill.utils.windowWidth

class DeleteOrderDialogFragment private constructor() : AppCompatDialogFragment(),
    View.OnClickListener {

    private lateinit var mBinding: FragmentDeleteOrderDialogBinding
    private lateinit var mActiveOrderItem: ActiveOrderItem
    private lateinit var mOnOrderDeleteListener: OnOrderDeleteListener

    companion object {
        @JvmStatic
        fun getInstance(
            activeOrderItem: ActiveOrderItem,
            onOrderDeleteListener: OnOrderDeleteListener
        ): DeleteOrderDialogFragment {
            return DeleteOrderDialogFragment().apply {
                mActiveOrderItem = activeOrderItem
                mOnOrderDeleteListener = onOrderDeleteListener
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDeleteOrderDialogBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setTitle(getString(R.string.cancel_order_format, mActiveOrderItem.subItemName))
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
        mBinding.cancelButton.setOnClickListener(this)
        mBinding.deleteBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                mBinding.deleteBtn.id -> {
                    mOnOrderDeleteListener.onOrderDeleted(mBinding.remarksEt.text.toString())
                    dismiss()
                }
                mBinding.cancelButton.id -> dismiss()
            }
        }
    }

    interface OnOrderDeleteListener {
        fun onOrderDeleted(remarks: String?)
    }
}