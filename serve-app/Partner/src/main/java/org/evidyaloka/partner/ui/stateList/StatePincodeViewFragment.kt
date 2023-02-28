package org.evidyaloka.partner.ui.stateList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.FragmentViewStatePincodeBinding
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.digitalschool.DigitalSchoolFragmentDirections

@AndroidEntryPoint
class StatePincodeViewFragment : BaseFragment() {

    private val viewModel: StateViewModel by viewModels()
    private var digitalSchool: DigitalSchool? = null
    private lateinit var binding: FragmentViewStatePincodeBinding
    private val stateAdapter = SelectedStateAdapter({
        navController.navigate(StatePincodeViewFragmentDirections.actionStatePincodeViewFragmentToEditStatePincodeFragment(digitalSchool,it))
    })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            var args =
                StatePincodeViewFragmentArgs.fromBundle(
                    it
                )
            args.digitalSchool?.let{ digitalSchool = it}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewStatePincodeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        digitalSchool?.let{
            getSchoolDetails(it.id)
            it.selectedState?.let { stateAdapter.setItem(it) }
        }
        binding.rcView?.apply {
            visibility = View.VISIBLE
            adapter = stateAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.addState?.setOnClickListener {
            navController.navigate(StatePincodeViewFragmentDirections.actionStatePincodeViewFragmentToStateListFragment(digitalSchool,false))
        }

    }

    private fun getSchoolDetails(id:Int){
        viewModel.getDigitalSchool(id).observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { schoolDetails ->
                        val mSchoolDetails = schoolDetails.listDigitalSchools[0]
                        mSchoolDetails?.let {
                            it.selectedState?.let { stateAdapter.setItem(it) }
                        }
                    }
                }
                else -> {
                    handleNetworkError()
                }
            }
        })
    }
}