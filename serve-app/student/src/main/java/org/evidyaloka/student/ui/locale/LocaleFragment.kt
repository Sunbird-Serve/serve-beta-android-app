package org.evidyaloka.student.ui.locale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yariksoffice.lingver.Lingver
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentLocaleBinding
import org.evidyaloka.student.ui.BaseFragment

class LocaleFragment : BaseFragment() {


    private lateinit var binding : FragmentLocaleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentLocaleBinding.inflate(layoutInflater, container, false)
        binding.btLocaleSelect.setOnClickListener {
            when (binding.rgLocales?.checkedItemId) {
                R.id.rb_english -> {
                    setNewLocale(CommonConst.LANGUAGE_ENGLISH)
                }
                R.id.rb_hindi -> {
                    setNewLocale(CommonConst.LANGUAGE_HINDI)
                }
                R.id.rb_tamil -> {
                    setNewLocale(CommonConst.LANGUAGE_TAMIL)
                }
                R.id.rb_kanada -> {
                    setNewLocale(CommonConst.LANGUAGE_KANNADA)
                }
                R.id.rb_telugu -> {
                    setNewLocale(CommonConst.LANGUAGE_TELUGU)
                }
                R.id.rb_marathi -> {
                    setNewLocale(CommonConst.LANGUAGE_MARATHI)
                }
            }
        }
        getCurrentLocale()
        return binding.root
    }

    private fun setNewLocale(language: String) {
        try {
            context?.let { Lingver.getInstance().setLocale(it, language, CommonConst.LANGUAGE_INDIA_COUNTRY) }
            activity?.let {
                navController.popBackStack()
                it.recreate()
            }
        } catch (e: Exception) {
        }
    }

    private fun getCurrentLocale() {
        when (Lingver.getInstance().getLanguage()) {
            CommonConst.LANGUAGE_ENGLISH -> {
                binding.rbEnglish.isChecked = true
            }
            CommonConst.LANGUAGE_HINDI -> {
                binding.rbHindi.isChecked = true
            }
            CommonConst.LANGUAGE_TAMIL -> {
                binding.rbTamil.isChecked = true
            }
            CommonConst.LANGUAGE_KANNADA -> {
                binding.rbKanada.isChecked = true
            }
            CommonConst.LANGUAGE_TELUGU -> {
                binding.rbTelugu.isChecked = true
            }
            CommonConst.LANGUAGE_MARATHI -> {
                binding.rbMarathi.isChecked = true
            }
            else -> binding.rbEnglish.isChecked = true
        }
    }
}