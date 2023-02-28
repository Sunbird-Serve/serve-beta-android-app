package org.evidyaloka.common.view

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.dialog_success_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.evidyaloka.common.R
import org.evidyaloka.common.util.Utils

class SuccessDialogFragment private constructor(val builder: Builder) : DialogFragment(){

    private var title: String? = null
    private var description: String? = null
    private var icon: Int? = null
    private var iconBackground: Int? = null
    private var viewType: DIALOG_TYPE =
        DIALOG_TYPE.ALERT
    private var buttonType: BUTTON_TYPE =
        BUTTON_TYPE.ROUND_COLORED
    private var isDialogCancelable: Boolean = true
    private var dismissTimer: Long? = null
    private var onDismissListener: OnDismissListener? = null
    private var buttonPositiveText: String? = null
    private var buttonNegativeText: String? = null
    private var clickListner: View.OnClickListener? = null
    private var clickNegativeListner: View.OnClickListener? = null

    private var type: String? = null
    private lateinit var tv_title: TextView
    private lateinit var tv_description: TextView
    private lateinit var iv_icon: ImageView
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button

    init {
        title = builder.title
        description = builder.description
        icon = builder.icon
        iconBackground = builder.iconBackground
        viewType = builder.viewType
        buttonType = builder.buttonType
        isDialogCancelable = builder.isDialogCancelable
        builder.dismissTimer?.let { dismissTimer = it }
        onDismissListener = builder.onDismissListener
        buttonPositiveText = builder.buttonPositiveText
        buttonNegativeText = builder.buttonNegativeText
        clickListner = builder.clickListner
        clickNegativeListner = builder.clickNegativeListner
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_success_layout, container, false)
        dialog?.window?.apply {
            this.setBackgroundDrawableResource(R.drawable.curved_corner_bg)
            this.requestFeature(Window.FEATURE_NO_TITLE);
        }
        tv_title = view.findViewById(R.id.tv_title)
        tv_description = view.findViewById(R.id.tv_description)
        iv_icon = view.findViewById(R.id.iv_icon)
        buttonPositive = view.findViewById(R.id.buttonPositive)
        buttonNegative = view.findViewById(R.id.buttonnegative)
        return view
    }

    override fun getTheme(): Int {
        if (DIALOG_TYPE.FULL_SCREEN.equals(viewType)) {
            return R.style.DialogTheme
        } else {
            return R.style.MaterialAlertDialogTheme
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title?.let { tv_title?.text = it }
        description?.let { tv_description?.text = it }
        icon?.let { iv_icon?.setImageResource(it) }
        iconBackground?.let { iv_icon?.setBackgroundResource(it) }
        if (DIALOG_TYPE.FULL_SCREEN == viewType){
            iv_icon?.setPadding(0)
        }
        buttonPositive?.apply {
            buttonPositiveText?.let {
                this.visibility = View.VISIBLE
                this.text = it
                if(clickListner == null){
                    this.setOnClickListener(View.OnClickListener {
                        this@SuccessDialogFragment.dismiss()
                    })
                }else {
                    this.setOnClickListener(clickListner)
                }
                if (BUTTON_TYPE.FLAT_NO_COLOR == buttonType){
                    this.setBackgroundResource(R.drawable.bottom_right_corner_secondary)
                    this.setTextAppearance(requireContext(),R.style.Theme_Button_Dialog_Flat)
                }else{
                    var parms: LinearLayout.LayoutParams = this.layoutParams as LinearLayout.LayoutParams
                    val _20dp = Utils.convertDpToPixel(20F,requireContext()).toInt()
                    parms.setMargins(_20dp/2,0,_20dp,_20dp)
                    this.layoutParams = parms
                }
            }
        }
        buttonNegative?.apply {
            buttonNegativeText?.let {
                this.visibility = View.VISIBLE
                this.text = it
                if(clickNegativeListner == null){
                    this.setOnClickListener(View.OnClickListener {
                        this@SuccessDialogFragment.dismiss()
                    })
                }else {
                    this.setOnClickListener(clickNegativeListner)
                }
                if (BUTTON_TYPE.FLAT_NO_COLOR == buttonType){
                    this.setBackgroundResource(R.drawable.bottom_left_corner_secondary)
                    this.setTextAppearance(requireContext(),R.style.Theme_Button_Dialog_Flat)
                }else{
                    var parms: LinearLayout.LayoutParams = this.layoutParams as LinearLayout.LayoutParams
                    val _20dp = Utils.convertDpToPixel(20F,requireContext()).toInt()
                    parms.setMargins(_20dp,0,_20dp/2,_20dp)
                    this.layoutParams = parms
                }
            }
        }
        isCancelable = isDialogCancelable
        dismissTimer?.let { timeMillis->
            lifecycleScope.launch {
                delay(timeMillis)
                try {
                    this@SuccessDialogFragment.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.OnDismiss(dialog)
        Log.e("SuccessDialogFragment","isDismissed")
    }

    enum class DIALOG_TYPE {
        FULL_SCREEN, ALERT
    }

    enum class BUTTON_TYPE {
        FLAT_NO_COLOR, ROUND_COLORED
    }

    interface OnDismissListener {
        fun OnDismiss(dialog: DialogInterface)
    }

    class Builder(val context: Context){

        internal var title: String? = null
            private set
        internal var description: String? = null
            private set
        internal var icon: Int? = null
            private set
        internal var iconBackground: Int? = null
            private set
        internal var viewType: DIALOG_TYPE =
            DIALOG_TYPE.ALERT
            private set
        internal var buttonType: BUTTON_TYPE =
            BUTTON_TYPE.ROUND_COLORED
            private set
        internal var isDialogCancelable: Boolean = true
            private set
        internal var dismissTimer: Long? = null
            private set
        internal var onDismissListener: OnDismissListener? = null
            private set
        internal var buttonPositiveText: String? = null
            private set
        internal var buttonNegativeText: String? = null
            private set
        internal var clickListner: View.OnClickListener? = null
            private set
        internal var clickNegativeListner: View.OnClickListener? = null
            private set

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setDescription(description: String): Builder {
            this.description = description
            return this
        }

        fun setIcon(icon: Int): Builder {
            this.icon = icon
            return this
        }

        fun setIconBackground(res: Int):Builder{
            this.iconBackground = res
            return this
        }

        fun setViewType(type: DIALOG_TYPE): Builder {
            this.viewType = type
            return this
        }

        fun setButtonType(type: BUTTON_TYPE): Builder {
            this.buttonType = type
            return this
        }

        fun build(): SuccessDialogFragment {
            return SuccessDialogFragment(this)
        }

        fun setIsDialogCancelable(isCancelable: Boolean): Builder {
            this.isDialogCancelable = isCancelable
            return this
        }

        fun setDismissTimer(timeMillis: Long): Builder {
            this.dismissTimer = timeMillis
            return this
        }

        fun setOnDismissListener(listener: OnDismissListener): Builder {
            this.onDismissListener = listener
            return this
        }

        fun setButtonPositiveText(text:String,listner: View.OnClickListener?= null): Builder {
            this.buttonPositiveText = text
            this.clickListner = listner
            return this
        }

        fun setButtonNegativeText(text:String,listner: View.OnClickListener?= null): Builder {
            this.buttonNegativeText = text
            this.clickNegativeListner = listner
            return this
        }

        fun setOnClickListner(listner: View.OnClickListener): Builder {
            this.clickListner = listner
            return this
        }


    }


}