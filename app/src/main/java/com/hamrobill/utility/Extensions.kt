package com.hamrobill.utility

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.hamrobill.R
import java.text.SimpleDateFormat
import java.util.*

private const val DEFAULT_INPUT_FORMAT = "yyyy-MM-dd'T'hh:mm:ss"
private const val DEFAULT_OUTPUT_FORMAT = "yyyy-MM-dd"
fun FragmentActivity.hideKeyboard() {
    val view: View? = currentFocus
    val imm = view?.let {
        view.clearFocus()
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.vibrate() {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                350,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }

    val animation = AnimationUtils.loadAnimation(context, R.anim.vibrate)
    startAnimation(animation)
}

fun FragmentActivity.showToast(message: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, length).apply {
        setGravity(Gravity.TOP or Gravity.CENTER, 0, 0)
    }.show()
}

fun Context.isTablet(): Boolean = resources.getBoolean(R.bool.isTablet)
fun Context.isLandScape(): Boolean =
    resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun FragmentActivity.windowWidth(): Int {
    val displayRectangle = Rect()
    window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
    return displayRectangle.width()
}

fun FragmentActivity.windowHeight(): Int {
    val displayRectangle = Rect()
    window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
    return displayRectangle.height()
}


private var progressDialog: AlertDialog? = null
fun FragmentActivity.showProgressDialog() {
    val view = layoutInflater.inflate(R.layout.progress_dialog, null)
    progressDialog = AlertDialog.Builder(this)
        .setView(view)
        .setCancelable(false)
        .create()
    progressDialog?.show()
    //Dialog window is set only after it is shown
    progressDialog?.window?.setLayout(
        550,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
}

fun hideProgressDialog() {
    progressDialog?.dismiss()
}

fun Calendar.getISOFormattedStringDate(): String {
    return SimpleDateFormat(DEFAULT_INPUT_FORMAT, Locale.US)
        .format(Calendar.getInstance().let { time })
}

fun Calendar.getFormattedStringDate(format: String = DEFAULT_OUTPUT_FORMAT): String {
    return SimpleDateFormat(format, Locale.US)
        .format(Calendar.getInstance().let { time })
}

fun String.getCalenderDate(inputFormat: String = "EEE, dd MMM yyyy HH:mm:ss zzzz"): Calendar {
    val dateFormat = SimpleDateFormat(inputFormat, Locale.US)
    val date = dateFormat.parse(this)
    return Calendar.getInstance().apply { time = date!! }
}

fun String.getUserName(): String {
    val lastAtIndex = lastIndexOf("@")
    return substring(0, lastAtIndex)
}

fun String.getOrderQuantity(): String {
    return try {
        val lastUnderscoreIndex = lastIndexOf(".")
        if(substring(lastUnderscoreIndex + 1, length).toInt() > 0){
            this
        }else{
            substring(0, lastUnderscoreIndex)
        }
    } catch (e: Exception) {
        this
    }
}