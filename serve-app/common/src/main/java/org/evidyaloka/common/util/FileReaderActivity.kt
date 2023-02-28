package org.evidyaloka.common.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.evidyaloka.common.R
import org.evidyaloka.common.databinding.ActivityFileReaderBinding
import org.evidyaloka.core.Constants.StudentConst

class FileReaderActivity : AppCompatActivity() {
    private val TAG = FileReaderActivity::class.java.simpleName
    private lateinit var binding: ActivityFileReaderBinding
    private var url = ""
    private var title = ""
    private var hideDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        url = intent?.extras?.getString(StudentConst.WEB_URL) as String
        title = intent?.extras?.getString(StudentConst.PAGE_TITLE) as String
        hideDone = intent?.extras?.getBoolean("hideDone",false) == true
        binding = ActivityFileReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = title
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.buttonDone.setOnClickListener {
            val resultIntent = Intent()
            intent?.extras?.let { bundle -> resultIntent.putExtras(bundle) }
            setResult(
                RESULT_OK,
                resultIntent
            ) // You can also send result without any data using setResult(int resultCode)
            finish()
        }
        if(hideDone){
            binding.buttonDone.visibility = View.GONE
        }
        url?.let {
            binding.webView.apply {
                binding.webView.visibility = View.GONE
                binding.progressCircular.visibility = View.VISIBLE
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)
                        //some times PDF or files doesn't load in one time,
                        // to reload file again, if the title of webview is empty
                        if (view.title == "") {
                            view.reload()
                        } else {
                            //to disable google login popup
                            view.loadUrl(
                                "javascript:(function() { " +
                                        "document.querySelector('[role=\"toolbar\"]').remove();})()"
                            )
                            view.loadUrl("javascript:(function() {document.querySelector('[class=\"ndfHFb-c4YZDc-Wrql6b\"]').remove();})()")
                            view.loadUrl(
                                "javascript:(function() { " +
                                        "document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()"
                            )
                            binding.progressCircular.visibility = View.GONE
                            binding.webView.visibility = View.VISIBLE
                        }
                    }

                    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                        super.onReceivedError(view, request, error)
                        Log.e(TAG, "onReceivedError: $error")
                    }
                }
                loadUrl(it)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.webView.apply {
            stopLoading()
            removeAllViews()
            destroy()
            null
        }
    }

}