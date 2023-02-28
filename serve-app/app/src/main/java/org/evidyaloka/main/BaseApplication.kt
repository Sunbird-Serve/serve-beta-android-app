package org.evidyaloka.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import org.evidyaloka.BuildConfig
import org.evidyaloka.core.Constants.CommonConst
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 23-12-2020
 *
 */
//@AcraCore(buildConfigClass = org.acra.BuildConfig::class,
//        reportFormat = StringFormat.JSON)
//@AcraMailSender(mailTo = "madhankumar.e@digital.datamatics.com",
//    resSubject = R.string.acra_mail_subject,
//    reportFileName = "ErrorReport",
//    reportAsFile = true)
//@AcraToast(resText = R.string.acra_toast_text)
@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider  {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        Lingver.init(this, CommonConst.LANGUAGE_ENGLISH)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)


//        val builder = CoreConfigurationBuilder(this)
//            .setBuildConfigClass(org.acra.BuildConfig::class.java)
//            .setReportFormat(StringFormat.JSON)
//        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder::class.java)
//            .setResText(R.string.acra_toast_text)
//        builder.getPluginConfigurationBuilder(
//            MailSenderConfigurationBuilder::class.java
//        )
//            .setMailTo("madhankumar.e@digital.datamatics.com")
//            .setResSubject(R.string.acra_mail_subject)
//            .setReportFileName("ErrorReport")
//            .setReportAsFile(true)
//        ACRA.init(this, builder)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}