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
import kotlinx.android.synthetic.main.fragment_digital_shcool.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.core.partner.model.Pincode
import org.evidyaloka.core.partner.model.State
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.FragmentPincodeListBinding
import org.evidyaloka.partner.databinding.FragmentStateListBinding
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.digitalschool.DIGITAL_SCHOOL_DETAILS
import org.evidyaloka.partner.ui.digitalschool.IS_EDIT_DIGITAL_SCHOOL
import org.evidyaloka.partner.ui.digitalschool.IS_STATE_PINCODE_RESULT


@AndroidEntryPoint
class PincodeListFragment : BaseFragment() {

    private val viewModel: StateViewModel by viewModels()
    private lateinit var binding: FragmentPincodeListBinding
    private var adapter = PincodeAdapter()
    private var digitalSchool: DigitalSchool? = null
    private var state: State? = null
    private var isAddLocation: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = PincodeListFragmentArgs.fromBundle(it)
            digitalSchool = args.digitalSchool
            state = args.state
            isAddLocation = args.isAddLocation
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPincodeListBinding.inflate(layoutInflater, container, false)
        binding.rcPincodeList?.adapter = adapter
        binding.rcPincodeList?.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.rcPincodeList?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.etvSearch?.addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
            text?.takeIf { it.length >= 5 }?.let {
                state?.id?.let { id -> getStatePincodeList(id, text.toString()) }
            }
        })
        binding.btDone?.setOnClickListener {
            adapter?.getSelectedPincode()?.let {
                state?.pincodes = it as ArrayList<Pincode>
            }
            digitalSchool?.apply {
                state?.let { state ->
                    this.selectedState.indexOfFirst { it.id == state.id }.takeIf { it >= 0 }
                        ?.let { index ->
                            this.selectedState[index] = state
                        } ?: this.selectedState?.add(state)
                }
            }
            if (isAddLocation) {
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
            } else {
                saveSate()
            }
        }
        state?.let {
            if (it.pincodes.size > 0) {
                adapter.setSelectedPincodes(it.pincodes, !isAddLocation)
            }
            it.id?.let { id -> getStatePincodeList(id) }

        }
        return binding.root
    }

    private fun getStatePincodeList(stateId: Int, searchQuery: String? = null) {
        viewModel.getStatePincode(stateId, searchQuery).observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    private fun saveSate() {
        var map: HashMap<String, Any> = hashMapOf()
        var stateList = mutableListOf<HashMap<String, Any>>()
        var stateMap: HashMap<String, Any> = hashMapOf()
        map[PartnerConst.SCHOOL_ID] = digitalSchool?.id ?: 0
        state?.let {
            stateMap[PartnerConst.STATE_ID] = it.id
            var pincodeList = mutableListOf<HashMap<String, Any>>()
            adapter?.getSelectedPincode()?.let {
                (it as ArrayList<Pincode>)?.forEach {
                    var pincodeMap: HashMap<String, Any> = hashMapOf()
                    pincodeMap[PartnerConst.PINCODE_ID] = it.id
                    pincodeMap[PartnerConst.PINCODE_CODE] = it.pincode.toString()
                    pincodeList.add(pincodeMap)
                }
            }
            stateMap[PartnerConst.PINCODES] = pincodeList
            stateList.add(stateMap)
        }
        map[PartnerConst.SELECTED_STATES] = stateList
        viewModel.saveState(map).observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    val navOption = NavOptions.Builder().apply {
                        setPopUpTo(R.id.statePincodeViewFragment, true)
                    }.build()
                    navController.navigate(
                        PincodeListFragmentDirections.actionGlobalStatePincodeViewFragment(
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