package org.evidyaloka.common.util

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mindev.mindev_pdfviewer.MindevPDFViewer
import com.mindev.mindev_pdfviewer.PdfScope
import org.evidyaloka.common.R
import org.evidyaloka.common.databinding.ActivityPdfReaderBinding
import org.evidyaloka.common.view.SuccessDialogFragment
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.student.database.entity.CourseContentEntity


open class PdfReaderActivity : AppCompatActivity() {
    lateinit var binding: ActivityPdfReaderBinding
    private var url: String? = null
    private var local_url: String? = null
    private var title : String? = null
    private var offlineContentDetails: CourseContentEntity? = null
    private var isDeepLink: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = intent?.extras?.getString(StudentConst.WEB_URL)
        local_url = intent?.extras?.getString(StudentConst.LOCAL_WEB_URL)
        title = intent?.extras?.getString(StudentConst.PAGE_TITLE)
        offlineContentDetails = intent?.extras?.getParcelable(StudentConst.OFFLINE_CONTENT)
        isDeepLink = intent?.extras?.getBoolean(StudentConst.IS_DEEP_LINK,false) == true

        binding = ActivityPdfReaderBinding.inflate(layoutInflater)
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

        local_url?.let{ path ->
            Uri.parse(path).path?.let {
                binding.pdf.fileInit(it,statusListener)
            }
        }?:url?.let {
            binding.pdf.initializePDFDownloader(it, statusListener)
            lifecycle.addObserver(PdfScope())
        }
    }

    override fun onDestroy() {
        binding.pdf.pdfRendererCore?.clear()
        super.onDestroy()
    }

    private val statusListener = object : MindevPDFViewer.MindevViewerStatusListener {
        override fun onStartDownload() {
            binding.progressCircular.visibility = View.VISIBLE
        }

        override fun onPageChanged(position: Int, total: Int) {

        }

        override fun onProgressDownload(currentStatus: Int) {

        }

        override fun onSuccessDownLoad(path: String) {
            try{
                binding.pdf.fileInit(path)
            }catch(e: Exception){
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            binding.progressCircular.visibility = View.GONE
        }

        override fun onFail(error: Throwable) {
            binding.progressCircular.visibility = View.GONE
            showErrorAlert()
        }

        override fun unsupportedDevice() {
            binding.progressCircular.visibility = View.GONE
            showErrorAlert()
        }
    }

    private fun showErrorAlert(){
        try {
            SuccessDialogFragment.Builder(this@PdfReaderActivity)
                .setIsDialogCancelable(true)
                .setDismissTimer(PartnerConst.DIALOG_CLOSE_TIME)
                .setTitle(getString(R.string.unknown_error))
                .setDescription(getString(R.string.unable_to_open_pdf_file))
                .setIcon(R.drawable.ic_inactive)
                .setViewType(SuccessDialogFragment.DIALOG_TYPE.ALERT)
                .setOnDismissListener(object : SuccessDialogFragment.OnDismissListener {
                    override fun OnDismiss(dialog: DialogInterface) {
                        finish()
                    }
                }).build()
                .show(supportFragmentManager,null)
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

}
