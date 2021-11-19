package com.hamrobill.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.hamrobill.HamrobillApp
import com.hamrobill.R
import com.hamrobill.utils.SharedPreferenceStorage
import com.hamrobill.utils.getCalenderDate
import java.util.*
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var mSharedPreferenceStorage: SharedPreferenceStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as HamrobillApp).applicationComponent.getActivityComponentFactory()
            .create(baseContext).inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            val tokenExpiryTime = mSharedPreferenceStorage.tokenExpiresAt
            val intent: Intent =
                if (tokenExpiryTime != null && tokenExpiryTime.getCalenderDate() > Calendar.getInstance()) {
                    Intent(this, MainActivity::class.java)
                } else {
                    Intent(this, LoginActivity::class.java)
                }
            startActivity(intent)
            finish()
        }, 1500)
    }
}