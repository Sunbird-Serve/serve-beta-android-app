package org.evidyaloka.common.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.crashlytics.FirebaseCrashlytics

fun Activity.hideKeyboard() {
    try {
        val view: View = this.findViewById(android.R.id.content)
        if (view != null) {
            val imm: InputMethodManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        e.printStackTrace()
    }
}

fun Context.startActivityFromString(activityPath: String) {
    try {
        val intent = Intent(this, Class.forName(activityPath))
        startActivity(intent)
        (this as Activity).finish()
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
    }
}