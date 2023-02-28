package org.evidyaloka.student.ui.explore.schoolboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.explore.ExploreSchoolBoard
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.ExploreData
import org.evidyaloka.student.R
import org.evidyaloka.student.ui.explore.OnboardingViewModel
import org.evidyaloka.student.utils.Util

@AndroidEntryPoint
class FragmentSchoolBoard : ExploreSchoolBoard() {
    private val TAG = "FragmentSchoolBoard"
    private val viewModel: OnboardingViewModel by viewModels()
    private var exploreData: ExploreData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            var args = FragmentSchoolBoardArgs.fromBundle(it)
            exploreData = args.exploreData
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getCourseProviders().observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            schoolBoardList.clear()
                            schoolBoardList.addAll(it)
                            schoolBoardAdapter.setItems(it)
                        }
                    }

                    is Resource.GenericError -> {
                        it.code?.let {
                            if (it <= 0)
                                return@Observer
                            else{
                                Util.showPopupDialog(
                                    requireContext(),
                                    getString(R.string.error),
                                    getString(R.string.data_submited_not_valid)
                                )
                            }
                        }
                    }
                }
            }

        })

        binding.btContinue.setOnClickListener {
            schoolBoardAdapter?.let {
                it.getSelectedCourseProvoider()?.let {
                    exploreData?.courseProviderId = it.id
                    findNavController().navigate(FragmentSchoolBoardDirections.actionFragmentSchoolBoardToFragmentGradeSelection(exploreData))
                }
            }
        }


    }

}