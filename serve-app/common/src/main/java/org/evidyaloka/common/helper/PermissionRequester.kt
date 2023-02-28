package org.evidyaloka.common.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.evidyaloka.common.R

class PermissionRequester(
    fragment: Fragment,
    private val permission: String,
    rationalMessage: Int? = null,
    settingsMessage: Int? = null,
    private val onDenied: () -> Unit = {}
) {
    private lateinit var onGranted: () -> Unit

    @SuppressLint("NewApi")
    private val launcher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when {
                        isGranted -> onGranted()
                        fragment.shouldShowRequestPermissionRationale(permission) -> showRationalDialog(
                            fragment.requireContext(),
                            rationalMessage
                        )
                        !fragment.shouldShowRequestPermissionRationale(permission) -> showSettingsDialog(
                            fragment.requireContext(),
                            settingsMessage
                        )
                        else -> onDenied()
                    }
                } else {
                    onGranted()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    val startForResult = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (ContextCompat.checkSelfPermission(fragment.requireContext(),permission) == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        }else{
            onDenied()
        }
    }

    fun runWithPermission(
        onGranted: () -> Unit
    ) {
        this.onGranted = onGranted
        launcher.launch(permission)
    }

    private fun showRationalDialog(context: Context, rationalMessage: Int?) {
        rationalMessage?.let {
            try {
                AlertDialog.Builder(context)
                    .setMessage(context.getString(it))
                    .setPositiveButton(
                        context.getString(R.string.ok),
                        DialogInterface.OnClickListener { _, _ ->
                    launcher.launch(permission)
                        })
                    .setNegativeButton(context.getString(R.string.cancel), null)
                    .create()
                    .show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun showSettingsDialog(context: Context, settingsMessage: Int?) {
        try {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.permission_required))
                .setMessage(context.getString(R.string.permission_ask_setting))
                .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                    startForResult.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        data = Uri.parse("package:${context.packageName}")
                    })
                }
                .setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
                    onDenied()
                }
                .create()
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}