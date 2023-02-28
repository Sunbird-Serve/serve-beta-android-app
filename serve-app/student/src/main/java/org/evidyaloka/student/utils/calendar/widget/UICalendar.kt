package org.evidyaloka.student.utils.calendar.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import java.util.*
import android.os.Build
import org.evidyaloka.student.R
import org.evidyaloka.student.utils.calendar.data.Day
import org.evidyaloka.student.utils.calendar.view.ExpandIconView
import org.evidyaloka.student.utils.calendar.view.LockScrollView
import org.evidyaloka.student.utils.calendar.view.OnSwipeTouchListener


@SuppressLint("ClickableViewAccessibility")
abstract class UICalendar constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    protected var mInflater: LayoutInflater

    // UI
    protected var mLayoutRoot: LinearLayout
    protected var mTxtTitle: TextView
    protected var mTableHead: TableLayout
    protected var mScrollViewBody: LockScrollView
    protected var mTableBody: TableLayout
    protected var mLayoutBtnGroupMonth: RelativeLayout
    protected var mLayoutBtnGroupWeek: RelativeLayout
    protected var mBtnPrevMonth: ImageView
    protected var mBtnNextMonth: ImageView
    protected var mBtnPrevWeek: ImageView
    protected var mBtnNextWeek: ImageView
    protected var expandIconView: ExpandIconView
    protected var clEntireTextView: LinearLayout
    protected var mTodayIcon: ImageView
    var datePattern = "MMMM"
        set(value: String) {
            field = value

        }

    // Attributes
    var isShowWeek = true
        set(showWeek) {
            field = showWeek

            if (showWeek) {
                mTableHead.visibility = View.VISIBLE
            } else {
                mTableHead.visibility = View.GONE
            }
        }

    //to remove swipe functionality
    var isSwipe = true
        set(swipe) {
            field = swipe
        }

    var firstDayOfWeek = MONDAY
        set(firstDayOfWeek) {
            field = firstDayOfWeek
            reload()
        }
    var hideArrow = true
        set(value: Boolean) {
            field = value
            hideButton()
        }
    open var state = STATE_COLLAPSED
        set(state) {
            field = state

            if (this.state == STATE_EXPANDED) {
                mLayoutBtnGroupMonth.visibility = View.VISIBLE
                mLayoutBtnGroupWeek.visibility = View.GONE
            }
            if (this.state == STATE_COLLAPSED) {
                mLayoutBtnGroupMonth.visibility = View.GONE
                mLayoutBtnGroupWeek.visibility = View.VISIBLE
            }
        }

    var textColor = Color.BLACK
        set(textColor) {
            field = textColor
            redraw()

            mTxtTitle.setTextColor(this.textColor)
        }
    var primaryColor = Color.WHITE
        set(primaryColor) {
            field = primaryColor
            redraw()

            mLayoutRoot.setBackgroundColor(this.primaryColor)
        }

    var todayItemTextColor = Color.BLACK
        set(todayItemTextColor) {
            field = todayItemTextColor
            redraw()
        }
    var todayItemBackgroundDrawable = resources.getDrawable(R.drawable.calendar_selected_day, null)
        set(todayItemBackgroundDrawable) {
            field = todayItemBackgroundDrawable
            redraw()
        }
    var selectedItemTextColor = Color.WHITE
        set(selectedItemTextColor) {
            field = selectedItemTextColor
            redraw()
        }
    var selectedItemBackgroundDrawable =
        resources.getDrawable(R.drawable.calendar_selected_day, null)
        set(selectedItemBackground) {
            field = selectedItemBackground
            redraw()
        }

    var unSelectedItemTextColor = resources?.getColor(R.color.calendar_text_color)
        set(unSelectedItemTextColor) {
            field = unSelectedItemTextColor
            redraw()
        }

    var unSelectedItemBackgroundDrawable = resources.getDrawable(R.drawable.calendar_unselected_day)
        set(unselectedItemBackgroundDrawable) {
            field = unselectedItemBackgroundDrawable
            redraw()
        }

    /**
     * This can be used to defined the left icon drawable other than predefined icon
     */
    var buttonLeftDrawable = resources.getDrawable(R.drawable.ic_left)
        set(buttonLeftDrawable) {
            field = buttonLeftDrawable
            mBtnPrevMonth.setImageDrawable(buttonLeftDrawable)
            mBtnPrevWeek.setImageDrawable(buttonLeftDrawable)
        }

    /**
     *  This can be used to set the drawable for the right icon, other than predefined icon
     */
    var buttonRightDrawable = resources.getDrawable(R.drawable.ic_right)
        set(buttonRightDrawable) {
            field = buttonRightDrawable
            mBtnNextMonth.setImageDrawable(buttonRightDrawable)
            mBtnNextWeek.setImageDrawable(buttonRightDrawable)
        }

    var selectedItem: Day? = null

    private var mButtonLeftDrawableTintColor = Color.BLACK
    private var mButtonRightDrawableTintColor = Color.BLACK

    private var mExpandIconColor = Color.BLACK
    var eventColor = Color.BLACK
        private set(eventColor) {
            field = eventColor
            redraw()

        }

    fun getSwipe(context: Context): OnSwipeTouchListener {
        return object : OnSwipeTouchListener(context) {
            override fun onSwipeTop() {
                //do nothing
            }

            override fun onSwipeLeft() {
                if (isSwipe) {
                    if (state == STATE_COLLAPSED) {
                        mBtnNextWeek.performClick()
                    } else if (state == STATE_EXPANDED) {
                        mBtnNextMonth.performClick()
                    }
                }
            }

            override fun onSwipeRight() {
                if (isSwipe) {
                    if (state == STATE_COLLAPSED) {
                        mBtnPrevWeek.performClick()
                    } else if (state == STATE_EXPANDED) {
                        mBtnPrevMonth.performClick()
                    }
                }
            }

            override fun onSwipeBottom() {
                //do nothing
            }
        }
    }

    fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
    }

    init {
        mInflater = LayoutInflater.from(context)

        // load rootView from xml
        val rootView = mInflater.inflate(R.layout.widget_collapsible_calendarview, this, true)

        // init UI
        mLayoutRoot = rootView.findViewById(R.id.layout_root)
        mTxtTitle = rootView.findViewById(R.id.txt_title)
        mTodayIcon = rootView.findViewById(R.id.today_icon)
        mTableHead = rootView.findViewById(R.id.table_head)
        mTableBody = rootView.findViewById(R.id.table_body)
        mLayoutBtnGroupMonth = rootView.findViewById(R.id.layout_btn_group_month)
        mLayoutBtnGroupWeek = rootView.findViewById(R.id.layout_btn_group_week)
        mBtnPrevMonth = rootView.findViewById(R.id.btn_prev_month)
        mBtnNextMonth = rootView.findViewById(R.id.btn_next_month)
        mBtnPrevWeek = rootView.findViewById(R.id.btn_prev_week)
        mBtnNextWeek = rootView.findViewById(R.id.btn_next_week)
        mScrollViewBody = rootView.findViewById(R.id.scroll_view_body)
        expandIconView = rootView.findViewById(R.id.expandIcon)
        expandIconView.visibility = View.GONE
        clEntireTextView = rootView.findViewById(R.id.cl_title)
        /*clEntireTextView.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            expandIconView.performClick()
        })*/

        mLayoutRoot.setOnTouchListener(getSwipe(context));
        mScrollViewBody.setOnTouchListener(getSwipe(context))
        mScrollViewBody.setParams(getSwipe(context))
        val attributes = context.theme.obtainStyledAttributes(
            attrs, R.styleable.UICalendar, defStyleAttr, 0
        )
        setAttributes(attributes)
        attributes.recycle()
    }

    protected abstract fun redraw()
    protected abstract fun reload()
    private fun hideButton() {
        mBtnNextWeek.visibility = View.GONE
        mBtnPrevWeek.visibility = View.GONE
        mBtnNextMonth.visibility = View.GONE
        mBtnPrevMonth.visibility = View.GONE
    }

    protected fun setAttributes(attrs: TypedArray) {
        // set attributes by the values from XML
        //setStyle(attrs.getInt(R.styleable.UICalendar_style, mStyle));
        isShowWeek = attrs.getBoolean(R.styleable.UICalendar_showWeek, isShowWeek)
        firstDayOfWeek = attrs.getInt(R.styleable.UICalendar_firstDayOfWeek, firstDayOfWeek)
        hideArrow = attrs.getBoolean(R.styleable.UICalendar_hideArrows, hideArrow)
        datePattern = attrs.getString(R.styleable.UICalendar_datePattern).let {
            if (it == null)
                datePattern
            else {
                it
            }
        }
        isSwipe = attrs.getBoolean(R.styleable.UICalendar_swipe, isSwipe)
        state = attrs.getInt(R.styleable.UICalendar_state, state)

        textColor = attrs.getColor(R.styleable.UICalendar_textColor, textColor)
        primaryColor = attrs.getColor(R.styleable.UICalendar_primaryColor, primaryColor)

        eventColor = attrs.getColor(R.styleable.UICalendar_eventColor, eventColor)


        todayItemTextColor = attrs.getColor(
            R.styleable.UICalendar_todayItem_textColor, todayItemTextColor
        )
        var todayItemBackgroundDrawable =
            attrs.getDrawable(R.styleable.UICalendar_todayItem_background)
        if (todayItemBackgroundDrawable != null) {
            this.todayItemBackgroundDrawable = todayItemBackgroundDrawable
        } else {
            this.todayItemBackgroundDrawable = todayItemBackgroundDrawable
        }

        selectedItemTextColor = attrs.getColor(
            R.styleable.UICalendar_selectedItem_textColor, selectedItemTextColor
        )
        var selectedItemBackgroundDrawable =
            attrs.getDrawable(R.styleable.UICalendar_selectedItem_background)
        if (selectedItemBackgroundDrawable != null) {
            this.selectedItemBackgroundDrawable = selectedItemBackgroundDrawable
        } else {
            this.selectedItemBackgroundDrawable = selectedItemBackgroundDrawable
        }

        var buttonLeftDrawable = attrs.getDrawable(R.styleable.UICalendar_buttonLeft_drawable)
        if (buttonLeftDrawable != null) {
            buttonLeftDrawable = buttonLeftDrawable
        } else {
            buttonLeftDrawable = this.buttonLeftDrawable
        }

        var buttonRightDrawable = attrs.getDrawable(R.styleable.UICalendar_buttonRight_drawable)
        if (buttonRightDrawable != null) {
            buttonRightDrawable = buttonRightDrawable
        } else {
            buttonRightDrawable = this.buttonRightDrawable
        }

        setButtonLeftDrawableTintColor(
            attrs.getColor(
                R.styleable.UICalendar_buttonLeft_drawableTintColor,
                mButtonLeftDrawableTintColor
            )
        )
        setButtonRightDrawableTintColor(
            attrs.getColor(
                R.styleable.UICalendar_buttonRight_drawableTintColor,
                mButtonRightDrawableTintColor
            )
        )
        setExpandIconColor(attrs.getColor(R.styleable.UICalendar_expandIconColor, mExpandIconColor))
        val selectedItem: Day? = null
    }

    fun setButtonLeftDrawableTintColor(color: Int) {
        this.mButtonLeftDrawableTintColor = color
        mBtnPrevMonth.drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        mBtnPrevWeek.drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        redraw()
    }

    fun setButtonRightDrawableTintColor(color: Int) {

        this.mButtonRightDrawableTintColor = color
        mBtnNextMonth.drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        mBtnNextWeek.drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        redraw()
    }

    fun setExpandIconColor(color: Int) {
        this.mExpandIconColor = color
        expandIconView.setColor(color)
    }

    abstract fun changeToToday()

    companion object {


        // Day of Week
        val MONDAY = 0
        val TUESDAY = 1
        val WEDNESDAY = 2
        val THURSDAY = 3
        val FRIDAY = 4
        val SATURDAY = 5
        val SUNDAY = 6

        // State
        val STATE_EXPANDED = 0
        val STATE_COLLAPSED = 1
        val STATE_PROCESSING = 2
    }


}
