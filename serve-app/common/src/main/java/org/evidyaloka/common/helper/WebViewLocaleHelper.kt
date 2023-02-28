package org.evidyaloka.common.helper

import android.content.Context
import android.webkit.WebView
import androidx.annotation.MainThread
import com.yariksoffice.lingver.Lingver

/**
 * @author Madhankumar
 * created on 15-04-2021
 *
 */
class WebViewLocaleHelper(private val context: Context) {

    private var requireWorkaround = true

    @MainThread
    fun implementWorkaround() {
        if (requireWorkaround) {
            requireWorkaround = false
            WebView(context).destroy()
            val lingver = Lingver.getInstance()
            lingver.setLocale(context, lingver.getLocale())
        }
    }
}