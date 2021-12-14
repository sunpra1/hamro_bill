package com.hamrobill.view.cancel_order_dialog_fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDialogFragment
import com.hamrobill.R
import com.hamrobill.data.pojo.CancellableOrderItem
import com.hamrobill.databinding.FragmentCancelOrderDialogBinding
import com.hamrobill.utility.DIALOG_WIDTH_LIMIT
import com.hamrobill.utility.DIALOG_WIDTH_RATIO_BIG
import com.hamrobill.utility.DIALOG_WIDTH_RATIO_SMALL
import com.hamrobill.utility.windowWidth

class CancelOrderDialogFragment private constructor() : AppCompatDialogFragment(),
    View.OnClickListener {

    private lateinit var mBinding: FragmentCancelOrderDialogBinding
    private lateinit var mCancellableOrderItem: CancellableOrderItem
    private var mPosition: Int = -1
    private lateinit var mOnOrderDeleteListener: OnOrderDeleteListener

    companion object {
        @JvmStatic
        fun getInstance(
            cancellableOrderItem: CancellableOrderItem,
            position: Int,
            onOrderDeleteListener: OnOrderDeleteListener
        ): CancelOrderDialogFragment {
            return CancelOrderDialogFragment().apply {
                mCancellableOrderItem = cancellableOrderItem
                mPosition = position
                mOnOrderDeleteListener = onOrderDeleteListener
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCancelOrderDialogBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setTitle(getString(R.string.cancel_order_format, mCancellableOrderItem.subItemName))
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
        mBinding.cancelBtn.setOnClickListener(this)
        mBinding.backBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                mBinding.cancelBtn.id -> {
                    mOnOrderDeleteListener.onOrderCancelled(
                        mCancellableOrderItem,
                        mPosition,
                        mBinding.remarksEt.text.toString()
                    )
                    dismiss()
                }
                mBinding.backBtn.id -> {
                    mOnOrderDeleteListener.onCancellationCancelled(mCancellableOrderItem, mPosition)
                    dismiss()
                }
            }
        }
    }

    interface OnOrderDeleteListener {
        fun onOrderCancelled(
            cancellableOrderItem: CancellableOrderItem,
            position: Int,
            remarks: String?
        )

        fun onCancellationCancelled(cancellableOrderItem: CancellableOrderItem, position: Int)
    }
}