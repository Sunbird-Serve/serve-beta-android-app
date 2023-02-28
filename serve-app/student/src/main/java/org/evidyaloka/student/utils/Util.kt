package org.evidyaloka.student.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.student.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Util {

    fun dakerColor(color: Int, factor: Float, alpha: Int = 0): Int {
        val a: Int = alpha.takeIf { it > 0 } ?: Color.alpha(color)
        val r = Math.round(Color.red(color) * factor).toInt()
        val g = Math.round(Color.green(color) * factor).toInt()
        val b = Math.round(Color.blue(color) * factor).toInt()
        return Color.argb(
            a,
            Math.min(r, 255),
            Math.min(g, 255),
            Math.min(b, 255)
        )
    }

    fun lighterColor(color: Int, factor: Float): Int {
        val red =
            ((Color.red(color) * (1 - factor) / 255 + factor) * 255).toInt()
        val green =
            ((Color.green(color) * (1 - factor) / 255 + factor) * 255).toInt()
        val blue =
            ((Color.blue(color) * (1 - factor) / 255 + factor) * 255).toInt()
        return Color.argb(Color.alpha(color), red, green, blue)
    }

    fun genderPlaceHolderImage(context: Context, gender: String, randomVal: Int): Int {
        if (CommonConst.Gender.Girl.toString().equals(gender)) {
            return when (randomVal) {
                0 -> R.drawable.ic_girl_1
                1 -> R.drawable.ic_girl_2
                else -> R.drawable.ic_girl_3
            }
        } else {
            return when (randomVal) {
                0 -> R.drawable.ic_boy_1
                1 -> R.drawable.ic_boy_2
                else -> R.drawable.ic_boy_3
            }
        }
    }

    /**
     * Popup to notify user
     * @param context: Context
     * @param title: title of the popup
     * @param message: message to be shown
     */
    fun showPopupDialog(
        context: Context,
        title: String? = null,
        message: String? = "",
        icon: Int? = null,
        buttonText: String? = null,
        onClickListener: View.OnClickListener? = null
    ): AlertDialog? {
        var dialog: AlertDialog? = null
        var dialogBuilder: MaterialAlertDialogBuilder? = null

        context?.let {
            val inflater =
                it?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            //TODO Use DialogFragment instead of dialog, can cause memory leaks, else needs to handle it manually every time
            val alertLayout: View = inflater.inflate(R.layout.custom_dialog_student, null)
            title?.let{
                alertLayout?.findViewById<TextView>(R.id.dlg_title)?.apply {
                    text = title
                    visibility = View.VISIBLE
                }
            }
            alertLayout?.findViewById<TextView>(R.id.dlg_message)?.text = message
            icon?.let {
                alertLayout?.findViewById<ImageView>(R.id.icon)?.setImageResource(it)
            }
            dialogBuilder = MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialogTheme)
                .setView(alertLayout).setCancelable(true)
            alertLayout?.findViewById<Button>(R.id.okay)?.apply {
                buttonText?.let { text = it.toString() }
                setOnClickListener {
                    onClickListener?.onClick(it)
                    dialog?.dismiss()
                }
            }
        }
        dialog = dialogBuilder?.create()
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        return dialog
    }

    fun showRetryPopupDialog(
        context: Context,
        title: String? = "",
        message: String? = "",
        icon: Int? = null,
        onClick: () -> Unit
    ): AlertDialog? {
        var dialog: AlertDialog? = null

        context?.let {
            val inflater =
                it?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            //TODO Use DialogFragment instead of dialog, can cause memory leaks, else needs to handle it manually every time
            val alertLayout: View = inflater.inflate(R.layout.custom_dialog_student, null)
            alertLayout?.findViewById<TextView>(R.id.dlg_title)?.apply {
                text = title
                visibility = View.VISIBLE
            }
            alertLayout?.findViewById<TextView>(R.id.dlg_message)?.text = message
            icon?.let {
                alertLayout?.findViewById<ImageView>(R.id.icon)?.setImageResource(it)
            }
            dialog = MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialogTheme)
                .setView(alertLayout).setCancelable(true).create()
            alertLayout?.findViewById<TextView>(R.id.okay)?.apply {
                setOnClickListener { view ->
                    dialog?.dismiss()
                    onClick.invoke()
                }
                text = it.getString(R.string.retry)
            }
        }
        return dialog
    }

    /**
     * Popup to notify user
     * @param context: Context
     * @param title: title of the popup
     * @param message: message to be shown
     */
    fun showLoadingPopupDialog(
        context: Context,
        title: String? = null,
        message: String? = null
    ): AlertDialog? {
        var dialog: AlertDialog? = null

        context?.let {
            val inflater =
                it?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            //TODO Use DialogFragment instead of dialog, can cause memory leaks, else needs to handle it manually every time
            val alertLayout: View = inflater.inflate(R.layout.custom_loading_dialog, null)
            title?.let { alertLayout?.findViewById<TextView>(R.id.dlg_title)?.text = it }

            message?.let { alertLayout?.findViewById<TextView>(R.id.dlg_message)?.text = it }
            dialog = MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialogTheme)
                .setView(alertLayout).setCancelable(true).create()

        }
        return dialog
    }

    fun getLocalizedDays(context: Context, timeSlot: String) {
        when (timeSlot) {
            StudentConst.earlyMorning -> {
                context.resources.getString(R.string.early_morning)
            }
            StudentConst.morning -> {
                context.resources.getString(R.string.morning)
            }
            StudentConst.afternoon -> {
                context.resources.getString(R.string.afternoon)
            }
            StudentConst.evening -> {
                context.resources.getString(R.string.evening)
            }
        }
    }

    fun formatTimeToHHmmAA(time: String, locale: Locale? = null): String {
        val oldFormat = "HH:mm"
        val newFormat = "hh:mm a"

        var result = ""

        val fromSdf = SimpleDateFormat(oldFormat)
        val toSdf = locale?.let {
            SimpleDateFormat(newFormat, it)
        } ?: SimpleDateFormat(newFormat)

        try {
            result = toSdf.format(fromSdf.parse(time))
        } catch (e: ParseException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }

        return result
    }

    /**
     * to prevent bottomsheet covering navigation bar.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun setWhiteNavigationBar(@NonNull dialog: Dialog) {
        try {
            val window: Window? = dialog.window
            if (window != null) {
                val metrics = DisplayMetrics()
                window.getWindowManager().getDefaultDisplay().getMetrics(metrics)
                val dimDrawable = GradientDrawable()
                // ...customize your dim effect here
                val navigationBarDrawable = GradientDrawable()
                navigationBarDrawable.shape = GradientDrawable.RECTANGLE
                navigationBarDrawable.setColor(Color.WHITE)
                val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
                val windowBackground = LayerDrawable(layers)
                windowBackground.setLayerInsetTop(1, metrics.heightPixels)
                window.setBackgroundDrawable(windowBackground)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun getActivityBackground(pos: Int): Int {
        var result = pos % 10

        return when (result) {
            9 -> {
                return R.drawable.ic_activity_9
            }
            8 -> {
                return R.drawable.ic_activity_8
            }
            7 -> {
                return R.drawable.ic_activity_7
            }
            6 -> {
                return R.drawable.ic_activity_6
            }
            5 -> {
                return R.drawable.ic_activity_5
            }
            4 -> {
                return R.drawable.ic_activity_4
            }
            3 -> {
                return R.drawable.ic_activity_3
            }
            2 -> {
                return R.drawable.ic_activity_2
            }
            1 -> {
                return R.drawable.ic_activity_1
            }
            0 -> {
                return R.drawable.ic_activity_10
            }
            else -> return R.drawable.ic_activity_10
        }
    }

}