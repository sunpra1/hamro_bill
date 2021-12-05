package com.hamrobill.view.base_url_dialog_fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.hamrobill.databinding.FragmentBaseUrlDialogBinding
import com.hamrobill.utils.SharedPreferenceStorage

class BaseUrlDialogFragment private constructor() : AppCompatDialogFragment(), View.OnClickListener,
    View.OnFocusChangeListener {
    private lateinit var mSharedPreferenceStorage: SharedPreferenceStorage
    private lateinit var mBinding: FragmentBaseUrlDialogBinding
    private lateinit var mUrlUpdateListener: UrlUpdateListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBaseUrlDialogBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                mBinding.okButton.id -> {
                    if (validate()) {
                        mSharedPreferenceStorage.localBaseUrl =
                            mBinding.localEndPointEt.text.toString()
                        mSharedPreferenceStorage.remoteBaseUrl =
                            mBinding.remoteEndPointEt.text.toString()
                        dismiss()
                        mUrlUpdateListener.onUrlUpdated()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun validateRemoteBaseUrl() = if (mBinding.remoteEndPointEt.text.isNullOrEmpty()) {
        mBinding.remoteEndPointTil.apply {
            isErrorEnabled = true
            error = "Remote base url is required."
        }
        false
    } else if (!Patterns.WEB_URL.matcher(mBinding.remoteEndPointEt.text.toString()).matches()) {
        mBinding.remoteEndPointTil.apply {
            isErrorEnabled = true
            error = "Please provide valid url."
        }
        false
    } else true

    private fun validateLocalBaseUrl() = if (mBinding.localEndPointEt.text.isNullOrEmpty()) {
        mBinding.localEndPointTil.apply {
            isErrorEnabled = true
            error = "Local print base url is required."
        }
        false
    } else true

    private fun validate(): Boolean {
        var isValid = true
        if (!validateLocalBaseUrl()) isValid = false
        if (!validateRemoteBaseUrl()) isValid = false
        return isValid
    }

    private fun initializeViews() {
        mBinding.okButton.setOnClickListener(this)
        mBinding.localEndPointEt.onFocusChangeListener = this
        mBinding.remoteEndPointEt.onFocusChangeListener = this
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                mBinding.localEndPointEt.id -> {
                    if (hasFocus) {
                        if (mBinding.localEndPointTil.isErrorEnabled)
                            mBinding.localEndPointTil.isErrorEnabled = false
                    } else {
                        validateLocalBaseUrl()
                    }
                }

                mBinding.remoteEndPointEt.id -> {
                    if (hasFocus) {
                        if (mBinding.remoteEndPointTil.isErrorEnabled)
                            mBinding.remoteEndPointTil.isErrorEnabled = false

                    } else {
                        validateRemoteBaseUrl()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(
            sharedPreferenceStorage: SharedPreferenceStorage,
            urlUpdateListener: UrlUpdateListener
        ): BaseUrlDialogFragment {
            return BaseUrlDialogFragment().apply {
                mSharedPreferenceStorage = sharedPreferenceStorage
                mUrlUpdateListener = urlUpdateListener
            }
        }
    }

    interface UrlUpdateListener {
        fun onUrlUpdated()
    }
}