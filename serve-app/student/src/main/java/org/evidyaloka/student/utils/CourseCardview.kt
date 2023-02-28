package org.evidyaloka.student.utils

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import androidx.cardview.widget.CardView
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.setButtonColor
import org.evidyaloka.common.util.SubjectViewUtils
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.CourseCheckboxBinding


/**
 * @author Madhankumar
 * created on 16-03-2021
 *
 */
class CourseCardview  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr), Checkable {

    interface OnClickListener {
        fun onClick(v: CourseCardview)
    }
    private var isChecked = false
    private var clickListener: OnClickListener? = null
    private lateinit var binding: CourseCheckboxBinding
    private var sessionViewType: SessionViewType =
        SessionViewType.CHECKBOX
    private val CORNER_RADIUS = 15F
    private val CARD_ELEVATION = 6F

    constructor(context: Context, viewType:SessionViewType):this(context){
        sessionViewType = viewType
        initView()
    }

    init {
        attrs?.let {
            val att = context.obtainStyledAttributes(attrs,R.styleable.CourseCardView,0,0)
            val selectedType = att.getInt(R.styleable.CourseCardView_sessionType,-1)
            if(selectedType > -1) {
                sessionViewType = SessionViewType.values()[selectedType]
                initView()
            }
        }
    }

    fun initView() {
        binding = CourseCheckboxBinding.inflate(LayoutInflater.from(context),this)
//        var height = when(sessionViewType){
//            SessionViewType.CHECKBOX -> Utils.convertDpToPixel(85F,context)
//            SessionViewType.TIMETABLE -> Utils.convertDpToPixel(151F,context)
//            SessionViewType.COURSE -> Utils.convertDpToPixel(85F,context)
//        }
        val parms = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        parms.bottomMargin = Utils.convertDpToPixel(resources.getDimension(R.dimen.margin_4dp),context).toInt()
        this.setLayoutParams(parms)
        this.radius = Utils.convertDpToPixel(CORNER_RADIUS,context)
        this.elevation = Utils.convertDpToPixel(CARD_ELEVATION,context)
        this.cardElevation = Utils.convertDpToPixel(CARD_ELEVATION,context)
        setClickable(true)
        setChecked(false)


        when(sessionViewType){
            SessionViewType.CHECKBOX -> {
                binding.llCheckbox.visibility = View.VISIBLE
            }
            SessionViewType.COURSE -> {
                binding.llSchedule.visibility = View.VISIBLE
            }
            SessionViewType.TIMETABLE -> {
                binding.llSchedule.visibility = View.VISIBLE
                binding.tvTime.visibility = View.GONE
                binding.ivIcon.visibility = View.GONE
                binding.tvTimeHead.visibility = View.VISIBLE
                binding.iborder.visibility = View.VISIBLE

            }
        }
        binding.liveView.visibility = View.GONE
//        setCardBackgroundColor(
//            ContextCompat.getColorStateList(
//                getContext(),
//                R.color.selector_card_view_background
//            )
//        )
    }

    protected override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState: IntArray = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            mergeDrawableStates(drawableState,
                CHECKED_STATE_SET
            )
        }else{
            mergeDrawableStates(drawableState,
                CHECKED_STATE_PRESS
            )
        }
        return drawableState
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    override fun setChecked(checked: Boolean) {
        isChecked = checked
        binding.checkBox.isChecked = isChecked
        clickListener?.onClick(this)
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        setChecked(!isChecked)
    }

    fun setIcon(id:Int){
        binding.ivIcon.setImageResource(id)
    }

    fun setBackgroundImage(id:Int){
        binding.root.setBackgroundResource(id)
    }

    fun setSubject(title:String){
        binding.tvSubject.text = title
        val courseUiSettings = SubjectViewUtils.getCourseUISettings(title)
        setBackgroundImage(if(sessionViewType == SessionViewType.TIMETABLE) courseUiSettings.timeTableBackground() else courseUiSettings.background())
        setIcon(courseUiSettings.icon())
        setButtonColor(courseUiSettings.color())
        setCompleteIcon(courseUiSettings.complete())
    }

    fun setMedium(medium: String){
        binding.tvMedium.text = medium
    }

    fun setButtonColor(color:Int){
        binding.checkBox.setButtonColor(resources.getColor(color))
        binding.button.setTextColor(resources.getColor(color))
        setTimeHeaderColor(Util.dakerColor(resources.getColor(color),0.99F,204))
        binding.iborder.setBackgroundColor(Util.lighterColor(resources.getColor(color),0.4F))
    }

    private fun setTimeHeaderColor(color: Int){
        val gd = GradientDrawable()
        gd.setColor(color)
        gd.cornerRadii = floatArrayOf(
            Utils.convertDpToPixel(CORNER_RADIUS,context), Utils.convertDpToPixel(CORNER_RADIUS,context),
            Utils.convertDpToPixel(CORNER_RADIUS,context), Utils.convertDpToPixel(CORNER_RADIUS,context), 0f, 0f, 0f, 0f)
        binding.tvTimeHead.background = gd
    }

    fun setTopic(name:String){
        binding.tvTopic.apply {
            text = name
            visibility = View.VISIBLE
        }
    }

    fun setTeacherName(name:String){
        binding.tvFaculty.apply {
            text = name
            visibility = View.VISIBLE
        }
    }
    private fun setCompleteIcon(drawable: Int){
        binding.ivComplete.setImageResource(drawable)
    }

    fun setSessionStatus(state: SessionState){
        binding.liveView.visibility = View.GONE
        when(state){
            SessionState.COMPLETE -> {
                binding.llSchedule.visibility = View.GONE
                binding.llCompleted.visibility = View.VISIBLE
            }
            SessionState.SCHEDULE -> {
                binding.llSchedule.visibility = View.VISIBLE
                binding.llCompleted.visibility = View.GONE
            }
            SessionState.LIVE -> {
                binding.llCompleted.visibility = View.GONE
                binding.llSchedule.visibility = View.VISIBLE
                binding.button.setText(context.getString(R.string.join_class))
                if(sessionViewType == SessionViewType.TIMETABLE) {
                    binding.button.visibility = View.VISIBLE
                }else{
                    binding.button.visibility = View.GONE
                }
                binding.liveView.visibility = View.VISIBLE
            }
        }

    }

    fun setSessionTime(time:Long){
        binding.tvTime.text = Utils.getTimeIn12H(time)
        binding.tvTimeHead.text = Utils.getTimeIn12H(time)
    }
    fun setSessionTime(time:String){
        binding.tvTime.text = time
        binding.tvTimeHead.text = time
    }

    fun setClickListener(listener: OnClickListener){
        clickListener = listener
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(
            android.R.attr.state_checked
        )
        private val CHECKED_STATE_PRESS = intArrayOf(
            android.R.attr.state_pressed
        )
    }

    enum class SessionViewType{CHECKBOX,COURSE,TIMETABLE }
    enum class SessionState{LIVE,SCHEDULE,COMPLETE }
}