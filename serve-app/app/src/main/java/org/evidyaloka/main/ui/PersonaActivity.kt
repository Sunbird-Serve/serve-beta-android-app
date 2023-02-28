package org.evidyaloka.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_persona.*
import org.evidyaloka.BuildConfig
import org.evidyaloka.R
import org.evidyaloka.common.view.RecursiveRadioGroup
import org.evidyaloka.common.helper.startActivityFromString
import org.evidyaloka.common.util.FileReaderActivity
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.databinding.LayoutVolunteerPopupBinding
import org.evidyaloka.partner.ui.HomeActivity
import org.evidyaloka.student.ui.ParentExploreActivity

class PersonaActivity : AppCompatActivity() {

    private var selectedPersona = CommonConst.PersonaType.Parent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_persona)

        rg_persona?.setOnCheckedChangeListener(object :
            RecursiveRadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RecursiveRadioGroup?, checkedId: Int) {
                selectedPersona = when (rg_persona?.checkedItemId) {
                    R.id.rb_parent -> {
                        bt_join?.visibility = View.VISIBLE
                        CommonConst.PersonaType.Parent
                    }
                    R.id.rb_student -> {
                        bt_join?.visibility = View.GONE
                        CommonConst.PersonaType.Student
                    }
                    R.id.rb_teacher -> {
                        bt_join?.visibility = View.VISIBLE
                        CommonConst.PersonaType.Teacher
                    }
                    R.id.rb_partner -> {
                        bt_join?.visibility = View.VISIBLE
                        CommonConst.PersonaType.Partner
                    }
                    else -> {
                        bt_join?.visibility = View.VISIBLE
                        CommonConst.PersonaType.Parent
                    }
                }
            }
        })
        bt_login?.setOnClickListener {
            if (selectedPersona.equals(CommonConst.PersonaType.Parent)) {
                var intent = Intent(this@PersonaActivity, ParentExploreActivity::class.java)
                intent.putExtra(CommonConst.USER_TYPE, CommonConst.PersonaType.Parent.toString())
                intent.putExtra(CommonConst.USER_ACTION, CommonConst.UserActionType.Login.toString())
                startLoginActivity(intent)
            } else if (selectedPersona.equals(CommonConst.PersonaType.Teacher)) {
                Toast.makeText(this, getString(R.string.invalid_persona_selection), Toast.LENGTH_LONG).show()
            } else if (selectedPersona.equals(CommonConst.PersonaType.Student)) {
                var intent = Intent(this@PersonaActivity, ParentExploreActivity::class.java)
                intent.putExtra(CommonConst.USER_TYPE, CommonConst.PersonaType.Student.toString())
                intent.putExtra(CommonConst.USER_ACTION, CommonConst.UserActionType.Login.toString())
                startLoginActivity(intent)
            } else if (selectedPersona.equals(CommonConst.PersonaType.Partner)) {
                var intent = Intent(this@PersonaActivity, HomeActivity::class.java)
                intent.putExtra(CommonConst.USER_TYPE, CommonConst.PersonaType.Partner.toString())
                intent.putExtra(CommonConst.USER_ACTION, CommonConst.UserActionType.Login.toString())
                startLoginActivity(intent)
            } else{
                Toast.makeText(this, getString(R.string.invalid_persona_selection), Toast.LENGTH_LONG).show()
            }
        }

        bt_join?.setOnClickListener {
            if (selectedPersona.equals(CommonConst.PersonaType.Parent)) {
                val intent = Intent(this@PersonaActivity, ParentExploreActivity::class.java)
                intent.putExtra(CommonConst.USER_TYPE, CommonConst.PersonaType.Parent.toString())
                intent.putExtra(CommonConst.USER_ACTION, PartnerConst.UserActionType.Register.toString())
                startActivity(intent)
                finish()
            } else if (selectedPersona.equals(CommonConst.PersonaType.Partner)) {
                var intent = Intent(this@PersonaActivity, HomeActivity::class.java)
                intent.putExtra(CommonConst.USER_TYPE, CommonConst.PersonaType.Partner.toString())
                intent.putExtra(CommonConst.USER_ACTION, PartnerConst.UserActionType.Join.toString())
                startActivity(intent)
                finish()
            }
        }

        bt_volunteer.setOnClickListener {
            openVolunteerPopup()
        }
    }

    private fun startLoginActivity(intent : Intent){
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        startActivityFromString(CommonConst.LOCALE_ACTIVITY_PATH)
    }

    private fun openVolunteerPopup(){
        val binding =
            LayoutVolunteerPopupBinding.inflate(LayoutInflater.from(this), null, false)
        val alert = MaterialAlertDialogBuilder(this,
            R.style.MaterialAlertDialogTheme).setView(binding.root).create()
        binding.btRedirect.setOnClickListener {
            alert.dismiss()
            var bundle = Bundle().apply {
                putString("url", BuildConfig.VOLUNTEER_EXPLORE)
                putString("title", getString(R.string.volunteer_explore))
                putBoolean("hideDone",true)
            }
            var intent = Intent(this, FileReaderActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.btShare.setOnClickListener {
            alert.dismiss()
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.explore_volunteering_opportunities_with_eVidayaloka))
                putExtra(Intent.EXTRA_TEXT, BuildConfig.VOLUNTEER_EXPLORE)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_via))
            startActivity(shareIntent)
        }
        binding.btClose.setOnClickListener {
            alert.dismiss()
        }
        alert.show()
    }
}