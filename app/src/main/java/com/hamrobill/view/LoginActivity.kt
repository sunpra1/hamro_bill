package com.hamrobill.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hamrobill.HamrobillApp
import com.hamrobill.R
import com.hamrobill.data.pojo.LoginRequest
import com.hamrobill.databinding.ActivityLoginBinding
import com.hamrobill.utils.*
import com.hamrobill.view_model.LoginActivityViewModel
import java.util.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mViewModel: LoginActivityViewModel
    private lateinit var mBinding: ActivityLoginBinding
    private var mPreviousConnectivityStatus = true

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as HamrobillApp).applicationComponent.getActivityComponentFactory()
            .create(baseContext).inject(this)
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        supportActionBar?.elevation = 0F
        supportActionBar?.title = resources.getString(R.string.login)
        mViewModel = ViewModelProvider(this, mViewModelFactory)[LoginActivityViewModel::class.java]

        initializeView()
        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.isLoading.observe(this) {
            if (it) showProgressDialog() else hideProgressDialog()
        }
        mViewModel.errorMessage.observe(this) {
            val message = when (it) {
                is String -> it
                is Int -> getString(it)
                else -> getString(R.string.something_went_wrong)
            }
            AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle("INFORMATION")
                .setIcon(R.drawable.information_24)
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        mViewModel.isNetworkAvailable.observe(this) {
            if (!mPreviousConnectivityStatus)
                Handler(Looper.getMainLooper()).postDelayed({
                    mBinding.netWorkConnectivity.root.visibility =
                        if (it) View.GONE else View.VISIBLE
                }, 1000)
            else
                mBinding.netWorkConnectivity.root.visibility = if (it) View.GONE else View.VISIBLE
            mBinding.netWorkConnectivity.networkStatus.text =
                if (it) getString(R.string.connected) else getString(R.string.no_internet)
            mBinding.netWorkConnectivity.networkStatus.setTextColor(getColor(if (it) R.color.green else R.color.red))
            mBinding.loginBtn.isEnabled = it
            mPreviousConnectivityStatus = it
        }
        mViewModel.isLoginSuccess.observe(this, EventObserver {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        })
    }

    private fun initializeView() {
        mBinding.apply {
            loginBtn.setOnClickListener(this@LoginActivity)
            emailEt.onFocusChangeListener = this@LoginActivity
            passwordEt.onFocusChangeListener = this@LoginActivity
        }
    }

    override fun onClick(view: View?) {
        if (view != null)
            when (view.id) {
                mBinding.loginBtn.id -> {
                    if (validate()) {
                        hideKeyboard()
                        mViewModel.loginUser(
                            LoginRequest(
                                mBinding.emailEt.text.toString(),
                                mBinding.passwordEt.text.toString()
                            ).getHashMap()
                        )
                    }
                }
            }
    }

    private fun validate(): Boolean {
        var isValid = true

        if (!validateEmail(shouldVibrateView = false)) isValid = false
        if (!validatePassword(shouldVibrateView = false)) isValid = false

        if (!isValid) mBinding.loginCardView.vibrate()

        return isValid
    }

    private fun validateEmail(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        val value = mBinding.emailEt.text.toString()
        val errorMessage: String? = when {
            value.isEmpty() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Provide valid email address"
            else -> null
        }
        if (errorMessage != null && shouldUpdateView) {
            mBinding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) vibrate()
            }
        }
        return errorMessage == null
    }

    private fun validatePassword(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        val value = mBinding.passwordEt.text.toString()
        val errorMessage: String? = when {
            value.isEmpty() -> "Password is required"
            value.length < 6 -> "Password must be 6 characters long"
            else -> null
        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) vibrate()
            }
        }
        return errorMessage == null
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                mBinding.emailEt.id -> {
                    if (hasFocus) {
                        if (mBinding.emailTil.isErrorEnabled)
                            mBinding.emailTil.isErrorEnabled = false
                    } else {
                        validateEmail()
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
            }
        }
    }
}