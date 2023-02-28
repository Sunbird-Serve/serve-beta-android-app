package org.evidyaloka.common.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.evidyaloka.common.R
import org.evidyaloka.common.databinding.LayoutSliderIndicatorBinding

class SliderIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutSliderIndicatorBinding.inflate(LayoutInflater.from(context),this)

    init {
        currentStep(1)
    }

    fun setStep1Text(text: String){
        binding.tvStep1.text = text
    }

    fun setStep2Text(text: String){
        binding.tvStep2.text = text
    }

    fun currentStep(step:Int){
        if(step == 1){
            binding.ivStep1?.setImageDrawable(null)
            binding.ivStep1?.isEnabled = true
            binding.ivStep2.isEnabled = false
            binding.dividerStep.setBackgroundColor(Color.parseColor("#80CCD0D5"))
        }else {
            binding.ivStep1?.setImageDrawable(resources.getDrawable(R.drawable.ic_done_circle))
            binding.ivStep2?.isEnabled = true
            binding.dividerStep?.setBackgroundResource(R.color.buttoncolor)
        }
    }
}