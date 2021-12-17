package com.hamrobill.view.update_service_charge_dialog_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.hamrobill.databinding.FragmentUpdateServiceChargeDialogBinding
import com.hamrobill.utility.SharedPreferenceStorage

class UpdateServiceChargeDialogFragment private constructor() : AppCompatDialogFragment(),
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

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                mBinding.okButton.id -> {
                    if (validateServiceCharge()) {
                        Log.d("SERVICE CHARGE", "${mBinding.serviceChargeEt.text}")
                        mSharedPreferenceStorage.serviceChargePercentage =
                            mBinding.serviceChargeEt.text.toString().toFloat()
                        Toast.makeText(
                            context,
                            "Service charge updated successfully.",
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

    private fun initializeViews() {
        mBinding.apply {
            okButton.setOnClickListener(this@UpdateServiceChargeDialogFragment)
            cancelButton.setOnClickListener(this@UpdateServiceChargeDialogFragment)
            serviceChargeEt.onFocusChangeListener = this@UpdateServiceChargeDialogFragment
            if (mSharedPreferenceStorage.serviceChargePercentage > 0)
                serviceChargeEt.setText(mSharedPreferenceStorage.serviceChargePercentage.toString())
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
        ): UpdateServiceChargeDialogFragment {
            return UpdateServiceChargeDialogFragment().apply {
                mSharedPreferenceStorage = sharedPreferenceStorage
            }
        }
    }
}