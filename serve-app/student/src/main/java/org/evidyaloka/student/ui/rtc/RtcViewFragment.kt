package org.evidyaloka.student.ui.rtc


import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.evidyaloka.common.helper.PermissionRequester
import org.evidyaloka.common.helper.TakePicturePreview
import org.evidyaloka.common.helper.getMimeType
import org.evidyaloka.common.receiver.InternetConnectionReceiver
import org.evidyaloka.common.ui.rtc.CoursePlayerPagerList
import org.evidyaloka.common.ui.rtc.OnItemClickListner
import org.evidyaloka.common.ui.rtc.SessionType
import org.evidyaloka.common.util.FileReaderActivity
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.model.rtc.ContentAttributes
import org.evidyaloka.core.model.rtc.ContentDetail
import org.evidyaloka.core.student.database.entity.BookmarkEntity
import org.evidyaloka.core.student.database.entity.CourseContentEntity
import org.evidyaloka.core.student.model.School
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.player.FullScreenListener
import org.evidyaloka.player.PlayerError
import org.evidyaloka.player.PlayerListener
import org.evidyaloka.player.PlayerState
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentRtcViewBinding
import org.evidyaloka.student.databinding.LayoutImagePickerDialogBinding
import org.evidyaloka.student.download.ContentDownload
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.utils.Util
import org.evidyaloka.student.worker.ContentWorkerBuilder


