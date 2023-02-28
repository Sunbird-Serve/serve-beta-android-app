package org.evidyaloka.common.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.common.BuildConfig
import org.evidyaloka.common.R
import org.evidyaloka.common.helper.DeviceUuidFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author Madhankumar
 * created on 30-12-2020
 *
 */
object Utils {

    /**
     * Method to force open Android keyboard
     * @param activity current Activity
     * @return Nothing
     */
    fun showKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    /*
     * Get Grade in Roman Numeral
     * @param grade Int grade selected
     * @return String
     */
    fun getFormatedGrade(grade: Int) = when (grade) {
        1 -> "I"
        2 -> "II"
        3 -> "III"
        4 -> "IV"
        5 -> "V"
        6 -> "VI"
        7 -> "VII"
        8 -> "VIII"
        9 -> "IX"
        10 -> "X"
        11 -> "XI"
        12 -> "XII"
        else -> ""
    }

    /*
    * Get Suffix for Grade
    * @param i Int grade selected
    * @return String
    */
    fun ordinalSuffix(i: Int): String {
        var j = i % 10
        var k = i % 100
        if (j == 1 && k != 11) {
            return "st";
        }
        if (j == 2 && k != 12) {
            return "nd";
        }
        if (j == 3 && k != 13) {
            return "rd";
        }
        return "th";
    }

