package org.evidyaloka.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.util.regex.Matcher
import java.util.regex.Pattern


class OtpBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private var mListener: BroadcastListener? = null

        fun bindListener(listener: BroadcastListener) {
            mListener = listener
        }
    }

    private val TAG = "OtpBroadcastReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent?.extras
            extras?.let {
                val status: Status? = it[SmsRetriever.EXTRA_STATUS] as Status?
                when (status?.getStatusCode()) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get SMS message contents
                        var message: String? = it[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                        Log.e(TAG, "onReceive: " + message)
                        message?.let {
                            try {
                                val pattern: Pattern = Pattern.compile("(\\d{4,6})")
                                var matcher: Matcher = pattern.matcher(it)

                                var otp = ""
                                if (matcher.find()) {
                                    otp = matcher.group(0) // 4 digit number
                                    Log.e(TAG, "onCreate: " + otp)
                                    mListener?.onOtpReceived(otp as String)
                                }
                            } catch (e: Exception) {
                                FirebaseCrashlytics.getInstance().recordException(e)
                            }
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        //listener?.onOTPTimeOut()
                    }
                    else -> {
                    }
                }
            }
        }
    }
}