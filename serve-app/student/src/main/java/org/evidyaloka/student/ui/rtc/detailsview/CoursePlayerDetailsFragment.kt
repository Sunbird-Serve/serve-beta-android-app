package org.evidyaloka.student.ui.rtc.detailsview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.ui.rtc.CoursePlayerDetailsAdapter
import org.evidyaloka.common.ui.rtc.SessionType
import org.evidyaloka.common.util.FileReaderActivity
import org.evidyaloka.common.util.PdfReaderActivity
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.CommonConst.GOOGLE_DOC_URL
import org.evidyaloka.core.Constants.CommonConst.URL_STARTS_WITH_DRIVE_URL
import org.evidyaloka.core.Constants.CommonConst.URL_STARTS_WITH_GOOGLE_DOC_URL
import org.evidyaloka.core.Constants.StudentConst.CONTENT_TYPE
import org.evidyaloka.core.Constants.StudentConst.PAGE_TITLE
import org.evidyaloka.core.Constants.StudentConst.WEB_URL
import org.evidyaloka.core.model.rtc.ContentAttributes
import org.evidyaloka.core.model.rtc.ContentDetail
import org.evidyaloka.student.databinding.FragmentSessionDetailsBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.rtc.RtcViewModel

@AndroidEntryPoint
class CoursePlayerDetailsFragment : BaseFragment() {

    private val TAG: String = "SessionDetailsFragment"
    private var pageType =
        PageType.VideoList
    private val viewModel: RtcViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private lateinit var binding: FragmentSessionDetailsBinding
    private var sessionDetailsAdapter =
        CoursePlayerDetailsAdapter(
            SessionType.VIDEO
        )
    private lateinit var lessonDetailItem: ContentDetail

    enum class PageType {
        VideoList,
        Worksheet,
        Activity, Textbook
    }

    companion object {
        private val PAGE_TYPE = "page_type"
        private val DATA = "data"
        private val TIMETABLE_ID = "timetableId"
        private val SUBTOPIC_ID = "subTopicId"
        fun newInstance(
            pageType: PageType,
            data: ContentDetail?
        ): CoursePlayerDetailsFragment {
            val fragmentInstance =
                CoursePlayerDetailsFragment()
            val args = Bundle()
            args.putSerializable(PAGE_TYPE, pageType)
            data?.let { args.putParcelable(DATA, data) }
            fragmentInstance.arguments = args
            return fragmentInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hideToolbar()
        arguments?.let {
            pageType = it.get(PAGE_TYPE) as PageType
            lessonDetailItem = it.get(DATA) as ContentDetail
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSessionDetailsBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (pageType) {
            PageType.VideoList -> initListView(
                lessonDetailItem.videos,
                SessionType.VIDEO
            )
            PageType.Activity -> initListView(
                lessonDetailItem.activities,
                SessionType.ACTIVITY
            )
            PageType.Worksheet -> initListView(
                lessonDetailItem.worksheets,
                SessionType.WORKSHEET
            )
            PageType.Textbook -> initListView(
                lessonDetailItem.textbooks,
                SessionType.TEXTBOOK
            )
        }
        activity?.let {
            binding.rvDetails.apply {
                layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                adapter = sessionDetailsAdapter

            }
        }
    }

    private fun initListView(
        list: List<ContentAttributes>,
        listType: SessionType
    ) {
        if (listType != SessionType.VIDEO)
            if (list.isNullOrEmpty()) showEmptyView() else hideEmptyView()
        sessionDetailsAdapter?.apply {
            type = listType
            setItem(list)
            notifyDataSetChanged()

        }
    }

    private fun showEmptyView() {
        binding.emptyView.visibility = View.VISIBLE
        binding.rvDetails.visibility = View.GONE
    }

    private fun hideEmptyView() {
        binding.emptyView.visibility = View.GONE
        binding.rvDetails.visibility = View.VISIBLE
    }

    private fun openInWebView(contentAttributes: ContentAttributes) {
        var title = contentAttributes.title?.let { it } ?: ""
        var url = contentAttributes.url?.let { it } ?: ""
        var contentType = contentAttributes.contentType?.let { it } ?: ""

        contentAttributes.url?.let {
            if (!it.startsWith(URL_STARTS_WITH_DRIVE_URL) || !it.startsWith(
                    URL_STARTS_WITH_GOOGLE_DOC_URL
                )
            ) {
                if (contentType.equals(CommonConst.MS_DOC) ||
                    contentType.equals(CommonConst.MS_PPT) ||
                    contentType.equals(CommonConst.MS_XLS)
                ) {
                    url = GOOGLE_DOC_URL + it
                }
            }
        }

        var bundle = Bundle()
        bundle.putString(WEB_URL, url)
        bundle.putString(PAGE_TITLE, title)
        bundle.putString(CONTENT_TYPE, contentType)


        var intent: Intent = if (contentType.equals(CommonConst.PDF)) {
            Intent(context, PdfReaderActivity::class.java)
        } else {
            Intent(context, FileReaderActivity::class.java)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }

}