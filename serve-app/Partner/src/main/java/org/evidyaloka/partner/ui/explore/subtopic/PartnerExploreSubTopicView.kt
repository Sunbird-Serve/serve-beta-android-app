package org.evidyaloka.partner.ui.explore.subtopic

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.explore.ExploreSubjectTimetableFragment
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.model.Subject
import org.evidyaloka.core.model.Topic
import org.evidyaloka.core.partner.model.ExploreData
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.LayoutPartnerJoinAlertBinding
import org.evidyaloka.partner.ui.HomeActivity

@AndroidEntryPoint
class PartnerExploreSubTopicView : ExploreSubjectTimetableFragment() {
    private var subject: Subject? = null
    private var exploreData: ExploreData? = null
    private val viewModel: SubjectDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = PartnerExploreSubTopicViewArgs.fromBundle(it)
            subject = args.subject
            exploreData = args.exploreData
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subject?.let {
            viewModel.getSubjectDetails(it.offeringId, it.courseId)
                .observe(viewLifecycleOwner, Observer { listResponse ->
                    if (listResponse is Resource.Success) {
                        listResponse.data?.let { list ->
                            getParentContentSettings(list)
                        }
                    }
                })
        }
    }

    private fun getParentContentSettings(list: List<Topic>) {
        viewModel.ping().observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Success -> {
                    it.data?.let { setting ->
                        subject?.let {
                            binding.subtopicView.apply {
                                setSubject(
                                    it.subjectName,
                                    list,
                                    true,
                                    setting.partnerContentSettings?.numberOfChapters?.toInt() ?: 0,
                                    setting?.partnerContentSettings?.numberOfSubtopicAllowedPerChapter?.toInt()
                                        ?: 0
                                ) { topic: Topic, subtopic: SubTopic, isLocked: Boolean ->
                                    if (isLocked) {
                                        showJoinDialog()
                                    } else {
                                        findNavController().navigate(
                                            PartnerExploreSubTopicViewDirections.actionPartnerExploreSubTopicViewToPartnerExploreRTCView(
                                                Session(
                                                    offeringId = it.offeringId,
                                                    topicId = topic.id
                                                ),
                                                subTopic = subtopic,
                                                exploreData = exploreData
                                            )
                                        )
                                    }
                                }
                                setViewBackground(R.drawable.bg_bottom_sheet_curved)
                            }

                            binding.subtopicView.setLessonCount(
                                String.format(
                                    getString(R.string.title_lessons),
                                    list.size
                                )
                            )
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        subject?.let {
            activity?.let { act ->
                (act as HomeActivity).updateToolbarTitle(
                    String.format(
                        getString(R.string.title_lessons),
                        it.subjectName
                    )
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            activity?.let { act ->
                (act as HomeActivity).updateToolbarTitle("")
            }
        } catch (e: Exception) {
        }
    }

    private fun showJoinDialog() {
        var binding =
            LayoutPartnerJoinAlertBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        val alert = MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialogTheme).setView(binding.root).create()
        binding.btJoin.setOnClickListener {
            alert.dismiss()
            findNavController().navigate(PartnerExploreSubTopicViewDirections.actionPartnerExploreSubTopicViewToUpdateRegistrationFragment(exploreData))
        }
        binding.btClose.setOnClickListener {
            alert.dismiss()
        }
        alert.show()
    }
}