package org.evidyaloka.partner.ui.explore.digitalschool

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.explore.ExploreDigitalSchoolFragment
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Course
import org.evidyaloka.core.model.Subject
import org.evidyaloka.core.partner.model.ExploreData
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.HomeActivity

@AndroidEntryPoint
class PartnerExploreDigitalSchoolView : ExploreDigitalSchoolFragment() {

    private val viewModel: DigitalSchoolViewModel by viewModels()
    private var exploreData: ExploreData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = PartnerExploreDigitalSchoolViewArgs.fromBundle(it)
            exploreData = args.exploreData
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        exploreData?.grade?.let { binding.boardGrade.setSelectedGrade(it) }
        getGrade()
        getSchooldDetails()
        exploreData?.let {
            getDigitalSchoolData(it.grade, it.courseProviderId)
        }

        binding.btJoin.apply {
            text = getString(R.string.complete_your_registration)
            setOnClickListener {
                findNavController().navigate(
                    PartnerExploreDigitalSchoolViewDirections.actionPartnerExploreDigitalSchoolViewToUpdateRegistrationFragment(
                        exploreData
                    )
                )
            }
        }
        binding.tvJoinText.visibility = View.VISIBLE
    }

    fun getSchooldDetails() {
        viewModel.getCourseProviders().observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            binding.boardGrade?.apply {
                                it.forEach {
                                    if (it.id == exploreData?.courseProviderId)
                                        binding.boardGrade.setSelectedBoard(it)
                                }
                                setBoard(it) {
                                    binding.boardGrade.setSelectedGrade(null)
                                    binding.digitalSchool.setCoursesAdapter(listOf<Course>(),{})
                                    exploreData?.courseProviderId = it.id
                                    exploreData?.let {
                                        getDigitalSchoolData(it.grade, it.courseProviderId)
                                    }
                                }
                            }
                        }
                    }

                    is Resource.GenericError -> {
                        it.code?.let {
                            if (it <= 0)
                                return@Observer
                            else {
                                (activity as HomeActivity)?.showSuccessDialog(
                                    title = getString(R.string.error),
                                    message = getString(R.string.data_submited_not_valid)
                                ).show()

                            }
                        }
                    }
                }
            }

        })
    }

    fun getGrade() {
        exploreData?.courseProviderId?.let {
            viewModel.getGrades(it).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            binding.boardGrade?.apply {
                                setGradeSpinner(it.grades.sorted()) {
                                    exploreData?.grade = it
                                    exploreData?.let {
                                        getDigitalSchoolData(it.grade, it.courseProviderId)
                                    }

                                }
                            }
                        }

                    }
                    is Resource.GenericError -> {

                    }
                }
            })
        }
    }

    fun getDigitalSchoolData(grade: Int, courseProviderId: Int) {
        viewModel.getExploreDigitalSchool(grade, courseProviderId)
            .observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            binding.digitalSchool?.apply {
                                exploreData?.school = it
                                setName(it.name ?: "")
                                setCoursesAdapter(it.courses as List<Course>) {
                                    findNavController().navigate(
                                        PartnerExploreDigitalSchoolViewDirections.actionPartnerExploreDigitalSchoolViewToPartnerExploreSubTopicView(
                                            exploreData,
                                            Subject(it.id, it.offeringId, it.name)

                                        )
                                    )
                                }
                                setDSMName(it.dsmName)
                                setNGOName(it.partnerName)
                                setStudents(it.enrolledStudentCount.toString())
//                                setAboutUs(it.statementOfPurpose)
                                setLogo(it.schoolLogoUrl)
                                setBanner(it.bannerUrl)
                                setMedium(it.medium)
                                if(Lingver.getInstance().getLanguage() != CommonConst.LANGUAGE_ENGLISH)
                                    setToggleAboutUsLocale()
                            }
                        }

                    }
                    is Resource.GenericError -> {

                    }
                }

            })
    }

}