@AndroidEntryPoint
class RtcViewFragment : BaseFragment() {
    private val TAG = "RtcViewFragment"
    private val viewModel: RtcViewModel by viewModels()
    private lateinit var binding: FragmentRtcViewBinding
    private var session: Session? = null
    private var timetableId: Int? = null
    private var subTopic: SubTopic? = null
    private var offlineContentDetails: CourseContentEntity? = null
    private var isDeepLink: Boolean = false
    private var contentDetailsId: Int? = null
    private var isBackPopView = false
    private var videoUrl: String? = null
    private var currentDuration: Float = 0F
    private var contentDetail: ContentDetail? = null
    private var isInternetConnected: Boolean = false
    private val writeStoragePermission = PermissionRequester(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        R.string.rationale_read_storage
    )
    private var bookMarked: BookmarkEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = RtcViewFragmentArgs.fromBundle(it)
            session = args.session
            timetableId = args.timetableId
            subTopic = args.subTopic
            offlineContentDetails = args.offlineContent
            isDeepLink = args.isDeepLink
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //hideToolbar()
        binding = FragmentRtcViewBinding.inflate(layoutInflater, container, false)
        if (isDeepLink) {
            offlineContentDetails?.studentId?.let { setOfflineStudent(it) }
        }
        lifecycle.addObserver(binding.adaptivePlayerView)
        binding.adaptivePlayerView.setFullScreenListener(object : FullScreenListener {
            override fun enterFullScreen() {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun exitFullScreen() {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

        })
        binding.adaptivePlayerView.setPlayerEventListner(object : PlayerListener {
            override fun onError(playerState: PlayerError) {
                binding.video.text = when (playerState) {
                    PlayerError.VIDEO_NOT_FOUND -> getString(R.string.video_not_found)
                    PlayerError.VIDEO_NOT_PLAYABLE -> getString(R.string.video_is_not_playable)
                    PlayerError.UNKNOWN -> getString(R.string.unknown_error)
                    PlayerError.INVALID_PARAMETER_IN_REQUEST -> getString(R.string.invalid_video_url)
                    PlayerError.OUT_OF_MEMORY -> getString(R.string.device_memory_full)
                    PlayerError.VIDEO_NOT_PLAYABLE_EMBEDED -> getString(R.string.video_not_allow_to_play_embed)
                    else -> getString(R.string.unknown_error)
                }
                binding.errorView.visibility = View.VISIBLE
            }

            override fun onPlayeBackEvent(playbackState: PlayerState) {
                if (playbackState == PlayerState.ENDED || playbackState == PlayerState.PAUSED) {
                    updateProgress()
                }
                if (playbackState == PlayerState.PLAYING) {
                    if (!isBackPopView && contentDetail?.hasAttended == false) sendAttadance()
                }

            }
        })
//        binding.adaptivePlayerView.setUrl("https://www.youtube.com/watch?v=kRhp_Ydr2v8",0F)
        binding.backButton.setOnClickListener {
            navController.popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        registerNetworkCallback()
        checkBookmark()
        viewModel.getVideoUrl().observe(viewLifecycleOwner, Observer {
            contentDetailsId = it.id
            try {
                it.url?.let { url ->
                    if (!isBackPopView) {
                        videoUrl = url
                        currentDuration = it.progress?.toFloat() ?: 0F
                    }
                    binding.errorView.visibility = View.GONE
                    playVideo(videoUrl!!, it.contentHost!!, currentDuration)
                    isBackPopView = false
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Toast.makeText(context, "Video conetent host issue", Toast.LENGTH_SHORT).show()
            }
        })

        binding.CoursePlayerViewPager.setOnItemClickListner(object : OnItemClickListner {
            override fun OnItemClick(
                type: SessionType,
                contentAttribute: ContentAttributes
            ) {
                if (type == SessionType.VIDEO) {
                    updateProgress()
                    playVideoCoursePlayerItem(contentAttribute)
                } else {
                    openInDocView(contentAttribute)
                }
            }
        })

        binding.CoursePlayerViewPager.setDownloadClickListener(object : OnItemClickListner {
            override fun OnItemClick(
                type: SessionType,
                contentAttribute: ContentAttributes
            ) {
                checkCourseContentLocally(type, timetableId ?: 0, contentAttribute)
            }
        })

        binding.CoursePlayerViewPager.setDeleteClickListener(object : OnItemClickListner {
            override fun OnItemClick(
                type: SessionType,
                contentAttribute: ContentAttributes
            ) {
                deleteOfflineContentAlert(contentAttribute)
            }
        })

        binding.llAskedDoubt.setOnClickListener {
            //Todo open Asked Doubts
            binding.adaptivePlayerView.pause()
            if (!isInternetConnected) {
                return@setOnClickListener internetErrorDialog(getString(R.string.Please_connect_to_internet_to_view_asked_doubts))
            }
            val navigate =
                RtcViewFragmentDirections.actionRtcViewFragmentToAskedDoubtsFragment(
                    session!!,
                    timetableId!!,
                    subTopic!!
                )
            try {
                navController.navigate(navigate)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)

            }
        }

        binding.llLike.setOnClickListener {
            if (!isInternetConnected) {
                return@setOnClickListener internetErrorDialog(getString(R.string.Please_connect_to_internet_to_like_the_video))
            }
            updateSessionRating()
        }
        binding.llDoubt.setOnClickListener {
            binding.adaptivePlayerView.pause()
            if (!isInternetConnected) {
                return@setOnClickListener internetErrorDialog(getString(R.string.Please_connect_to_internet_to_ask_doubt))
            }
            selectImageDialog()
        }
        binding.llBookmark.setOnClickListener {
            if(bookMarked == null) {
                timetableId?.let { it1 -> bookMarkCourseContentLocally(it1) }
            }else{
                bookMarked?.let { it1 -> deleteBookmarkDB(it1) }
            }
        }
        if (offlineContentDetails != null) {
            var contentAttribute = ContentAttributes(
                id = offlineContentDetails?.contentId ?: 0,
                subtopicId = offlineContentDetails?.subtopicId ?: 0,
                url = offlineContentDetails?.url,
                title = offlineContentDetails?.title,
                subtitle = offlineContentDetails?.subtitle,
                localUrl = offlineContentDetails?.localUrl,
                subTopicTitle = offlineContentDetails?.subTopicName,
                contentHost = "s3",
                progress = offlineContentDetails?.progress,
                downloadStatus = offlineContentDetails?.downloadStatus
            )
            playVideoCoursePlayerItem(contentAttribute)
        } else {
            viewModel.getAllDownloadContentDeatils().observe(viewLifecycleOwner, Observer {
                getCourseDetails()
            })
        }
    }

    private fun internetErrorDialog(message: String) {
        Util.showPopupDialog(
            requireContext(),
            title = getString(R.string.no_internet_connection),
            message = message
        )?.let {
            it.show()
        }
    }

    private fun registerNetworkCallback() {
        InternetConnectionReceiver(this@RtcViewFragment) { isConnected ->
            lifecycleScope.launch(Dispatchers.Main) {
                isInternetConnected = isConnected
            }
        }
    }


    private fun getCourseDetails() {
        session?.let {
            viewModel.getSessionDetails(
                it.id,
                it.offeringId,
                it.topicId,
                timetableId!!,
                subTopic!!.id
            )
                .observe(viewLifecycleOwner, Observer {
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let {
                                if (it.hasLiked) {
                                    binding.ivLike.setImageResource(R.drawable.ic_like_selected)
                                    binding.tvLike.setTextColor(resources.getColor(R.color.live_background))
                                    binding.llLike.isActivated = true
                                }
                                contentDetail = it
                                binding.CoursePlayerViewPager.setAdapterData(
                                    listOf(
                                        CoursePlayerPagerList(
                                            SessionType.VIDEO,
                                            it.videos
                                        ),
                                        CoursePlayerPagerList(
                                            SessionType.ACTIVITY,
                                            it.activities
                                        ),
                                        CoursePlayerPagerList(
                                            SessionType.WORKSHEET,
                                            it.worksheets
                                        ),
                                        CoursePlayerPagerList(
                                            SessionType.TEXTBOOK,
                                            it.textbooks
                                        )
                                    )
                                )
                                //Check the data and open page which has data.
                                if (it.videos.size > 0) {
                                    if (!isBackPopView && videoUrl == null) {
                                        playPrimaryVideo(it.videos)
                                    } else {
                                        //Do nothing
                                    }
                                } else if (it.worksheets.size > 0) {
                                    binding.CoursePlayerViewPager.setCurrentItem(1, true)
                                } else if (it.textbooks.size > 0) {
                                    binding.CoursePlayerViewPager.setCurrentItem(2, true)
                                } else {
                                    binding.llDoubt.isClickable = false
                                    binding.llAskedDoubt.isClickable = false
                                    binding.llLike.isClickable = false
                                    binding.llBookmark.isClickable = false
                                    Util.showPopupDialog(
                                        requireContext(),
                                        getString(R.string.label_sorry),
                                        getString(R.string.no_content)
                                    )?.show()
                                }
                            }
                        }
                        is Resource.GenericError -> {
                            it.code?.let {
                                if (it <= 0)
                                    return@Observer
                            }
                            when (it.error?.code) {
                                67 -> {
                                    activity?.let {
                                        Util.showPopupDialog(
                                            it,
                                            it.resources.getString(R.string.label_sorry),
                                            it.resources.getString(R.string.please_attend_previous_class)
                                        )?.show()
                                    }
                                }
                                else -> {
                                    activity?.let {
                                        Util.showPopupDialog(
                                            it,
                                            it.resources.getString(R.string.label_sorry),
                                            it.resources.getString(R.string.could_not_able_to_fetch_data)
                                        )?.show()
                                    }
                                }
                            }
                        }

                    }
                })
        }
    }

