package org.evidyaloka.partner.ui.dsm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.custom_recycler_dialog.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.core.partner.network.entity.dsm.SchoolsAssigned

class CustomDialogFragment : DialogFragment(){

    private var type: String? = null
    private var view_type: String? = null
    private var digitalSchoolName: String? = null
    private var schools: ArrayList<SchoolsAssigned>? = null

    companion object {
        const val TAG = "CustomDialogFragment"
        private const val DIALOG_TYPE = "DIALOG_TYPE"
        private const val VIEW_TYPE = "VIEW_TYPE"
        private const val SCHOOLS_ASSIGNED = "SCHOOLS_ASSIGNED"

        fun newInstance(type: String, screen: String) = CustomDialogFragment().apply {
            arguments = Bundle().apply {
                putString(DIALOG_TYPE, type)
                putString(VIEW_TYPE, screen)
                putString(PartnerConst.DIGITAL_SCHOOL_NAME, digitalSchoolName)
                putSerializable(SCHOOLS_ASSIGNED, schools)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString(DIALOG_TYPE)
            view_type = it.getString(VIEW_TYPE)
            schools = it.getSerializable(SCHOOLS_ASSIGNED) as ArrayList<SchoolsAssigned>?
            digitalSchoolName = it.getString(PartnerConst.DIGITAL_SCHOOL_NAME)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        isCancelable = true
        return inflater.inflate(R.layout.custom_recycler_dialog, container, false)
    }

    override fun getTheme(): Int {
        if (PartnerConst.DIALOG_TYPE.FULL_SCREEN.name.equals(type)) {
            return R.style.DialogTheme
        } else {
            return R.style.MaterialAlertDialogTheme
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PartnerConst.VIEW_TYPE.DSM_ASSIGNED_SCHOOL.name.equals(view_type)) {

            schools?.let { list ->

                var adapter = AssignedSchoolAdapter()
                rv_assigned_schools?.let {
                    it.layoutManager = LinearLayoutManager(view.context,
                            LinearLayoutManager.VERTICAL, false)
                    it.adapter = adapter
                    adapter.setItem(list)
                }

            }

            lyClose.setOnClickListener {
                dismiss()
            }
        }
    }


}