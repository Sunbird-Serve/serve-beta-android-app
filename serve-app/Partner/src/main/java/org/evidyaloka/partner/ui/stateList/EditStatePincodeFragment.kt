package org.evidyaloka.partner.ui.stateList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_state_pincode.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.common.view.SuccessDialogFragment
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.core.partner.model.State
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.digitalschool.DIGITAL_SCHOOL_DETAILS
import org.evidyaloka.partner.ui.digitalschool.IS_STATE_PINCODE_RESULT

@AndroidEntryPoint
class EditStatePincodeFragment : BaseFragment() {

    private val viewModel: StateViewModel by viewModels()
    private var digitalSchool: DigitalSchool? = null
    private var state: State? = null
    private var isAddLocation: Boolean = false
    private var dialog: SuccessDialogFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args =
                EditStatePincodeFragmentArgs.fromBundle(
                    it
                )
            digitalSchool = args.dschool
            state = args.state
            isAddLocation = args.isAddLocation
        }
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(isAddLocation){
                    navigateToDS()
                }else{
                    navController.popBackStack()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_state_pincode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        state?.let {
            setAdapter(listOf(it))
        }

        addPincode?.setOnClickListener {
            navController.navigate(
                EditStatePincodeFragmentDirections.actionEditStatePincodeFragmentToPincodeListFragment(
                    digitalSchool,
                    state,
                    isAddLocation
                )
            )
        }
    }

    private fun setAdapter(list: List<State>) {
        rcView?.apply {
            adapter = EditStatePincodeAdapter(list, deleteState = { state ->
                dialog = SuccessDialogFragment.Builder(context)
                    .setIsDialogCancelable(true)
                    .setTitle(context.getString(R.string.delete).plus("!"))
                    .setDescription(
                        String.format(
                            context.getString(R.string.delete_state),
                            state.name
                        )
                    )
                    .setIcon(R.drawable.ic_trash)
                    .setIconBackground(R.drawable.ic_pop_img_bg)
                    .setViewType(SuccessDialogFragment.DIALOG_TYPE.ALERT)
                    .setButtonType(SuccessDialogFragment.BUTTON_TYPE.FLAT_NO_COLOR)
                    .setButtonPositiveText(
                        context.getString(R.string.delete),
                        View.OnClickListener {
                            if(isAddLocation){
                                digitalSchool?.selectedState?.remove(state)
                                navigateToDS()
                            }else {
                                deleteState(state?.id ?: 0)
                            }
                            dialog?.dismiss()
                        })
                    .setButtonNegativeText(
                        context.getString(R.string.cancel),
                        View.OnClickListener {
                            dialog?.dismiss()
                        })
                    .build()
                dialog?.show(childFragmentManager, "")

            },
                deletePinCode = { pincode ->
                    dialog = SuccessDialogFragment.Builder(context)
                        .setIsDialogCancelable(true)
                        .setTitle(context.getString(R.string.remove).plus("!"))
                        .setDescription(context.getString(R.string.remove_pincode))
                        .setIcon(R.drawable.ic_cross_blue)
                        .setIconBackground(R.drawable.ic_pop_img_bg)
                        .setViewType(SuccessDialogFragment.DIALOG_TYPE.ALERT)
                        .setButtonType(SuccessDialogFragment.BUTTON_TYPE.FLAT_NO_COLOR)
                        .setButtonPositiveText(
                            context.getString(R.string.remove),
                            View.OnClickListener {
                                if(isAddLocation){
                                    deletePincodeLocal(pincode.id)
                                }else {
                                    deletePincode(pincode?.id ?: 0)
                                }
                                dialog?.dismiss()
                            })
                        .setButtonNegativeText(
                            context.getString(R.string.cancel),
                            View.OnClickListener {
                                dialog?.dismiss()
                            })
                        .build()
                    dialog?.show(childFragmentManager, "")

                })
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun deleteState(stateId: Int) {
        var map: HashMap<String, Any> = hashMapOf()
        map[PartnerConst.SCHOOL_ID] = digitalSchool?.id ?: 0
        map[PartnerConst.DELETED_STATE_IDS] = listOf(stateId)
        viewModel?.deleteLocation(map).observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    setAdapter(listOf())
                }
            }
        })
    }

    private fun navigateToDS(){
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

    private fun deletePincode(pinCodeId: Int) {
        var map: HashMap<String, Any> = hashMapOf()
        map[PartnerConst.SCHOOL_ID] = digitalSchool?.id ?: 0
        map[PartnerConst.DELETED_PINCODE_IDS] = listOf(pinCodeId)
        viewModel?.deleteLocation(map).observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    deletePincodeLocal(pinCodeId)
                }
            }
        })
    }

    private fun deletePincodeLocal(pinCodeId: Int){
        state?.let {
            it.pincodes = it.pincodes?.filterNot { it.id == pinCodeId }
            setAdapter(listOf(it))
        }
    }
}