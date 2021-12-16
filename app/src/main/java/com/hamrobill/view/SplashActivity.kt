package com.hamrobill.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.hamrobill.R
import com.hamrobill.di.subcomponent.ActivityComponent
import com.hamrobill.utility.*
import com.hamrobill.view.base_url_dialog_fragment.BaseUrlDialogFragment
import java.util.*
import javax.inject.Inject

class SplashActivity : DICompactActivity(), BaseUrlDialogFragment.UrlUpdateListener {

    @Inject
    lateinit var mSharedPreferenceStorage: SharedPreferenceStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        determineRouting()

    }

    override fun configureDependencyInjection(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    private fun determineRouting() {
        if (mSharedPreferenceStorage.remoteBaseUrl.isNullOrEmpty() || mSharedPreferenceStorage.remoteBaseUrl.isNullOrEmpty()) {
            BaseUrlDialogFragment.getInstance(mSharedPreferenceStorage, this).apply {
                showNow(supportFragmentManager, tag)
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val tokenExpiryTime = mSharedPreferenceStorage.tokenExpiresAt
                val intent: Intent =
                    if (tokenExpiryTime != null && tokenExpiryTime.getCalenderDate() > Calendar.getInstance()) {
                        Intent(this, MainActivity::class.java)
                    } else {
                        Intent(this, LoginActivity::class.java)
                    }
                hideProgressDialog()
                startActivity(intent)
                finish()
            }, 1500)
        }
    }

    override fun onUrlUpdated() {
        showProgressDialog()
        determineRouting()
    }
}