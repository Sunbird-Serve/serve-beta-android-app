package org.evidyaloka.common.ui.rtc

import android.app.DownloadManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.evidyaloka.common.R
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.model.rtc.ContentAttributes


class CoursePlayerDetailsAdapter(var type: SessionType) :
    RecyclerView.Adapter<CoursePlayerDetailsAdapter.sessionHolder>() {

    private val TAG = "ActivityAdapter"
    private var onItemClickListener: OnItemClickListner? = null
    private var downloadClick: OnItemClickListner? = null
    private var deleteClick: OnItemClickListner? = null
    private var isDownloadOptionShown: Boolean = false

    init {
        this.type = type
    }

    private var contentAttributes: List<ContentAttributes> = listOf()

    inner class sessionHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(contentAttr: ContentAttributes, pos: Int) {

            when (type) {
                SessionType.VIDEO -> {
                    var title = contentAttr.title ?: contentAttr.subTopicTitle ?: ""

                    (view as CoursePlayerDetailsCardview).setTitle(title)

                    var progression = ""
                    contentAttr.duration?.let { progression = "" + Utils.getDurationInHHmmss(it) }
                    contentAttr.progress?.let {
                        progression = progression.plus(" / ").plus(Utils.getDurationInHHmmss(it))
                    }
                    view.setProgression(progression)
                    if (contentAttr.duration != null) {
                        view.setProgressBar(contentAttr.progress ?: 0, contentAttr.duration!!)
                    }
                    view.setThumbnail(contentAttr.thumbnailUrl)

                }
                SessionType.WORKSHEET, SessionType.ACTIVITY -> {

                    var title = contentAttr.title?.let { it } ?: ""
                    var subTitle = contentAttr.description?.let { it }

                    (view as CoursePlayerDetailsCardview).apply {
                        setTitle(title, subTitle)
                        setIcon(position)
                        setViewButtonText(contentAttr.viewStatus)
                    }
                }
                SessionType.TEXTBOOK -> {
//                    contentAttr.url?.let {
//                        (view as CoursePlayerDetailsCardview).setTexbook(it)
//                    } ?: (view as CoursePlayerDetailsCardview).setTexbook("")
                }
            }

            (view as CoursePlayerDetailsCardview).setBackground(contentAttr.viewStatus)

            view.setClickListener(object :
                CoursePlayerDetailsCardview.OnClickListener {
                override fun onClick(v: CoursePlayerDetailsCardview) {

                    if (type != SessionType.WORKSHEET || pos == 0 || (pos > 0 && contentAttributes[pos - 1].viewStatus != null)) {
                        onItemClickListener?.OnItemClick(type, contentAttr)
                    } else {
                        var snackbar = Snackbar.make(
                            view,
                            view.context.getString(R.string.remember_to_solve_the_worksheet),
                            Snackbar.LENGTH_LONG
                        )
                        val view: View = snackbar.view
                        val params = view.layoutParams as CoordinatorLayout.LayoutParams
                        params.gravity = Gravity.CENTER
                        view.layoutParams = params
                        snackbar.show()
                    }
                }
            })

            showDownloadOption(view, type, contentAttr)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): sessionHolder {
        var sessionView =
            CoursePlayerDetailsCardview(
                parent.context,
                type
            )
        return sessionHolder(sessionView)
    }

    override fun getItemCount(): Int {
        return contentAttributes.size
    }

    override fun onBindViewHolder(holder: sessionHolder, position: Int) {
        contentAttributes[position]?.let {
            holder.bind(it, position)
        }
    }

    fun setItem(list: List<ContentAttributes>, isDownloadOptionShown:Boolean = false) {
        contentAttributes = list
        this.isDownloadOptionShown = isDownloadOptionShown
        notifyDataSetChanged()
    }


    fun setOnItemClickListener(listner: OnItemClickListner?) {
        onItemClickListener = listner
    }

    fun setDownloadClickListener(listener: OnItemClickListner) {
        downloadClick = listener
    }

    fun setDeleteClickListener(listener: OnItemClickListner) {
        deleteClick = listener
    }

    private fun showDownloadOption(
        view: CoursePlayerDetailsCardview,
        type: SessionType,
        contentAttr: ContentAttributes
    ) {
        if (type == SessionType.WORKSHEET || type == SessionType.VIDEO) {
            if (contentAttr.localUrl == null && contentAttr.contentHost == "s3" && contentAttr.url?.startsWith(
                    "https",
                    true
                ) == true && (contentAttr.contentType == "video-standard" || contentAttr.contentType == "pdf") && isDownloadOptionShown
            ) {
                view.setDownloadClickListener(object : CoursePlayerDetailsCardview.OnClickListener {
                    override fun onClick(v: CoursePlayerDetailsCardview) {
                        downloadClick?.OnItemClick(type, contentAttr)
                    }
                })
            } else if (!contentAttr.localUrl.isNullOrEmpty()) {
                if (contentAttr.downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    view.setDownloadedImage()
                    view.setDeleteClickListener(object :
                        CoursePlayerDetailsCardview.OnClickListener {
                        override fun onClick(v: CoursePlayerDetailsCardview) {
                            deleteClick?.OnItemClick(type, contentAttr)
                        }
                    })
                } else {
                    view.setDownloadingAnimation()
                }
            }
        }
    }

}