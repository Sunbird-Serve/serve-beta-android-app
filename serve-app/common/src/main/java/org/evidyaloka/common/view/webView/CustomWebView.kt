package org.evidyaloka.common.view.webView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.evidyaloka.common.R

/**
 * @author Madhankumar
 * created on 04-02-2021
 *
 */
class CustomWebView(): Fragment() {

    private var url:String? = null
    private var title:String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            val args = CustomWebViewArgs.fromBundle(it)
            url = args.url
            title = args.title
        }
        return inflater.inflate(R.layout.fragment_custom_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView: WebView = view.findViewById<WebView>(R.id.webView)
        webView.isVerticalScrollBarEnabled = true
        webView.isHorizontalScrollBarEnabled = true
        webView.requestFocus()
        webView.webChromeClient = object : WebChromeClient(){
            override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                Log.d("evidyaloka-web", "${message.message()} -- From line " +
                        "${message.lineNumber()} of ${message.sourceId()}")
                return true
            }
        }
        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.url?.toString()?.let { view?.loadUrl(it) }
                return true
            }
        }
        webView.settings.apply {
            defaultTextEncodingName = "utf-8"
            javaScriptEnabled = true
            loadsImagesAutomatically = true
            useWideViewPort = true
        }
        if(url != null) {
            webView.loadUrl(url!!)
        }else{
            findNavController().navigateUp()
        }
    }


}