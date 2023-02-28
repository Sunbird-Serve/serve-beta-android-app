package org.evidyaloka.common.ui.digitalschool

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import org.evidyaloka.common.R
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.RoundedCornersTransformation
import org.evidyaloka.common.databinding.LayoutDigitalSchoolBinding
import org.evidyaloka.common.extension.getStringByLocale
import org.evidyaloka.core.model.Course
import org.evidyaloka.student.ui.student.DSOfferingsAdapter
import java.util.*

class DigitalSchoolView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var binding: LayoutDigitalSchoolBinding =
        LayoutDigitalSchoolBinding.inflate(LayoutInflater.from(context), this)
    private var isEnLocale:Boolean = false


    fun setName(name: String) {
        binding.schoolName.text = name
    }

    fun setBanner(url: String?){
        context?.let {
            Glide.with(it)
                .load(url ?: R.mipmap.digital_school_banner)
                .placeholder(R.mipmap.digital_school_banner)
                .error(R.mipmap.digital_school_banner)
                .transform(
                    RoundedCornersTransformation(
                        Utils.convertDpToPixel(45F, context).toInt(),
                        0,
                        RoundedCornersTransformation.CornerType.TOP_RIGHT
                    )
                )
                .into(binding.schoolBanner)
        }
    }

    fun setLogo(url: String?){
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_school_logo_placeholder)
            .error(R.drawable.ic_school_logo_placeholder)
            .circleCrop()
            .into(binding.schoolLogo)
    }

    fun setCoursesAdapter(courses: List<Course>, onCourseSelected: (course: Course) -> Unit){
        binding.courses.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = DSOfferingsAdapter(courses,onCourseSelected)
        }
    }

    fun setAboutUs(value: String){
        binding.aboutUs.text = value
    }

    fun setNGOName(name: String){
        binding.partnerName.text = name
    }

    fun setDSMName(name: String){
        binding.dsmName.text = name
    }

    fun setStudents(count: String){
        binding.studentEnrolled.text = String.format(context.getString(R.string.we_have_around_d_students_enrolled_and_avail_the_better_learning_experience),count)
    }

    fun setMedium(medium: String){
        binding.medium.text = medium
    }

    fun setToggleAboutUsLocale(){
        binding.aboutUs.text = context.getString(R.string.the_digital_school_about_us)
        binding.cbEnglishToggle.apply {
            visibility = View.VISIBLE
            setOnCheckedChangeListener { buttonView, isChecked ->
                binding.aboutUs.text = if (isChecked) {
                    context.getStringByLocale(R.string.the_digital_school_about_us, Locale.ENGLISH)

                } else {
                    context.getString(R.string.the_digital_school_about_us)
                }
                binding.tvAboutDigitalSchool.text = if (isChecked) {
                    context.getStringByLocale(R.string.about_digital_school, Locale.ENGLISH)

                } else {
                    context.getString(R.string.about_digital_school)
                }
                binding.tvCourseOffered.text = if (isChecked) {
                    context.getStringByLocale(R.string.courses_offered, Locale.ENGLISH)

                } else {
                    context.getString(R.string.courses_offered)
                }
                binding.tvMediumInstruction.text = if (isChecked) {
                    context.getStringByLocale(R.string.medium_of_instruction, Locale.ENGLISH)

                } else {
                    context.getString(R.string.medium_of_instruction)
                }
                binding.tvNetworkPartner.text = if (isChecked) {
                    context.getStringByLocale(R.string.network_partner, Locale.ENGLISH)

                } else {
                    context.getString(R.string.network_partner)
                }
                binding.tvDigitalSchoolManager.text = if (isChecked) {
                    context.getStringByLocale(R.string.digital_school_n_manager, Locale.ENGLISH)

                } else {
                    context.getString(R.string.digital_school_n_manager)
                }
            }
        }
    }

}