package com.hamrobill.view.update_vat_service_charge_dialog_fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.hamrobill.databinding.FragmentUpdateServiceChargeDialogBinding
import com.hamrobill.utility.SharedPreferenceStorage

class UpdateVatServiceChargeDialogFragment : AppCompatDialogFragment(),
    View.OnClickListener,
    View.OnFocusChangeListener {
    private lateinit var mSharedPreferenceStorage: SharedPreferenceStorage
    private lateinit var mBinding: FragmentUpdateServiceChargeDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUpdateServiceChargeDialogBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setStyle(STYLE_NO_TITLE, 0)
        }
        return dialog
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                mBinding.okButton.id -> {
                    if (validate()) {
                        mSharedPreferenceStorage.serviceChargePercentage =
                            mBinding.serviceChargeEt.text.toString().toFloat()
                        mSharedPreferenceStorage.vatPercentage =
                            mBinding.vatEt.text.toString().toFloat()
                        Toast.makeText(
                            context,
                            "Charges updated successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismiss()
                    }
                }
                mBinding.cancelButton.id -> dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun validateServiceCharge() = when {
        mBinding.serviceChargeEt.text.isNullOrEmpty() -> {
            mBinding.serviceChargeTil.apply {
                isErrorEnabled = true
                error = "Service charge percentage is required."
            }
            false
        }
        else -> true
    }

    private fun validateTax() = when {
        mBinding.vatEt.text.isNullOrEmpty() -> {
            mBinding.vatTil.apply {
                isErrorEnabled = true
                error = "VAT percentage is required."
            }
            false
        }
        else -> true
    }

    private fun validate(): Boolean {
        var isValid = true
        if (!validateServiceCharge()) isValid = false
        if (!validateTax()) isValid = false
        return isValid
    }

    private fun initializeViews() {
        mBinding.apply {
            okButton.setOnClickListener(this@UpdateVatServiceChargeDialogFragment)
            cancelButton.setOnClickListener(this@UpdateVatServiceChargeDialogFragment)
            serviceChargeEt.onFocusChangeListener = this@UpdateVatServiceChargeDialogFragment
            serviceChargeEt.setText(mSharedPreferenceStorage.serviceChargePercentage.toString())
            vatEt.setText(mSharedPreferenceStorage.vatPercentage.toString())
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                mBinding.serviceChargeEt.id -> {
                    if (hasFocus) {
                        if (mBinding.serviceChargeTil.isErrorEnabled)
                            mBinding.serviceChargeTil.isErrorEnabled = false
                    } else {
                        validateServiceCharge()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(
            sharedPreferenceStorage: SharedPreferenceStorage
        ): UpdateVatServiceChargeDialogFragment {
            return UpdateVatServiceChargeDialogFragment().apply {
                mSharedPreferenceStorage = sharedPreferenceStorage
            }
        }
    }
}