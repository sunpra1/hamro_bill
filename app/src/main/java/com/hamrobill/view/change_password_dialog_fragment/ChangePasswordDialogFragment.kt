package com.hamrobill.view.change_password_dialog_fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.hamrobill.databinding.FragmentChangePasswordDialogBinding

class ChangePasswordDialogFragment private constructor() : AppCompatDialogFragment(),
    View.OnClickListener,
    View.OnFocusChangeListener {
    private lateinit var mBinding: FragmentChangePasswordDialogBinding
    private lateinit var mPasswordUpdateListener: PasswordUpdateListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChangePasswordDialogBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                mBinding.changeButton.id -> {
                    if (validate()) {
                        mPasswordUpdateListener.onPasswordUpdated(
                            mBinding.oldPasswordEt.text.toString(),
                            mBinding.passwordEt.text.toString()
                        )
                    }
                }
                mBinding.cancelButton.id -> {
                    dismiss()
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setStyle(STYLE_NO_TITLE, 0)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun validateOldPassword() = when {
        mBinding.oldPasswordEt.text.isNullOrEmpty() -> {
            mBinding.oldPasswordTil.apply {
                isErrorEnabled = true
                error = "Old password is required."
            }
            false
        }
        mBinding.oldPasswordEt.text.toString().length < 6 -> {
            mBinding.oldPasswordTil.apply {
                isErrorEnabled = true
                error = "Password must be 6 characters long."
            }
            false
        }
        else -> true
    }

    private fun validatePassword() = when {
        mBinding.passwordEt.text.isNullOrEmpty() -> {
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = "New password is required."
            }
            false
        }
        mBinding.passwordEt.text.toString().length < 6 -> {
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = "Password must be 6 characters long."
            }
            false
        }
        else -> true
    }

    private fun validateConfirmPassword() = when {
        mBinding.confirmPasswordEt.text.isNullOrEmpty() -> {
            mBinding.confirmPasswordTil.apply {
                isErrorEnabled = true
                error = "Confirm new password is required."
            }
            false
        }
        mBinding.confirmPasswordEt.text.toString().length < 6 -> {
            mBinding.confirmPasswordTil.apply {
                isErrorEnabled = true
                error = "Password must be 6 characters long."
            }
            false
        }
        validatePassword() && mBinding.passwordEt.text.toString() != mBinding.confirmPasswordEt.text.toString() -> {
            mBinding.confirmPasswordTil.apply {
                isErrorEnabled = true
                error = "New password and confirm password is doesn't match."
            }
            false
        }
        else -> true
    }

    private fun validate(): Boolean {
        var isValid = true
        if (!validateOldPassword()) isValid = false
        if (!validatePassword()) isValid = false
        if (!validateConfirmPassword()) isValid = false
        return isValid
    }

    private fun initializeViews() {
        mBinding.apply {
            changeButton.setOnClickListener(this@ChangePasswordDialogFragment)
            cancelButton.setOnClickListener(this@ChangePasswordDialogFragment)
            oldPasswordEt.onFocusChangeListener = this@ChangePasswordDialogFragment
            passwordEt.onFocusChangeListener = this@ChangePasswordDialogFragment
            confirmPasswordEt.onFocusChangeListener = this@ChangePasswordDialogFragment
        }

    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                mBinding.oldPasswordEt.id -> {
                    if (hasFocus) {
                        if (mBinding.oldPasswordTil.isErrorEnabled)
                            mBinding.oldPasswordTil.isErrorEnabled = false
                    } else {
                        validateOldPassword()
                    }
                }

                mBinding.passwordEt.id -> {
                    if (hasFocus) {
                        if (mBinding.passwordTil.isErrorEnabled)
                            mBinding.passwordTil.isErrorEnabled = false

                    } else {
                        validatePassword()
                    }
                }

                mBinding.confirmPasswordEt.id -> {
                    if (hasFocus) {
                        if (mBinding.confirmPasswordTil.isErrorEnabled)
                            mBinding.confirmPasswordTil.isErrorEnabled = false

                    } else {
                        validateConfirmPassword()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(
            passwordUpdateListener: PasswordUpdateListener
        ): ChangePasswordDialogFragment {
            return ChangePasswordDialogFragment().apply {
                mPasswordUpdateListener = passwordUpdateListener
            }
        }
    }

    interface PasswordUpdateListener {
        fun onPasswordUpdated(oldPassword: String, newPassword: String)
    }
}