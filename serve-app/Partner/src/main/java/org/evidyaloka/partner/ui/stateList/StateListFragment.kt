package org.evidyaloka.partner.ui.stateList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.core.partner.model.State
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.FragmentStateListBinding
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.digitalschool.DIGITAL_SCHOOL_DETAILS
import org.evidyaloka.partner.ui.digitalschool.IS_STATE_PINCODE_RESULT


@AndroidEntryPoint
class StateListFragment : BaseFragment() {

    private val viewModel: StateViewModel by viewModels()
    private lateinit var binding: FragmentStateListBinding
    private var adapter = StateAdapter()
    private var stateList: List<State> = listOf()
    private var digitalSchool: DigitalSchool? = null
    private var isAddLocation: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val arg = StateListFragmentArgs.fromBundle(it)
            digitalSchool = arg.digitalSchool
            isAddLocation = arg.isAddLocation
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStateListBinding.inflate(layoutInflater, container, false)
        binding.rcStateList.adapter = adapter
        binding.rcStateList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.rcStateList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.etvSearch.addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
            text?.takeIf { it.length > 0 }?.let { query ->

                stateList.filter { it.name?.contains(query, true) == true }?.let {
                    adapter.setItem(it)
                }
            } ?: adapter.setItem(stateList)
        })
        getStateList()
        adapter?.setSateSelectListner(object : StateAdapter.IOnStateSelectListner {
            override fun onStateSelected(state: State) {
                binding.btConfirmState.isEnabled = true
                binding.btSelectPincode.isEnabled = true
            }
        })
        binding.btConfirmState?.setOnClickListener {
            hideKeyboard()
            digitalSchool?.apply {
                adapter.getSelectedState()?.let { state ->
                    this.selectedState.indexOfFirst { it.id == state.id }.takeIf { it >= 0 }
                        ?.let { index ->
                            this.selectedState[index] = state
                        } ?: this.selectedState?.add(state)
                }
            }
            if (isAddLocation) {
                newDigitalSchoolAddState()
            } else {
                saveSate()
            }
        }
        binding.btSelectPincode?.setOnClickListener {
            hideKeyboard()
            adapter.getSelectedState()?.let {
                navController.navigate(
                    StateListFragmentDirections.actionStateListFragmentToPincodeListFragment(
                        digitalSchool,
                        it,
                        isAddLocation
                    )
                )
            }
        }
        return binding.root
    }

    private fun getStateList() {
        viewModel.getStateList().observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    it?.data?.let {
                        stateList = it
                        adapter.setItem(it)
                    }
                }
                is Resource.GenericError -> {

                }

            }
        })
    }

    private fun newDigitalSchoolAddState() {
        try {
            val arguments = Bundle().apply {
                putBoolean(IS_STATE_PINCODE_RESULT, true)
                putParcelable(DIGITAL_SCHOOL_DETAILS, digitalSchool)
            }
            val navOption = NavOptions.Builder().apply {
                setPopUpTo(R.id.digitalSchoolFragment, true)
            }.build()
            navController.navigate(
                R.id.digitalSchoolFragment,
                arguments,
                navOption
            )
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun saveSate() {
        var map: HashMap<String, Any> = hashMapOf()
        var stateList = mutableListOf<HashMap<String, Any>>()
        var stateMap: HashMap<String, Any> = hashMapOf()
        map[PartnerConst.SCHOOL_ID] = digitalSchool?.id ?: 0
        adapter?.getSelectedState()?.let {
            stateMap[PartnerConst.STATE_ID] = it.id
            var pincodeList = mutableListOf<HashMap<String, Any>>()
            it.pincodes?.forEach {
                var pincodeMap: HashMap<String, Any> = hashMapOf()
                pincodeMap[PartnerConst.PINCODE_ID] = it.id
                pincodeMap[PartnerConst.PINCODE_CODE] = it.pincode.toString()
                pincodeList.add(pincodeMap)
            }
            stateMap[PartnerConst.PINCODES] = pincodeList
            stateList.add(stateMap)
        }
        map[PartnerConst.SELECTED_STATES] = stateList
        viewModel.saveState(map).observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    val navOption = NavOptions.Builder().apply{
                        setPopUpTo(R.id.statePincodeViewFragment,true)
                    }.build()
                    navController.navigate(
                        StateListFragmentDirections.actionGlobalStatePincodeViewFragment(
                            digitalSchool
                        ),
                        navOption
                    )
                }
                is Resource.GenericError -> {
                    it.error?.let {
                        when (it?.code) {
                            2 -> {
                                showSnackBar(getString(R.string.error_required_field_missing))
                            }
                            23 -> {
                                showSnackBar(getString(R.string.only_digital_school_patner_allowed))
                            }
                            3 -> {
                                showSnackBar(getString(R.string.invalid_data_contact_admin))

                            }
                            else -> showSnackBar(it.message)
                        }
                    }
                }
            }
        })
    }


}