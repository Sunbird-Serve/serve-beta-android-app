package org.evidyaloka.student.ui.explore.rtc


import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.explore.ExploreRtcViewFragment
import org.evidyaloka.common.ui.rtc.CoursePlayerPagerList
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.ExploreData
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.model.rtc.ContentDetail
import org.evidyaloka.student.R
import org.evidyaloka.common.ui.rtc.CoursePlayerDetailsCardview
import org.evidyaloka.common.ui.rtc.SessionType
import org.evidyaloka.student.ui.ParentExploreActivity
import org.evidyaloka.student.utils.Util


@AndroidEntryPoint
class StudentExploreRTCView : ExploreRtcViewFragment() {
    private val TAG = "RtcViewFragment"
    private val viewModel: RtcViewModel by viewModels()
    private var session: Session? = null
    private var timetableId: Int? = null
    private var subTopic: SubTopic? = null
    private var contentDetailsId: Int? = null
    private var isBackPopView = false
    private var videoUrl: String? = null
    private var currentDuration:Float? = null
    private var contentDetail: ContentDetail? = null
    private var exploreData: ExploreData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as ParentExploreActivity)?.hideToolbar()
        arguments?.let {
            var args = StudentExploreRTCViewArgs.fromBundle(it)
            session = args.session
            timetableId = args.timetableId
            subTopic = args.subTopic
            exploreData = args.exploreData
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCourseDetails()
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btJoin.setOnClickListener {
            findNavController().navigate(StudentExploreRTCViewDirections.actionStudentExploreRTCViewToStudentRegistrationFragment(exploreData))
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as ParentExploreActivity)?.showToolbar()
    }

    private fun getCourseDetails() {
        session?.let {
            viewModel.getSessionDetails(
                null,
                it.offeringId,
                it.topicId,
                null,
                subTopic?.id?:0
            )
                .observe(viewLifecycleOwner, Observer {
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let {
                                contentDetail = it
                                binding.CoursePlayerViewPager.setAdapterData(
                                    listOf(
                                        CoursePlayerPagerList(SessionType.VIDEO,it.videos),
                                        CoursePlayerPagerList(SessionType.WORKSHEET,it.worksheets),
                                        CoursePlayerPagerList(SessionType.TEXTBOOK,it.textbooks)
                                    ),
                                    false
                                )
                                if(it.videos.size > 0) {
                                    if(!isBackPopView && videoUrl == null) {
                                        playPrimaryVideo(it.videos)
                                    }else{
                                        //Do nothing
                                    }
                                }else if(it.worksheets.size > 0) {
                                    binding.CoursePlayerViewPager.setCurrentItem(1, true)
                                }else if(it.textbooks.size > 0) {
                                    binding.CoursePlayerViewPager.setCurrentItem(2, true)
                                }else {

                                    Util.showPopupDialog(requireContext(),getString(R.string.label_sorry), getString(R.string.no_content))?.show()
                                }
                            }
                        }
                        is Resource.GenericError -> {
                            it.code?.let{
                                if(it <= 0)
                                    return@Observer
                            }
                            when(it.error?.code){
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


}