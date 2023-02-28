package org.evidyaloka.student.ui.explore.subtopic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.explore.ExploreSubjectTimetableFragment
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.model.Subject
import org.evidyaloka.core.model.Topic
import org.evidyaloka.core.student.model.*
import org.evidyaloka.student.R
import org.evidyaloka.student.ui.ParentExploreActivity
import org.evidyaloka.common.util.SubjectViewUtils
import org.evidyaloka.student.databinding.LayoutParentJoinAlertsBinding

@AndroidEntryPoint
class StudenExploreSubTopicView : ExploreSubjectTimetableFragment() {
    private var subject: Subject? = null
    private var exploreData: ExploreData? = null
    private val viewModel: SubjectDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = StudenExploreSubTopicViewArgs.fromBundle(it)
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
                                    setting.parentContentSettings?.numberOfChapters ?: 0,
                                    setting?.parentContentSettings?.numberOfSubtopicAllowedPerChapter
                                        ?: 0
                                ) { topic: Topic, subtopic: SubTopic, isLocked: Boolean ->
                                    if (isLocked) {
                                        showJoinDialog()
                                    } else {
                                        findNavController().navigate(
                                            StudenExploreSubTopicViewDirections.actionStudenExploreSubTopicViewToStudentExploreRTCView(
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
                                    binding.root.resources.getString(R.string.title_lessons),
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
        try {
            subject?.let {
                activity?.let { act ->
                    val color = SubjectViewUtils.getCourseUISettings(it.subjectName).color()
                    (act as ParentExploreActivity).updateUiStyle(color)
                    (act as ParentExploreActivity).setToolbarTitle(
                        String.format(
                            getString(R.string.title_lessons),
                            it.subjectName
                        )
                    )
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.let { it ->
            (it as ParentExploreActivity).updateUiStyle()
        }
    }

    private fun showJoinDialog() {
        val binding =
            LayoutParentJoinAlertsBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        val alert = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogTheme).setView(binding.root).create()
        binding.btJoin.setOnClickListener {
            alert.dismiss()
            findNavController().navigate(StudenExploreSubTopicViewDirections.actionStudenExploreSubTopicViewToStudentRegistrationFragment(exploreData))
        }
        binding.btClose.setOnClickListener {
            alert.dismiss()
        }
        alert.show()
    }
}