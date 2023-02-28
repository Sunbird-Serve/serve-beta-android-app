package org.evidyaloka.common.receiver

import android.content.BroadcastReceiver as OtpBroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.util.regex.Matcher
import java.util.regex.Pattern


class OtpReceiver<T>(
    val context: T,
    onOtpListner: (otp: String?, isTimeout: Boolean) -> Unit
) : LifecycleObserver where T : Fragment, T : LifecycleOwner {

    private val TAG = "OtpBroadcastReceiver"
    private val appContext = context.requireContext().applicationContext

    init {
        val client = SmsRetriever.getClient(context.requireContext())
        val task: Task<Void> = client.startSmsRetriever()

        task.addOnSuccessListener(OnSuccessListener<Void?> {
            context.lifecycle.addObserver(this)
        }).addOnFailureListener(OnFailureListener {
            Log.e(TAG,"Failed to start sms retriever")
        })

    }


    private val broadcastReceiver = object : OtpBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

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
                                        onOtpListner(otp,false)
                                        stop()
                                    }
                                } catch (e: Exception) {
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                }
                            }
                        }
                        CommonStatusCodes.TIMEOUT -> {
                            onOtpListner(null,true)
                            stop()
                        }
                        else -> {

                        }
                    }
                }
            }
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        Log.e(TAG,"@OnLifecycleEvent(Lifecycle.Event.ON_START)")
        appContext.registerReceiver(broadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        Log.e(TAG,"@OnLifecycleEvent(Lifecycle.Event.ON_STOP)")
        try {
            appContext.unregisterReceiver(broadcastReceiver)
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            context.lifecycle.removeObserver(this)
        }
    }
}