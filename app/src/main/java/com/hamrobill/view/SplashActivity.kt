package com.hamrobill.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hamrobill.HamrobillApp
import com.hamrobill.R
import com.hamrobill.view_model.factory.ViewModelFactory
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as HamrobillApp).applicationComponent.getActivityComponentFactory()
            .create(baseContext).inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}