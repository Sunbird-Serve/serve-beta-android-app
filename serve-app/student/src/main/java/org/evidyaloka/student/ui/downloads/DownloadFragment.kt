package org.evidyaloka.student.ui.downloads

import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavDeepLinkBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.download_fragment.*
import org.evidyaloka.common.ui.rtc.SessionType
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.model.rtc.ContentAttributes
import org.evidyaloka.core.student.database.entity.CourseContentEntity
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.DownloadFragmentBinding
import org.evidyaloka.student.download.ContentDownload
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.ParentExploreActivity
import org.evidyaloka.student.ui.StudentHomeActivity
import org.evidyaloka.student.ui.rtc.PDFreaderImp
import org.evidyaloka.student.ui.rtc.RtcViewFragmentArgs


@AndroidEntryPoint
class DownloadFragment : BaseFragment() {

    private val viewModel: DownloadViewModel by viewModels()
    private lateinit var binding: DownloadFragmentBinding
    private val downloadAdapter = DownloadAdapter()
    private var studentContent: List<CourseContentEntity>? = null
    private var isStudentView: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val arg = DownloadFragmentArgs.fromBundle(it)
            isStudentView = arg.isStudentView
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DownloadFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.rvDownloads.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = downloadAdapter
        }
        if (isStudentView) {
            getSelectedStudentContent()
        } else {
            setStudentsSpinner()
        }
        setContentTypeSpinner()
        //        getDownloadedContents()

        downloadAdapter.setClickListener(object : OnItemClickListner {
            override fun OnItemClick(content: CourseContentEntity) {
                if (content.downloadStatus == DownloadManager.STATUS_SUCCESSFUL)
                    if (content.type == SessionType.VIDEO.toString()) {
                        if (activity is ParentExploreActivity) {
                            buildPendingIntent(
                                requireContext(),
                                R.id.rtcnavigation,
                                buildRtfcArgs(content).toBundle()
                            )?.send()
                        } else {
                            navController.navigate(
                                R.id.rtcnavigation,
                                buildRtfcArgs(content).toBundle()
                            )
                        }
                    } else {
                        val contentAttributes = ContentAttributes(
                            id = content?.contentId ?: 0,
                            subtopicId = content?.subtopicId ?: 0,
                            url = content?.url,
                            title = content?.title,
                            subtitle = content?.subtitle,
                            localUrl = content?.localUrl,
                            subTopicTitle = content?.subTopicName,
                            contentHost = "s3",
                            downloadStatus = content?.downloadStatus
                        )
                        if (activity is ParentExploreActivity) {
                            buildPendingIntent(
                                requireContext(),
                                R.id.pdfReaderActivity,
                                buildDocViewBundle(
                                    contentAttributes,
                                    content
                                )
                            )?.send()
                        } else {
                            openInPDFView(
                                contentAttributes,
                                content
                            )
                        }

                    }
            }
        })

        downloadAdapter.setDeleteClickListener(object : OnItemClickListner {
            override fun OnItemClick(content: CourseContentEntity) {
                deleteOfflineContentAlert(content)
            }
        })
    }

    private fun buildPendingIntent(
        context: Context,
        destId: Int,
        args: Bundle
    ): PendingIntent? {
        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(destId)
            .setComponentName(StudentHomeActivity::class.java)
            .setArguments(args)
            .createPendingIntent()
    }

    private fun buildRtfcArgs(content: CourseContentEntity): RtcViewFragmentArgs {
        with(content) {
            return RtcViewFragmentArgs(
                Session(
                    id = sessionId ?: 0,
                    topicId = topicId ?: 0,
                    topicName = topicName ?: "",
                    offeringId = offeringId ?: 0,
                    videoLink = "file:///data/user/0/org.evidyaloka/files/477"
                ),
                timetableId = timetableId ?: 0,
                subTopic = SubTopic(subtopicId ?: 0, subTopicName ?: ""),
                offlineContent = content,
                isDeepLink = true
            )
        }
    }

    private fun buildDocViewBundle(
        contentAttributes: ContentAttributes,
        content: CourseContentEntity
    ): Bundle {
        var title = contentAttributes.title?.let { it } ?: ""
        var url = contentAttributes.url?.let { it } ?: ""
        var contentType = contentAttributes.contentType?.let { it } ?: ""
        var bundle = Bundle()
        bundle.putString(StudentConst.WEB_URL, url)
        contentAttributes?.localUrl?.let {
            bundle.putString(StudentConst.LOCAL_WEB_URL, it)
        }
        bundle.putString(StudentConst.PAGE_TITLE, title)
        bundle.putString(StudentConst.CONTENT_TYPE, contentType)
        bundle.putParcelable(StudentConst.ACTIVITY_CONTENT, contentAttributes)
        bundle.putParcelable(StudentConst.OFFLINE_CONTENT, content)
        bundle.putBoolean(StudentConst.IS_DEEP_LINK, true)
        var intent: Intent = Intent(context, PDFreaderImp::class.java)
        intent.putExtras(bundle)
        return bundle
//        startActivity(intent)
//        resultLauncher.launch(intent)
    }

    private fun openInPDFView(contentAttributes: ContentAttributes, content: CourseContentEntity) {
        var intent: Intent = Intent(context, PDFreaderImp::class.java)
        intent.putExtras(buildDocViewBundle(contentAttributes, content))
        startActivity(intent)
//        resultLauncher.launch(intent)
    }


    private fun getSelectedStudentContent() {
        til_student.visibility = View.GONE
        tv_select_student.visibility = View.GONE
        viewModel.getSelectedStudentContent().observe(viewLifecycleOwner, Observer {
            studentContent = it
            studentContent?.let { downloadAdapter.setItems(it) }
        })
    }

    private fun setStudentsSpinner() {
        viewModel.getAllStudents().observe(viewLifecycleOwner, Observer {
            val studentsAdapter =
                ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_list_item,
                    it.map { it.name })
            it?.takeIf { it.size > 0 }?.get(0)?.let {
                it.id?.let { id -> getDownloadedContentsByStudentId(id) }
                sp_student.setText(it?.name, false)
            }
            studentsAdapter.setDropDownViewResource(R.layout.spinner_list_item)
            sp_student.apply {
                setAdapter(studentsAdapter)
                setOnItemClickListener { parent, view, position, id ->
                    it[position].id?.let { _id ->
                        sp_content_type.setText(getString(R.string.both), false)
                        getDownloadedContentsByStudentId(_id)
                    }
                }
            }
        })
    }

    private fun setContentTypeSpinner() {
        val types = arrayOf(
            getString(R.string.label_video_lessons),
            getString(R.string.label_worksheet),
            getString(R.string.both)
        )
        val contentAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_list_item, types)
        contentAdapter.setDropDownViewResource(R.layout.spinner_list_item)
        sp_content_type.apply {
            setAdapter(contentAdapter)
            setOnItemClickListener { parent, view, position, id ->

                studentContent?.let {
                    val content = it.filter {
                        when (position) {
                            0 -> it.type == SessionType.VIDEO.toString()
                            1 -> it.type == SessionType.WORKSHEET.toString()
                            else -> true
                        }
                    }
                    downloadAdapter.setItems(content)
                }
            }
            setText(getString(R.string.both), false)
        }
    }

    private fun getDownloadedContentsByStudentId(studentId: Int) {
        viewModel.getContentByStudentId(studentId)
            .observe(viewLifecycleOwner, Observer { content ->
                studentContent = content
                studentContent?.let { downloadAdapter.setItems(it) }
            })
    }

    private fun getDownloadedContentsByStudentId() {
        viewModel.getAllDownloadContentDeatils().observe(viewLifecycleOwner, Observer { list ->
            list?.let { downloadAdapter.setItems(it) }
        })
    }

    private fun deleteOfflineContentAlert(content: CourseContentEntity) {
        val builder: MaterialAlertDialogBuilder =
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.Base_ThemeOverlay_MaterialComponents_Dialog_Alert
            )
        builder.setTitle(getString(R.string.delete) + "?")
        builder.setMessage(
            String.format(
                requireContext().getString(R.string.delete_offline_content_alert),
                content.title
            )
        )
        builder.setPositiveButton(getText(R.string.yes)) { dialog, which ->
            deleteDownloadedContent(content)
            dialog.dismiss()
        }
        builder.setNegativeButton(getText(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    fun deleteDownloadedContent(content: CourseContentEntity) {
        try {
            viewModel.deleteByContentId(content.contentId?.toLong() ?: 0)
                .observe(viewLifecycleOwner, Observer {
                    it?.takeIf { it > 0 }?.let {
                        content.localUrl?.let { url ->
                            ContentDownload(requireContext()).deleteFile(
                                url
                            )
                        }
                        updateContentDownloadStatus(
                            content,
                            StudentConst.DownloadStatus.Removed
                        )
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateContentDownloadStatus(
        content: CourseContentEntity,
        status: StudentConst.DownloadStatus
    ) {
        try {
            val data = HashMap<String, Any?>()
            data[StudentConst.CONTENT_DETAILS_ID] = content.id
            data[StudentConst.SESSION_ID] = content.sessionId
            data[StudentConst.SUBTOPIC_ID] = content.subtopicId
            data[StudentConst.DEVICE_ID] = Utils.getDeviceId(requireContext())
            data[StudentConst.DOWNLOAD_STATUS] = status.value
            viewModel.updateContentDownloadStatus(data).observe(viewLifecycleOwner, Observer {

            })
        } catch (e: Exception) {

        }
    }


}