    private fun playPrimaryVideo(list: List<ContentAttributes>) {
        var item: ContentAttributes? = null
        list.forEach {
            if (item == null || it.viewStatus == 0) {
                item = it
            }
        }
        item?.let {
            playVideoCoursePlayerItem(it)
        }
    }

    private fun playVideoCoursePlayerItem(item: ContentAttributes) {
        binding.adaptivePlayerView.currentPosition?.let { updateVideoProgress(it.div(1000)) }
        contentDetailsId = item.id
        try {
            (item.localUrl?.takeIf { item.downloadStatus == DownloadManager.STATUS_SUCCESSFUL }
                ?: item.url)?.let { url ->
                if (!isBackPopView) {
                    videoUrl = url
                    currentDuration = item.progress?.toFloat() ?: 0F
                }
                binding.errorView.visibility = View.GONE
                playVideo(videoUrl!!, item.contentHost!!, currentDuration)
                isBackPopView = false
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Toast.makeText(context, "Video conetent host issue", Toast.LENGTH_SHORT).show()
        }
    }

    fun playVideo(uri: String, contentHost: String, startTime: Float) {
        try {
            binding.adaptivePlayerView.setUrl(uri, contentHost, startTime)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openInDocView(contentAttributes: ContentAttributes) {
        var title = contentAttributes.title?.let { it } ?: ""
        var url = contentAttributes.url?.let { it } ?: ""
        var contentType = contentAttributes.contentType?.let { it } ?: ""

        contentAttributes.url?.let {
            if (!it.startsWith(CommonConst.URL_STARTS_WITH_DRIVE_URL) || !it.startsWith(
                    CommonConst.URL_STARTS_WITH_GOOGLE_DOC_URL
                )
            ) {
                if (contentType.equals(CommonConst.MS_DOC) ||
                    contentType.equals(CommonConst.MS_PPT) ||
                    contentType.equals(CommonConst.MS_XLS)
                ) {
                    url = CommonConst.GOOGLE_DOC_URL + it
                }
            }
        }

        var bundle = Bundle()
        bundle.putString(StudentConst.WEB_URL, url)
        contentAttributes.localUrl?.takeIf { contentAttributes.downloadStatus == DownloadManager.STATUS_SUCCESSFUL }
            ?.let {
                bundle.putString(StudentConst.LOCAL_WEB_URL, it)
            }
        bundle.putString(StudentConst.PAGE_TITLE, title)
        bundle.putString(StudentConst.CONTENT_TYPE, contentType)
        bundle.putParcelable(StudentConst.ACTIVITY_CONTENT, contentAttributes)


        var intent: Intent = if (contentType.equals(CommonConst.PDF)) {
            Intent(context, PDFreaderImp::class.java)
        } else {
            Intent(context, FileReaderActivity::class.java)
        }
        intent.putExtras(bundle)
//        startActivity(intent)
        resultLauncher.launch(intent)
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
    }

    override fun onPause() {
        Log.e("RtcFragment", "onPause")
        updateProgress()
        super.onPause()

    }

    override fun onStop() {
        Log.e("RtcFragment", "onStop")
        super.onStop()
        showToolbar()
    }

    private fun updateProgress() {
        try {
            if (!isBackPopView) isBackPopView = true
            binding.adaptivePlayerView.pause()
            currentDuration = binding.adaptivePlayerView.currentPosition?.toFloat()?.div(1000)?:0F
            currentDuration?.let { updateVideoProgress(it.toLong()) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun sendAttadance() {
        try {
            val data = HashMap<String, Any?>()
            data[StudentConst.SESSION_ID] = session?.id
            data[StudentConst.SUBTOPIC_ID] = subTopic?.id
            data[StudentConst.CLASS_TYPE] = StudentConst.ClassType.VOD.value.toString()
            data[StudentConst.CONTENT_DETAILS_ID] = contentDetailsId
            viewModel.sendAttendance(data).observe(
                viewLifecycleOwner,
                Observer {
                    when (it) {
                        is Resource.Success -> {
                            contentDetail?.hasAttended = true
                        }
                        is Resource.GenericError -> {
                            Toast.makeText(
                                context,
                                resources.getString(R.string.attendance_submit_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun updateSessionRating() {

        try {
            val data = HashMap<String, Any?>()
            data[StudentConst.SESSION_ID] = session?.id
            data[StudentConst.SUBTOPIC_ID] = subTopic?.id
            data[StudentConst.HASLIKED] = if (binding.llLike.isActivated) 0 else 1
            viewModel.updateSessionRating(data).observe(
                viewLifecycleOwner,
                Observer {
                    when (it) {
                        is Resource.Success -> {
                            if (binding.llLike.isActivated) {
                                binding.ivLike.setImageResource(R.drawable.ic_like_unselected)
                                binding.tvLike.setTextColor(resources.getColor(R.color.radio_unselected_text_color))
                                binding.llLike.isActivated = false
                            } else {
                                binding.ivLike.setImageResource(R.drawable.ic_like_selected)
                                binding.tvLike.setTextColor(resources.getColor(R.color.live_background))
                                binding.llLike.isActivated = true
                            }
                        }
                        is Resource.GenericError -> {
                            it.code?.let {
                                if (it <= 0)
                                    return@Observer
                            }
                            Toast.makeText(
                                context,
                                resources.getString(R.string.rating_submit_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun openGalleryForResult() {
        openGallery.launch("image/*")
    }

    private fun openCameraForResult() {
        takePicturePreview.launch(null)
    }

    private fun selectImageDialog() {
        this.context?.let {
            var alertDialog: AlertDialog? = null
            val builder: MaterialAlertDialogBuilder =
                MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialogTheme)

            val alertBinding = LayoutImagePickerDialogBinding.inflate(layoutInflater, null, false)
            with(alertBinding) {
                btCamera.setOnClickListener {
                    alertDialog?.dismiss()
                    cameraPermission.runWithPermission {
                        openCameraForResult()
                    }
                }
                btGallery.setOnClickListener {
                    alertDialog?.dismiss()
                    storagePermission.runWithPermission {
                        openGalleryForResult()
                    }
                }
            }
            alertDialog = builder.setView(alertBinding.root).create()
            alertDialog.show()

        }
    }

    private fun deleteOfflineContentAlert(contentAttribute: ContentAttributes) {
        val builder: MaterialAlertDialogBuilder =
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.Base_ThemeOverlay_MaterialComponents_Dialog_Alert
            )
        builder.setTitle(getString(R.string.delete) + "?")
        builder.setMessage(
            String.format(
                requireContext().getString(R.string.delete_offline_content_alert),
                contentAttribute.title
            )
        )
        builder.setPositiveButton(getText(R.string.yes)) { dialog, which ->
            deleteDownloadedContent(contentAttribute)
            dialog.dismiss()
        }
        builder.setNegativeButton(getText(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private val storagePermission = PermissionRequester(
        this@RtcViewFragment,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        R.string.rationale_storage_doubt
    )

    private val cameraPermission = PermissionRequester(
        this@RtcViewFragment,
        Manifest.permission.CAMERA,
        R.string.rationale_camera_storage_doubt
    )

    private val takePicturePreview = registerForActivityResult(TakePicturePreview()) {
        try {
            if (it == null) {
                showSnackBar(getString(R.string.file_does_not_exist))
            } else {
                NavigateToDoubtView(it)
            }
        } catch (e: Exception) {
        }
    }

    private val openGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try {
            if (it == null) {
                showSnackBar(getString(R.string.file_does_not_exist))
            } else {
                NavigateToDoubtView(it)
            }
        } catch (e: Exception) {
        }
    }

    private fun NavigateToDoubtView(uri: Uri) {
        try {
            val navigate = RtcViewFragmentDirections.actionRtcViewFragmentToDoubtsFragment(
                uri,
                session!!,
                timetableId!!,
                subTopic!!
            )
            navController.navigate(navigate)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun updateVideoProgress(progress: Long) {
        try {
            if (isInternetConnected) {
                val data = HashMap<String, Any?>()
                session?.id?.let { data[StudentConst.SESSION_ID] = it }
                contentDetailsId?.let { data[StudentConst.CONTENT_DETAILS_ID] = it }
                subTopic?.id?.let { data[StudentConst.SUBTOPIC_ID] = it }
                progress.let { data[StudentConst.PROGRESS] = it }
                viewModel.updateViewStatus(data).observe(viewLifecycleOwner, Observer {

                })
            } else {
                contentDetailsId?.let {
                    viewModel.updateOfflineProgress(it.toLong(), progress)
                        .observe(viewLifecycleOwner, Observer {
                        })
                    val data = ContentWorkerBuilder.createInputWorkData(
                        session?.id ?: 0,
                        it,
                        subTopic?.id ?: 0,
                        progress
                    )
                    ContentWorkerBuilder.createOneTimeWorkRequest(
                        requireContext().applicationContext,
                        data
                    )
                }
            }
        } catch (e: Exception) {
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                result.data?.extras?.getParcelable<ContentAttributes>(StudentConst.ACTIVITY_CONTENT)
                    ?.let { courseItem ->
                        contentDetail?.worksheets?.find { it.id == courseItem.id }?.viewStatus =
                            1

                        try {
                            val data = HashMap<String, Any?>()
                            session?.id?.let { data[StudentConst.SESSION_ID] = it }
                            courseItem.let { data[StudentConst.CONTENT_DETAILS_ID] = it.id }
                            subTopic?.id?.let { data[StudentConst.SUBTOPIC_ID] = it }
                            data[StudentConst.PROGRESS] = 100
                            viewModel.updateViewStatus(data).observe(viewLifecycleOwner, Observer {
                                if (it is Resource.Success) {
                                    contentDetail?.let {
                                        binding.CoursePlayerViewPager.setAdapterData(
                                            listOf(
                                                CoursePlayerPagerList(
                                                    SessionType.VIDEO,
                                                    it.videos
                                                ),
                                                CoursePlayerPagerList(
                                                    SessionType.ACTIVITY,
                                                    it.activities
                                                ),
                                                CoursePlayerPagerList(
                                                    SessionType.WORKSHEET,
                                                    it.worksheets
                                                ),
                                                CoursePlayerPagerList(
                                                    SessionType.TEXTBOOK,
                                                    it.textbooks
                                                )
                                            )
                                        )
                                    }
                                }
                            })
                        } catch (e: Exception) {
                        }
                    }
            }
        }

    fun checkCourseContentLocally(
        type: SessionType,
        timetableId: Int,
        contentAttribute: ContentAttributes
    ) {
        viewModel.getDownloadContentDetailsByContentId(contentAttribute.id.toLong())
            .observe(viewLifecycleOwner, Observer {
                if (it == null) {
                    downloadCourseContentLocally(type, timetableId, contentAttribute)
                } else if (it.contentId == contentAttribute.id) {
                    when (it.downloadStatus) {
                        DownloadManager.STATUS_SUCCESSFUL -> {

                        }
                        DownloadManager.STATUS_PAUSED,
                        DownloadManager.STATUS_PENDING,
                        DownloadManager.STATUS_RUNNING -> {
                            checkAndUpdateDownloadStatus(it.id)
                        }
                        DownloadManager.STATUS_FAILED -> {
                            downloadCourseContentLocally(type, timetableId, contentAttribute)
                        }
                    }
                }
            })
    }

    fun downloadCourseContentLocally(
        type: SessionType,
        timetableId: Int,
        contentAttribute: ContentAttributes
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            ContentDownload(requireContext()).apply {
                download(contentAttribute,
                    onSuccess = { id, uri ->
                        lifecycleScope.launch(Dispatchers.Main) {
                            insertDownloadedDetailsDB(
                                CourseContentEntity(
                                    id = id,
                                    contentId = contentAttribute.id,
                                    subtopicId = subTopic?.id,
                                    topicId = session?.topicId,
                                    timetableId = timetableId,
                                    topicName = session?.topicName,
                                    subTopicName = subTopic?.subtopicName,
                                    offeringId = session?.offeringId,
                                    sessionId = session?.id,
                                    contentType = contentAttribute.contentType,
                                    type = type.toString(),
                                    downloadStatus = null,
                                    duration = contentAttribute.duration,
                                    localUrl = uri.toString(),
                                    subtitle = contentAttribute.subtitle,
                                    title = contentAttribute.title,
                                    url = contentAttribute.url,
                                    mimeType = contentAttribute.url?.getMimeType()
                                )
                            )
                            updateContentDownloadStatus(
                                contentAttribute,
                                StudentConst.DownloadStatus.InProgress
                            )
                        }
                    },
                    onFailed = { error ->
                        val message = when (error) {
                            INSUFFICIENT_SPACE -> {
                                String.format(
                                    getString(R.string.not_enough_space),
                                    contentAttribute.title
                                )
                            }
                            FAILED_TO_DOWNLOAD -> {
                                String.format(
                                    getString(R.string.unable_to_download_file),
                                    contentAttribute.title
                                )
                            }
                            NOT_ABLE_ACCESS_FILE_LOCATION -> {
                                getString(R.string.do_not_have_access)
                            }
                            else -> {
                                getString(R.string.unknown_error)
                            }
                        }
                        lifecycleScope.launch(Dispatchers.Main) {
                            Util.showPopupDialog(
                                requireContext(),
                                getString(R.string.warning),
                                message
                            )?.show()
                        }
                    }
                )
            }
        }
    }

    fun setOfflineStudent(studentId: Int) {
        studentId?.let { id ->
            viewModel.getStudentById(id).observe(viewLifecycleOwner,
                Observer { student ->
                    viewModel.setSelectedStudent(
                        Student(
                            id = student.id ?: 0,
                            name = student.name ?: "",
                            grade = student.grade ?: "",
                            schools = School(
                                id = student.schoolId,
                                name = student.schoolName,
                                logoUrl = student.logoUrl
                            )
                        )
                    ).observe(viewLifecycleOwner, Observer { })
                })
        }
    }

    fun checkAndUpdateDownloadStatus(id: Long) {
        val details =
            ContentDownload(requireContext()).checkDownloadStatus(id)
        if (details?.status == DownloadManager.STATUS_SUCCESSFUL) {
            updateDownloadedContentDetails(
                id,
                DownloadManager.STATUS_SUCCESSFUL
            )
        }
    }

    fun insertDownloadedDetailsDB(courseContentEntity: CourseContentEntity) {
        viewModel.insertContentDetail(courseContentEntity).observe(viewLifecycleOwner, Observer {

        })
    }

    fun updateDownloadedContentDetails(id: Long, status: Int?, reason: String? = "") {
        viewModel.updateDownloadContentDetails(id, status, reason)
            .observe(viewLifecycleOwner, Observer {

            })
    }

    fun deleteDownloadedContent(contentAttribute: ContentAttributes) {
        try {
            viewModel.deleteByContentId(contentAttribute.id.toLong())
                .observe(viewLifecycleOwner, Observer {
                    it?.takeIf { it > 0 }?.let {
                        contentAttribute.localUrl?.let { it1 ->
                            ContentDownload(requireContext()).deleteFile(
                                it1
                            )
                        }
                        updateContentDownloadStatus(
                            contentAttribute,
                            StudentConst.DownloadStatus.Removed
                        )
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateContentDownloadStatus(
        contentAttribute: ContentAttributes,
        status: StudentConst.DownloadStatus
    ) {
        try {
            val data = HashMap<String, Any?>()
            data[StudentConst.CONTENT_DETAILS_ID] = contentAttribute?.id
            data[StudentConst.SESSION_ID] = session?.id
            data[StudentConst.SUBTOPIC_ID] = subTopic?.id
            data[StudentConst.DEVICE_ID] = Utils.getDeviceId(requireContext())
            data[StudentConst.DOWNLOAD_STATUS] = status.value
            viewModel.updateContentDownloadStatus(data).observe(viewLifecycleOwner, Observer {

            })
        } catch (e: Exception) {

        }
    }


    //Bookmark
    private fun insertBookmarkDB(bookmarkEntity: BookmarkEntity) {
        viewModel.insertBookmark(bookmarkEntity).observe(viewLifecycleOwner, Observer {
            bookMarked = bookmarkEntity
            binding.tvBookmark.setTextColor(resources.getColor(R.color.class_scheduled_color))
            binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_selected)
        })
    }

    private fun deleteBookmarkDB(bookmarkEntity: BookmarkEntity) {
        viewModel.deleteBookmark(bookmarkEntity).observe(viewLifecycleOwner, Observer {
            binding.tvBookmark.setTextColor(resources.getColor(R.color.text_color_rtc_doubt))
            binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_unselected)
            bookMarked = null
        })
    }

    private fun bookMarkCourseContentLocally(
        timetableId: Int
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            insertBookmarkDB(
                BookmarkEntity(
                    subtopicId = subTopic?.id,
                    topicId = session?.topicId,
                    timetableId = timetableId,
                    topicName = session?.topicName,
                    subTopicName = subTopic?.subtopicName,
                    offeringId = session?.offeringId,
                    sessionId = session?.id
                )
            )
        }
    }

    private fun checkBookmark() {
        subTopic?.id?.let {
            viewModel.getBookmarkBySubtopicId(it).observe(viewLifecycleOwner, Observer {
                it?.let {
                    bookMarked = it
                    binding.tvBookmark.setTextColor(resources.getColor(R.color.class_scheduled_color))
                    binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_selected)
                }
            })
        }
    }

}