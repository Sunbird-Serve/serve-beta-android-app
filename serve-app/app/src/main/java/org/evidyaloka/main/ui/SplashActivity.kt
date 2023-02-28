package org.evidyaloka.main.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.splash_animation.*
import kotlinx.coroutines.launch
import org.evidyaloka.R
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.ui.HomeActivity
import org.evidyaloka.student.ui.ParentExploreActivity


/**
 * SplashActivity class serves as the Splash screen for the application.
 */
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by viewModels()
    private val animatorSet = AnimatorSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_animation)

        val animStep1 = ObjectAnimator.ofFloat(step1, "alpha", 0f, 1f).apply { duration = 100; interpolator = AccelerateInterpolator() }
        val animStep2 = ObjectAnimator.ofFloat(step2, "alpha", 0f, 1f).apply { duration = 100; interpolator = AccelerateInterpolator() }
        val animStep3 = ObjectAnimator.ofFloat(step3, "alpha", 0f, 1f).apply { duration = 300; interpolator = AccelerateInterpolator() }
        val animStep4 = ObjectAnimator.ofFloat(step4, "alpha", 0f, 1f).apply { duration = 300; interpolator = AccelerateInterpolator() }
        val animStep5 = ObjectAnimator.ofFloat(step5, "alpha", 0f, 1f).apply { duration = 300; interpolator = AccelerateInterpolator() }
        val animStep6 = ObjectAnimator.ofFloat(step6, "alpha", 0f, 1f).apply { duration = 300; interpolator = AccelerateInterpolator() }
        val animStep7 = ObjectAnimator.ofFloat(step7, "alpha", 0f, 1f).apply { duration = 300; interpolator = AccelerateInterpolator() }
        val animStep8 = ObjectAnimator.ofFloat(step8, "alpha", 0f, 1f).apply { duration = 300; interpolator = AccelerateInterpolator() }
        val animStep9 = ObjectAnimator.ofFloat(step9, "alpha", 0f, 1f).apply { duration = 300; interpolator = AccelerateInterpolator() }
        val animStep10 = ObjectAnimator.ofFloat(step9, "alpha", 1f, 1f).apply { duration = 1000; interpolator = AccelerateInterpolator() }

        animatorSet.playSequentially(animStep1,animStep2,animStep3,animStep4,animStep5,animStep6,animStep7,animStep8,animStep9,animStep10)
        animatorSet.start()
        animatorSet.addListener (onEnd = {
            try {
                checkUserLogin()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })


//        val intent = Intent(this@SplashActivity,MainActivity::class.java)
//        startActivity(intent)
    }

    private fun checkUserLogin() {
        lifecycleScope.launch {
            viewModel.getUser().observe(this@SplashActivity, Observer { user ->
                if (user != null && user.id != 0 && (user.roles.contains(PartnerConst.RoleType.DSM.toString())
                            || user.roles.contains(PartnerConst.RoleType.DSP.toString()))) {
                    val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                    val bundle = Bundle()
                    bundle.putBoolean(CommonConst.IS_LOGED_IN, true)
                    if (user.roles.contains(PartnerConst.RoleType.DSP.name)) {
                        bundle.putSerializable(PartnerConst.ROLE, PartnerConst.RoleType.DSP.name)
                        if (user.roles.contains(PartnerConst.RoleType.DSM.name)) {
                            bundle.putSerializable(PartnerConst.DSP_HAS_DSM_ROLE, true)
                        }
                    } else {
                        bundle.putSerializable(PartnerConst.ROLE, PartnerConst.RoleType.DSM.name)
                    }
                    intent.putExtras(bundle)
                    startActivity(intent)
                } else if (user != null && user.id != 0 && user.roles.contains(CommonConst.PersonaType.Student.toString())) {
                    val intent = Intent(this@SplashActivity, ParentExploreActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString(CommonConst.USER_TYPE, CommonConst.PersonaType.Student.toString())
                    bundle.putString(CommonConst.USER_ACTION, CommonConst.UserActionType.Logged.toString())
                    intent.putExtras(bundle)
                    startActivity(intent)
                }else if (user != null && user.id != 0 && user.roles.contains(CommonConst.PersonaType.Parent.toString())) {
                    val intent = Intent(this@SplashActivity, ParentExploreActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString(CommonConst.USER_TYPE, CommonConst.PersonaType.Parent.toString())
                    bundle.putString(CommonConst.USER_ACTION, CommonConst.UserActionType.Logged.toString())
                    intent.putExtras(bundle)
                    startActivity(intent)
                } else {
                    startActivity(Intent(this@SplashActivity, LocaleActivity::class.java))
                }
                finish()

            })
        }
    }



    override fun onPause() {
        super.onPause()
        try {
            animatorSet.cancel()
        } catch (e: Exception) {
        }
    }

}