    fun formatDate(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat("dd MM yyyy")
            val netDate = Date(timestamp)
            return sdf.format(netDate)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
            return null
        }
    }

    /*
     * converts timestamp to String in d MMMM yyyy date format
     * @param timestamp A Long value to represent timestamp
     * @return A String
     */
    fun formatddMMMMyyyy(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat("d MMMM yyyy")
            val netDate = Date(timestamp)
            return sdf.format(netDate)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
            return null
        }
    }

    fun formatddMMMMyyyyFromddMMyyyy(time: String): String? {
        try {
            val original = SimpleDateFormat("dd-MM-yyyy")
            val sdf = SimpleDateFormat("d MMM yyyy")
            return sdf.format(original.parse(time))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
            return null
        }
    }

    /*
     * converts timestamp to String in d MMMM date format
     * @param timestamp A Long value to represent timestamp
     * @return A String
     */
    fun formatDatedMMMM(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat("d MMMM")
            val netDate = Date(timestamp)
            return sdf.format(netDate)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
            return null
        }
    }

    fun formatDatedMonthText(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat("MMMM")
            val netDate = Date(timestamp)
            return sdf.format(netDate)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
            return null
        }
    }

    /**
     * converts timestamp to String in yyyy-MM-dd date format
     * @param timestamp A Long value to represent timestamp
     * @return A String
     */
    fun formatyyyy_MM_dd(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val netDate = Date(timestamp)
            return sdf.format(netDate)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
            return null
        }
    }

    fun format_dd_MM_yyyy(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val netDate = Date(timestamp)
            return sdf.format(netDate)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
            return null
        }
    }

    fun formatyyyy_MM_dd_To_dd_MM_yyyy(dateStr: String): String? {
        try {
            var originalFormat = SimpleDateFormat("yyyy-MM-dd")
            var targetFormat = SimpleDateFormat("dd-MM-yyyy")

            var formattedDate = targetFormat.format(originalFormat.parse(dateStr))
            return formattedDate
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return null
    }

    fun format_dd_MM_yyyy_To_yyyy_MM_dd(dateStr: String): String? {
        try {
            var originalFormat = SimpleDateFormat("dd-MM-yyyy")
            var targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            var formattedDate = targetFormat.format(originalFormat.parse(dateStr))
            return formattedDate
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return null
    }

    /**
     * Get current Date String in the form of d MMM yyyy
     */
    fun getCurrentDateIndMMMyyyy(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy")
        val netDate = Date()
        return sdf.format(netDate)
    }

    /**
     * Get current Date String in the form of d MMM yyyy
     */
    fun getTimeIn12H(time: Long): String {
        val sdf = SimpleDateFormat("hh:mm a")
        val netDate = Date(time)
        return sdf.format(netDate)
    }

    /**
     * Get current Date String in the form of d MMM yyyy
     */
    fun getCurrentTimeIn12H(): String {
        val sdf = SimpleDateFormat("hh:mm a")
        val netDate = Date()
        return sdf.format(netDate)
    }

    fun format12hFrom24h(time: String): String? {
        try {
            val original = SimpleDateFormat("HH:mm")
            val sdf = SimpleDateFormat("hh:mm a")
            return sdf.format(original.parse(time))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return null
    }

    /**
     * Get String date in dd MMMM yyyy format
     * @param dateStr String format - yyyy-MM-dd'T'HH:mm:ss
     * @return A String in dd MMMM yyyy format
     */
    fun formatDateInddMMMMyyyy(dateStr: String): String? {
        try {
            var originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            var targetFormat = SimpleDateFormat("dd MMMM yyyy")

            var formattedDate = targetFormat.format(originalFormat.parse(dateStr))
            return formattedDate
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return null
    }

    fun formatDateInddMMMyyyyFromyyyyMMddHHmmss(dateStr: String): String? {
        try {
            var originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var targetFormat = SimpleDateFormat("dd MMM yyyy")
            var formattedDate = targetFormat.format(originalFormat.parse(dateStr))
            return formattedDate
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return null
    }

    fun formatDateInhhmmFromyyyyMMddHHmmssa(dateStr: String): String? {
        try {
            var originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var targetFormat = SimpleDateFormat("hh:mm a")
            var formattedDate = targetFormat.format(originalFormat.parse(dateStr))
            return formattedDate
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return null
    }

    fun formatDateInddMMMMyyyyFromyyyyMMdd(dateStr: String): String? {
        try {
            var originalFormat = SimpleDateFormat("yyyy-MM-dd")
            var targetFormat = SimpleDateFormat("dd MMMM yyyy")

            var formattedDate = targetFormat.format(originalFormat.parse(dateStr))
            return formattedDate
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return null
    }

    /**
     * Get device info in the form of Map
     * @return Map<String, Any>
     */
    fun getDeviceInfo(context: Context, userType: String?): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()
            if (userType != null) {
                map.put("appType", userType)
            }
            map.put("osType","android")
            map.put("appVersion", BuildConfig.VersionCode)
            map.put("osVersion", android.os.Build.VERSION.RELEASE)
            map.put("sdkVersion", android.os.Build.VERSION.SDK_INT)
            map.put("device", android.os.Build.DEVICE)
            map.put("model", android.os.Build.MODEL)
            getDeviceId(context)?.let {
                map.put("deviceId", it)
            }
            map.put("additionalDeviceId", UUID.nameUUIDFromBytes(Settings.Secure.getString(
                context.contentResolver, Settings.Secure.ANDROID_ID).toByteArray(Charsets.UTF_8)))

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return map
    }

    fun getDeviceId(context: Context) = DeviceUuidFactory.getDeviceUuid(context)

    /**
     * This method gets InputStream from content resolver
     *
     * @param uri Uri
     * @param context Context
     * @return InputStream
     */
    fun getFileInputStream(context: Context, uri: Uri) = context.contentResolver?.openInputStream(
        uri
    )


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * Function to format timeout in 00m 00s format (OTP Screen timer)
     */
    fun formatTimout(duration: Int): String {
        return String.format(
            "%02dm %02ds",
            (duration % 3600) / 60, (duration % 60)
        );
    }

    fun convertddMMyyyyHhmmToLong(time: String): Long? {
        var date: Long? = null
        try {
            val df: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
            date = df.parse(time).getTime()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)

        }
        return date
    }

    fun convertyyyyMMhhhmmToLong(time: String): Long? {
        var date: Long? = null
        try {
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            date = df.parse(time).getTime()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)

        }
        return date
    }

    fun getCurrentDateInddMMyyyy(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val netDate = Date()
        return sdf.format(netDate)
    }

    fun getCurrentDateInyyyyMMddHHmmss(): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val netDate = Date()
        return sdf.format(netDate)
    }

    fun getDurationInHHmmss(durationInSecs: Int): String? {
        var sdf = SimpleDateFormat("ss");

        try {
            var date = sdf.parse("" + durationInSecs);
            sdf = SimpleDateFormat("HH:mm:ss");

            return sdf.format(date)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            return ""
        }

    }

    fun isNetworkAvailable(activity: Activity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun getAbbreviatedDayName(dayName: String): String{
        return dayName.take(3)
    }

    fun getLocalDateInSeconds(year:Int?=null,month:Int?=null,date:Int?=null,hours:Int = 23, minute:Int=59, second:Int=59):Long{
        val cal = Calendar.getInstance(TimeZone.getDefault()).apply {
            set(year?:this.get(Calendar.YEAR), month?:this.get(Calendar.MONTH), date?:this.get(Calendar.DATE),hours,minute,second)
        }
        return  cal.timeInMillis + TimeZone.getDefault().rawOffset
    }

    fun getLocalDateInSeconds(cal: Calendar):Long{
        return getLocalDateInSeconds(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE))
    }

    fun getClassTime(durationInmins: Int): String {
        var sdf = SimpleDateFormat("mm");

        try {
            var date = sdf.parse("" + durationInmins);
            sdf = SimpleDateFormat("hh:mm a");

            return sdf.format(date)
        } catch (e: Exception) {
            return ""
        }
    }

    fun getCourseProvidersDrawable(boardName: String?): Int? {
        return when(boardName?.toLowerCase()){
            "apsb" -> R.mipmap.apsb
            "bseb" -> R.mipmap.bseb
            "gseb" -> R.mipmap.gseb
            "jacb" -> R.mipmap.jacb
            "dsert" -> R.mipmap.kseeb
            "mhsb" -> R.mipmap.mhsb
            "opepa" -> R.mipmap.opepa
            "scert" -> R.mipmap.scert
            "tnsb" -> R.mipmap.tnsb
            "ubse" -> R.mipmap.ubse
            "upmsp" -> R.mipmap.upnsp
            "wbsed" -> R.mipmap.wbbse
            "cbse" -> R.mipmap.cbse
            else -> R.drawable.ic_school_logo_placeholder
        }
    }


}