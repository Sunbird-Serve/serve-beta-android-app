package org.evidyaloka.student.ui.explore.rtc


import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.explore.ExploreRtcViewFragment
import org.evidyaloka.common.ui.rtc.CoursePlayerPagerList
import org.evidyaloka.common.ui.rtc.SessionType
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.model.rtc.ContentDetail
import org.evidyaloka.core.partner.model.ExploreData
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.HomeActivity
import org.evidyaloka.partner.ui.explore.rtc.RtcViewModel


@AndroidEntryPoint
class PartnerExploreRTCView : ExploreRtcViewFragment() {
    private val TAG = "RtcViewFragment"
    private val viewModel: RtcViewModel by viewModels()
    private var session: Session? = null
    private var timetableId: Int? = null
    private var subTopic: SubTopic? = null
    private var contentDetailsId: Int? = null
    private var isBackPopView = false
    private var videoUrl: String? = null
    private var currentDuration: Float? = null
    private var contentDetail: ContentDetail? = null
    private var exploreData: ExploreData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = PartnerExploreRTCViewArgs.fromBundle(it)
            session = args.session
            subTopic = args.subTopic
            exploreData = args.exploreData
        }
        (activity as HomeActivity)?.let {
            it.hideToolbar()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCourseDetails()
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvJoin.text = getString(R.string.join_us_to_run_your_own_digital_school_your_username_and_password_are_sent_to_your_registered_email_id)
        binding.btJoin.apply {
            text = getString(R.string.complete_your_registration)
            setOnClickListener {
                findNavController().navigate(
                    PartnerExploreRTCViewDirections.actionPartnerExploreRTCViewToUpdateRegistrationFragment(
                        exploreData
                    )
                )
            }
        }
    }


    private fun getCourseDetails() {
        session?.let {
            viewModel.getSessionDetails(
                null,
                it.offeringId,
                it.topicId,
                null,
                subTopic?.id ?: 0
            )
                .observe(viewLifecycleOwner, Observer {
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let {
                                contentDetail = it
                                binding.CoursePlayerViewPager.setAdapterData(
                                    listOf(
                                        CoursePlayerPagerList(
                                            SessionType.VIDEO,
                                            it.videos
                                        ),
                                        CoursePlayerPagerList(
                                            SessionType.WORKSHEET,
                                            it.worksheets
                                        ),
                                        CoursePlayerPagerList(
                                            SessionType.TEXTBOOK,
                                            it.textbooks
                                        )
                                    ),
                                    false
                                )
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

                                    activity?.let {
                                        it as HomeActivity
                                        it.showSuccessDialog(
                                            getString(R.string.label_sorry),
                                            getString(R.string.could_not_able_to_fetch_data)
                                        ).show()
                                    }
                                }
                            }
                        }
                        is Resource.GenericError -> {
                            it.code?.let {
                                if (it <= 0)
                                    return@Observer
                            }
                            activity?.let {
                                it as HomeActivity
                                it.showSuccessDialog(
                                    getString(R.string.label_sorry),
                                    getString(R.string.could_not_able_to_fetch_data)
                                ).show()
                            }
                        }

                    }
                })
        }

    }

    override fun onStop() {
        super.onStop()
        try {
            (activity as HomeActivity)?.let {
                it.showToolbar()
            }
        } catch (e: Exception) {
        }
    }


}