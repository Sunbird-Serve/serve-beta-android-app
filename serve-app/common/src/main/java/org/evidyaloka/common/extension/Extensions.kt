package org.evidyaloka.common.helper

import android.R
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Patterns
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */

/**
 * checks whether current String provided is valid Email address or not
 * @return Boolean
 */
fun String.isValidEmail(): Boolean =
    this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

/**
 * checks whether current String provided is valid Name or not
 * @return Boolean
 */
fun String.isValidName(): Boolean = this.isNotEmpty() && this.matches("^[a-zA-Z ]*$".toRegex())

/**
 * checks whether current String provided is valid Phone number or not
 * @return Boolean
 */
fun String.isValidPhoneNumber(): Boolean =
    this.isNotEmpty() && this.matches("^[0-9]{10}$".toRegex())

/**
 * checks whether current String provided is valid Pincode or not
 * @return Boolean
 */
fun String.isValidPincode(): Boolean =
    this.isNotEmpty() && this.matches("^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}\$".toRegex())


/**
 * checks whether current String provided is valid Password or not
 * @return Boolean
 */
fun String.isValidPassword(): Boolean =
    this.isNotEmpty() && this.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,32}\$".toRegex())

/**
 * Get File Format
 * @return String
 */
fun String.fileFormat(): String = this.substring(this.lastIndexOf(".") + 1)

fun String.convert24to12a(): String {
    val sdf = SimpleDateFormat("H:mm")
    val dateObj = sdf.parse(this)
    return SimpleDateFormat("hh:mm a").format(dateObj)
}

fun String.convertShortDateWitha(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd H:mm")
    val dateObj = sdf.parse(this)
    return SimpleDateFormat("dd MMM hh:mm a").format(dateObj)
}

fun String.getMimeType(): String? {
    return MimeTypeMap.getFileExtensionFromUrl(this)?.let {
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
    }
}

/**
 * Get File Name
 * @param context Context
 * @return String
 */
fun Uri.getFileName(context: Context): String? {
    var result: String? = null
    if (this.scheme.equals("content")) {
        val cursor: Cursor? = context.contentResolver.query(this, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = this.path?.lastIndexOf('/')?.let {
            this.path?.substring(it + 1)
        }
    }
    return result
}

inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}

/**
 * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

/**
 * Function to place cursor to end of the EditText
 */
fun EditText.placeCursorToEnd() {
    this.setSelection(this.text.length)
}

/**
 * finds value on given key.
 * [T] is the type of value
 * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 */
inline operator fun <reified T : Any> SharedPreferences.get(
    key: String,
    defaultValue: T? = null
): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}


fun CheckBox.setButtonColor(color: Int) {
    val colorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-R.attr.state_checked), // unchecked
            intArrayOf(R.attr.state_checked) // checked
        ), intArrayOf(
            Color.TRANSPARENT, // unchecked color
            color // checked color
        )
    )

    // finally, set the radio button's button tint list
    buttonTintList = colorStateList

//    // optionally set the button tint mode or tint blend mode
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        buttonTintBlendMode = BlendMode.SRC_IN
//    }else{
//        buttonTintMode = PorterDuff.Mode.SRC_IN
//    }

    invalidate() //could not be necessary
}

fun ImageView.loadUrlWithGlideCircle(
    url: String,
    errorImage: Int? = null,
    placeHolder: Int? = null,
    requestOptions: RequestOptions? = null
) {
    val glide = Glide.with(this)
        .load(url)
    errorImage?.let {
        glide.error(it)
        glide.placeholder(it)
    }
    placeHolder?.let { glide.placeholder(it) }
    glide.circleCrop()
    requestOptions?.let { glide.apply(it) }
    glide.into(this)
}

fun ImageView.loadUrlWithGlide(
    url: String,
    errorImage: Int? = null,
    placeHolder: Int? = null,
    requestOptions: RequestOptions? = null
) {
    val glide = Glide.with(this)
        .load(url)
    errorImage?.let {
        glide.error(it)
        glide.placeholder(it)
    }
    placeHolder?.let { glide.placeholder(it) }
    requestOptions?.let { glide.apply(it) }
    glide.into(this)
}

fun ImageView.loadBitmap(
    bitmap: Bitmap?,
    errorImage: Int? = null,
    placeHolder: Int? = null,
    requestOptions: RequestOptions? = null
) {
    val glide = Glide.with(this)
        .load(bitmap)
    errorImage?.let {
        glide.error(it)
        glide.placeholder(it)
    }
    glide.into(this)
}

fun ImageView.loadDrawable(
    drawable: Int?,
    errorImage: Int? = null,
    placeHolder: Int? = null
) {
    val glide = Glide.with(this)
        .load(drawable)
    errorImage?.let {
        glide.error(it)
        glide.placeholder(it)
    }
    placeHolder?.let { glide.placeholder(it) }
    glide.into(this)
}

/**
 * To Avoid multiple clicks on buttons.
 */
fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

