package org.evidyaloka.main.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yariksoffice.lingver.Lingver
import org.evidyaloka.R
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.databinding.ActivityLocaleBinding

class LocaleActivity : AppCompatActivity() {
    lateinit var binding: ActivityLocaleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }

    private fun setNewLocale(language: String) {
        Lingver.getInstance().setLocale(this, language, CommonConst.LANGUAGE_INDIA_COUNTRY)
        startActivity(
            Intent(
                this@LocaleActivity,
                PersonaActivity::class.java
            )
        )
        finish()
    }
}