package org.evidyaloka.partner.ui.home.dsp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_digital_school.view.*
import kotlinx.android.synthetic.main.item_digital_school_courses_offered.view.*
import kotlinx.android.synthetic.main.item_digital_school_student_enrolled.view.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.core.partner.model.DigitalSchool

class DspSchoolAdapter(
        private val clickListener: (digitalSchool: DigitalSchool) -> Unit
) : RecyclerView.Adapter<DspSchoolAdapter.DspSchoolHolder>() {
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
        holder.bind(school, clickListener)
    }

    class DspSchoolHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(school: DigitalSchool, clickListener: (digitalSchool: DigitalSchool) -> Unit) {
            try {
                var requestOptions = RequestOptions()
                requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))

                view.iv_banner?.let {
                    it.context?.let { it1 ->
                        Glide.with(it1)
                                .load(school.bannerUrl)
                                .placeholder(R.mipmap.digital_school_banner)
                                .error(R.mipmap.digital_school_banner)
                                .centerCrop()
                                .apply(requestOptions)
                                .into(it)
                    }
                }

                view.iv_school_logo?.let {
                    it.context?.let { it1 ->
                        Glide.with(it1)
                                .load(school.logoUrl)
                                .placeholder(R.drawable.ic_school_logo_placeholder)
                                .error(R.drawable.ic_school_logo_placeholder)
                                .circleCrop()
                                .into(it)
                    }
                }

                view.tv_school_name?.setText(school.name)
                if(school.dsmId == null || school.dsmId == 0){
                    view.tv_dsm?.visibility = View.GONE
                    view?.bt_assign_dsm?.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            try{
                                findNavController().navigate(R.id.action_global_usersFragment)
                            }catch (e:Exception){

                            }
                        }
                    }
                }else {
                    val dsmFullName = school.dsmFirstName?.let {
                        view.resources.getString(R.string.dsm) + ": " + it.plus(" ")?.plus(
                            school.dsmLastName
                                ?: ""
                        )
                    } ?: view.resources.getString(R.string.not_assigned)

                    view.tv_dsm?.apply {
                        visibility = View.VISIBLE
                        text = dsmFullName
                    }
                }
                view.tv_courses_details?.setText(school.courseCount.toString())
                view.tv_students_details?.setText(school.studentCount.toString())

                if (school.status == PartnerConst.SchoolStatus.Active.name) {
                    view.tv_school_status?.setText(PartnerConst.SchoolStatusType.APPROVED.toString())
                    view.tv_school_status?.background = view.resources.getDrawable(R.drawable.button_approved,null)
                }else {
                    view.tv_school_status?.setText(PartnerConst.SchoolStatusType.PENDING.toString())
                    view.tv_school_status?.background = view.resources.getDrawable(R.drawable.button_pending,null)
                }

                view.setOnClickListener {
                    clickListener(school)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}