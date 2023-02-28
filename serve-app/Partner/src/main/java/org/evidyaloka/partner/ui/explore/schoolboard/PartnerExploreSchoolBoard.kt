package org.evidyaloka.partner.ui.explore.schoolboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.explore.ExploreSchoolBoard
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.ExploreData
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.HomeActivity
import org.evidyaloka.partner.ui.explore.OnboardingViewModel

@AndroidEntryPoint
class PartnerExploreSchoolBoard : ExploreSchoolBoard() {
    private val TAG = "FragmentSchoolBoard"
    private val viewModel: OnboardingViewModel by viewModels()
    private var exploreData: ExploreData? = ExploreData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            var args = PartnerExploreSchoolBoardArgs.fromBundle(it)
            exploreData = args.exploreData
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity)?.let{
            it.updateToolbarTitle(getString(R.string.choose_your_school_board))
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
//                                showSuccessDialog(
//                                    getString(R.string.error),
//                                    getString(R.string.data_submited_not_valid)
//                                )
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
                    findNavController().navigate(PartnerExploreSchoolBoardDirections.actionPartnerExploreSchoolBoardToParentExploreGradeSelection(exploreData))
                }
            }
        }


    }

}