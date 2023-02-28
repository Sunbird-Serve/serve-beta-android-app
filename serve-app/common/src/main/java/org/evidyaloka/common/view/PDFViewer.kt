package org.evidyaloka.common.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.mindev.mindev_pdfviewer.*
import org.evidyaloka.common.R
import java.io.File

class PDFViewer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var orientation: Direction = Direction.HORIZONTAL
    private var isPdfAnimation: Boolean = false


    init {
        getAttrs(attrs, defStyleAttr)
    }

    @SuppressLint("CustomViewStyleable")
    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MindevPDFViewer, defStyle, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val ori =
            typedArray.getInt(R.styleable.MindevPDFViewer_pdf_direction, Direction.HORIZONTAL.ori)
        orientation = Direction.values().first { it.ori == ori }
        isPdfAnimation = typedArray.getBoolean(R.styleable.MindevPDFViewer_pdf_animation, false)
        typedArray.recycle()
    }

    var pdfRendererCore: PdfCore? = null
    private lateinit var statusListener: MindevPDFViewer.MindevViewerStatusListener
    private val pageTotalCount get() = pdfRendererCore?.getPDFPagePage() ?: 0

    fun initializePDFDownloader(url: String, statusListener: MindevPDFViewer.MindevViewerStatusListener) {
        this.statusListener = statusListener
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            statusListener.unsupportedDevice()
            return
        }
        val cacheFile = File(context.cacheDir, "mindevPDF.pdf")
        PdfDownloader(cacheFile, url, statusListener)
    }

    fun fileInit(path: String,statusListener: MindevPDFViewer.MindevViewerStatusListener) {
        this.statusListener = statusListener
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            statusListener.unsupportedDevice()
            return
        }
        fileInit(path)
    }

    fun fileInit(path: String) {
        pdfRendererCore = PdfCore(context, File(path))
        val view = LayoutInflater.from(context)
            .inflate(R.layout.pdf_viewer_view, this, false)
        addView(view)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this.context).apply {
                orientation =
                    if (this@PDFViewer.orientation.ori == Direction.HORIZONTAL.ori) {
                        LinearLayoutManager.HORIZONTAL
                    } else {
                        LinearLayoutManager.VERTICAL
                    }
            }
            if (pdfRendererCore != null) adapter = PdfAdapter(pdfRendererCore!!, isPdfAnimation)
            addOnScrollListener(scrollListener)
        }

        rv.let(PagerSnapHelper()::attachToRecyclerView)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            (recyclerView.layoutManager as LinearLayoutManager).run {
                var foundPosition = findFirstCompletelyVisibleItemPosition()
                if (foundPosition != RecyclerView.NO_POSITION) {
                    statusListener.onPageChanged(foundPosition, pageTotalCount)
                    return@run
                }
                foundPosition = findFirstVisibleItemPosition()
                if (foundPosition != RecyclerView.NO_POSITION) {
                    statusListener.onPageChanged(foundPosition, pageTotalCount)
                    return@run
                }
            }
        }
    }
}