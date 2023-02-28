package org.evidyaloka.partner.ui.home.dsm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.item_digital_school.view.*
import kotlinx.android.synthetic.main.item_digital_school_courses_offered.view.*
import kotlinx.android.synthetic.main.item_digital_school_student_enrolled.view.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.partner.ui.digitalschool.DIGITAL_SCHOOL_ID

class DsmSchoolAdapter() : RecyclerView.Adapter<DsmSchoolAdapter.DspSchoolHolder>() {
    var digitalSchools: List<DigitalSchool> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DspSchoolHolder {
        return DspSchoolHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_digital_school, parent, false)
        )
    }

    fun setItem(digitalSchools: List<DigitalSchool>) {
        this.digitalSchools = digitalSchools
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return digitalSchools.size
    }

    override fun onBindViewHolder(holder: DspSchoolHolder, position: Int) {
        val school = digitalSchools[position]
        holder.bind(school)
    }

    class DspSchoolHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(school: DigitalSchool) {
            try {
                view?.tv_dsm?.visibility = View.GONE
                view?.ll_dsm_action?.visibility = View.VISIBLE
                //Todo use logoID to show digitalSchool logo from Base url once API is live
                view?.iv_school_logo?.let {
                    it.context?.let { context ->
                        Glide.with(context)
                                .load(school.logoUrl)
                                .placeholder(R.drawable.ic_school_logo_placeholder)
                                .error(R.drawable.ic_school_logo_placeholder)
                                .circleCrop()
                                .into(it)
                    }
                }


                var requestOptions = RequestOptions()
                requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))

                view?.iv_banner?.let {
                    it.context?.let { context ->
                        Glide.with(context)
                                .load(school.bannerUrl)
                                .placeholder(R.mipmap.digital_school_banner)
                                .error(R.mipmap.digital_school_banner)
                                .apply(requestOptions)
                                .into(it)
                    }
                }

                view?.tv_school_name?.setText(school.name)
                view?.tv_courses_details?.setText(school.courseCount.toString())
                view?.tv_students_details?.setText(school.studentCount.toString())

                if (school.status == PartnerConst.SchoolStatus.Active.name) {
                    view.tv_school_status?.setText(PartnerConst.SchoolStatusType.APPROVED.toString())
                    view.tv_school_status?.background = view.resources.getDrawable(R.drawable.button_approved,null)
                }else {
                    view.tv_school_status?.setText(PartnerConst.SchoolStatusType.PENDING.toString())
                    view.tv_school_status?.background = view.resources.getDrawable(R.drawable.button_pending,null)
                }

                school.courseCount?.let {
                    view?.bt_enroll_student?.isEnabled = it > 0
                }

                view?.bt_enroll_student?.setOnClickListener {
                    if (PartnerConst.SchoolStatus.Active.name.equals(school.status)) {
                        val bundle = Bundle()
                        bundle.putInt(DIGITAL_SCHOOL_ID, school.id)
                        bundle.putString(PartnerConst.DIGITAL_SCHOOL_NAME, school.name)
                        bundle.putString(PartnerConst.ROLE, PartnerConst.RoleType.DSM.name)
                        school.courseProviderList?.let { bundle.putSerializable(PartnerConst.COURSE_PROVIDER, it as ArrayList) }
                        try{
                        view.findNavController().navigate(R.id.action_global_studentFragment, bundle)
                        }catch (e : Exception){
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    } else {
                        Toast.makeText(view.context, view.resources.getString(R.string.school_verification_pending), Toast.LENGTH_LONG).show()
                    }
                }

                view?.bt_add_course?.setOnClickListener {
                    if (PartnerConst.SchoolStatus.Active.name.equals(school.status)) {
                        val arg = Bundle()
                        arg.putInt(DIGITAL_SCHOOL_ID, school.id)
                        arg.putString(PartnerConst.DIGITAL_SCHOOL_NAME, school.name)
                        arg.putString(PartnerConst.ROLE, PartnerConst.RoleType.DSM.name)
                        school.courseProviderList?.let {
                            arg.putSerializable(PartnerConst.COURSE_PROVIDER, it as ArrayList)
                        }
                        try{
                        view.findNavController().navigate(R.id.action_dsmHomeFragment_to_addCourseFragment, arg)
                        }catch (e : Exception){
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    } else {
                        Toast.makeText(view.context, view.resources.getString(R.string.school_verification_pending), Toast.LENGTH_LONG).show()
                    }

                }

                view?.setOnClickListener {
                    val arg = Bundle()
                    arg.putInt(DIGITAL_SCHOOL_ID, school.id)
                    arg.putString(PartnerConst.DIGITAL_SCHOOL_NAME, school.name)
                    arg.putString(PartnerConst.ROLE, PartnerConst.RoleType.DSM.name)
                    school.courseProviderList?.let { arg.putSerializable(PartnerConst.COURSE_PROVIDER, it as ArrayList) }
                    try{
                    view?.findNavController().navigate(R.id.action_global_digitalSchoolDetailsFragment, arg)
                    }catch (e : Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}