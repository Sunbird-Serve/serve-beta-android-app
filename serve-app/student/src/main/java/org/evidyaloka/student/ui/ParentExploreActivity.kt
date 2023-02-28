package org.evidyaloka.student.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.WebViewLocaleHelper
import org.evidyaloka.common.helper.startActivityFromString
import org.evidyaloka.common.view.webView.CustomWebViewArgs
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.student.BuildConfig
import org.evidyaloka.student.R
import org.evidyaloka.student.StudentListNavigationDirections
import org.evidyaloka.student.databinding.ActivityStudentListBinding
import org.evidyaloka.student.ui.home.HomeViewModel
import java.util.HashMap

@AndroidEntryPoint
class ParentExploreActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "StudentListActivity"
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var binding: ActivityStudentListBinding
    private lateinit var toolbar: Toolbar
    private lateinit var appBar: AppBarLayout
    private var userType: String? = null
    private var userAction: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent?.extras

        userType = bundle?.getString(CommonConst.USER_TYPE, null)
        userAction = bundle?.getString(CommonConst.USER_ACTION, null)

        binding = ActivityStudentListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            appBar = binding.appbarLayout.appbar
            toolbar = binding.appbarLayout.toolbar
            toolbar.setNavigationIcon(R.drawable.ic_menu)
            setSupportActionBar(toolbar)
            getSupportActionBar()?.setDisplayShowTitleEnabled(false)
            drawerLayout = binding.drawerStudentLayout
            navView = binding.navStudentView
            navView.menu.apply {
                removeItem(R.id.nav_home)
                removeItem(R.id.nav_school)
                removeItem(R.id.nav_doubts)
                removeItem(R.id.nav_learn_tv)
                removeItem(R.id.nav_faq)
                removeItem(R.id.nav_bookmark_classes)
            }
            val headerView = navView.getHeaderView(0)
            headerView.findViewById<LinearLayout>(R.id.ll_header).visibility = View.GONE
            var headerImage = headerView.findViewById<ImageView>(R.id.iv_toolbar_profile)

            (headerImage.parent as ViewGroup)?.apply {
                removeAllViews()
                addView(headerImage)
            }

            val layoutParams: ConstraintLayout.LayoutParams =
                ConstraintLayout.LayoutParams(150, 150)
            headerImage.setLayoutParams(layoutParams)
            headerImage.setImageResource(R.drawable.ic_logo)
            initLanguageDrawable()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }


        try {
            if (userType?.toString().equals(CommonConst.PersonaType.Student.toString())) {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            } else {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_parent_back)
            }
        } catch (e: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }

        try {
            val navHostFragment =
                this.supportFragmentManager.findFragmentById(R.id.nav_student_hos) as NavHostFragment
            navController = navHostFragment.navController
            var destination = R.id.fragmentLogin
            userAction?.let {
                if (it.equals(CommonConst.UserActionType.Login.toString())) {
                    destination = R.id.fragmentLogin
                } else if (it.equals(CommonConst.UserActionType.Register.toString())) {
                    destination = R.id.fragmentRegister
                } else if (it.equals(CommonConst.UserActionType.Logged.toString())) {
                    destination = R.id.nav_student_list_home
                } else if (it.equals(CommonConst.UserActionType.Registered.toString())) {
                    destination = R.id.fragmentLocationAccess
                } else {
                    destination = R.id.fragmentLogin
                }
            }

            val graphInflater = navHostFragment.navController.navInflater
            var navGraph = graphInflater.inflate(R.navigation.student_list_navigation)
            navController = navHostFragment.navController

            navGraph.startDestination = destination

            var bundle = Bundle()
            bundle.putString(CommonConst.USER_TYPE, userType)
            bundle.putString(CommonConst.USER_ACTION, userAction)

            navController.setGraph(navGraph, bundle)

            binding.appbarLayout.contentLayout.bottomNav.setupWithNavController(navController)
            if (this::navView.isInitialized) {
                navView.setupWithNavController(navController)
                navView.bringToFront()
                navView.setNavigationItemSelectedListener(this)
                navView.itemIconTintList = null
            }
            binding.appbarLayout.contentLayout.bottomNav.visibility = View.GONE
            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                lockDrawer()
                if (destination.id === R.id.customWebView) {
                    try {
                        val title = CustomWebViewArgs.fromBundle(arguments as Bundle).title
                        binding.appbarLayout.toolbarTitle.text = title.toString()
                    } catch (e: Exception) {

                    }
                } else {
                    binding.appbarLayout.toolbarTitle.text = destination.label
                }
                updateToolbarTitleColor(R.color.colorOnPrimary)
                updateUiStyle()
                when (destination.id) {
                    R.id.nav_student_list_home -> {
                        unlockDrawer()
                        toolbar.setNavigationIcon(R.drawable.ic_menu)
                        toolbar.setNavigationOnClickListener {
                            drawerLayout.openDrawer(GravityCompat.START)
                        }
                    }
                    R.id.fragmentLogin, R.id.fragmentRegister -> {
                        toolbar?.setNavigationOnClickListener {
                            startActivityFromString(CommonConst.PERSONA_ACTIVITY_PATH)
                        }
                    }
                    else -> {
                        toolbar.setNavigationIcon(R.drawable.ic_parent_back)
                        toolbar?.setNavigationOnClickListener {
                            onBackPressed()
                        }
                    }
                }
            }
            drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {

                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    val marginLeft = 450 * slideOffset
                    val marginRight =
                        40 - (slideOffset * 100) // bunny will move 50 to -50dp based on offset
                    val lp: FrameLayout.LayoutParams =
                        binding.bunnyFlying.layoutParams as FrameLayout.LayoutParams
                    lp.marginEnd = marginRight.toInt()
                    lp.marginStart = marginLeft.toInt()
                    binding.bunnyFlying.layoutParams = lp
                }

                override fun onDrawerClosed(drawerView: View) {}
                override fun onDrawerOpened(drawerView: View) {}
                override fun onDrawerStateChanged(newState: Int) {}
            })

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }

        try {
            WebViewLocaleHelper(this).implementWorkaround()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        try {
            setToolbarTitleGravity(Gravity.START)
            when (item.itemId) {
                R.id.nav_logout -> {
                    var map = HashMap<String, Any>().apply {
                        Utils.getDeviceInfo(this@ParentExploreActivity, userType)?.let {
                            put("deviceInfo", it)
                        }
                    }
                    viewModel.logout(map).observe(this, Observer { response ->
                        logoutuser()
                    })
                }

                R.id.nav_terms_of_use -> {

                    setToolbarTitleGravity(Gravity.CENTER)

                    val navigate = StudentListNavigationDirections.commonNavigation(
                        BuildConfig.TERMS_AND_CONDITIONS_URL, getString(
                            R.string.menu_terms_of_use
                        )
                    )
                    try {
                        navController.navigate(navigate)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }

                    toolbar?.setNavigationOnClickListener {
                        onBackPressed()
                    }
                }
                R.id.nav_privacy_policy -> {

                    setToolbarTitleGravity(Gravity.CENTER)

                    val navigate = StudentListNavigationDirections.commonNavigation(
                        BuildConfig.PRIVACY_POLICY_URL, getString(
                            R.string.menu_privacy_policy
                        )
                    )
                    try {
                        navController.navigate(navigate)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                    toolbar?.setNavigationOnClickListener {
                        onBackPressed()
                    }
                }

                R.id.nav_support -> {
                    try {
                        val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
                        emailSelectorIntent.data = Uri.parse("mailto:")
                        val emailIntent = Intent(Intent.ACTION_SEND)
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@evidyaloka.org"))
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support")
                        emailIntent.selector = emailSelectorIntent
                        if (emailIntent.resolveActivity(packageManager) != null) startActivity(
                            emailIntent
                        )
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
                R.id.nav_feedback -> {
                    try {
                        val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
                        emailSelectorIntent.data = Uri.parse("mailto:")
                        val emailIntent = Intent(Intent.ACTION_SEND)
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@evidyaloka.org"))
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
                        emailIntent.selector = emailSelectorIntent
                        if (emailIntent.resolveActivity(packageManager) != null) startActivity(
                            emailIntent
                        )
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
                R.id.nav_locale -> {
                    navController.navigate(R.id.action_global_localeFragment)
                }
                R.id.nav_download -> {
                    navController.navigate(R.id.downloadFragment)
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    fun lockDrawer() {
        try {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } catch (e: Exception) {
        }
    }

    fun unlockDrawer() {
        try {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } catch (e: Exception) {
        }
    }

    fun openDrawer() {
        try {
            if (this::drawerLayout.isInitialized) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun setToolbarTitle(title: String) {
        try {
            binding.appbarLayout.toolbarTitle.text = title
        } catch (e: Exception) {
        }
    }

    fun setToolbarTitleGravity(gravity: Int? = null) {
        try {
            var relativeGravity = when (gravity) {

                Gravity.START -> {
                    Gravity.START
                }
                Gravity.CENTER -> {
                    Gravity.CENTER
                }
                Gravity.END -> {
                    Gravity.END
                }
                else -> {
                    Gravity.START
                }
            }

            var params = Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT
            )
            params.gravity = relativeGravity
            (binding.appbarLayout.toolbarTitle as TextView).layoutParams = params
        } catch (e: Exception) {
        }
    }

    fun getToolBarTitle(): View {
        return binding.appbarLayout.toolbarTitle
    }

    fun showToolbar() {
        try {
            if (this::appBar.isInitialized) {
                appBar?.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun hideToolbar() {
        try {
            if (this::appBar.isInitialized) {
                appBar?.visibility = View.GONE
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun hideKeyboard() {
        try {
            val view: View = this.findViewById(android.R.id.content)
            if (view != null) {
                val imm: InputMethodManager =
                    this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    //To update title dynamically
    fun updateToolbarTitle(title: String) {
        try {
            supportActionBar!!.title = title
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /**
     * This function will be responsible to create snackbar and display to userEntity
     * @param message String text to show in snackbar
     * @param action String Action button text
     * @param listener action button click listener, if null default action button listener will be created
     */
    fun showSnackBar(
        message: String,
        action: String? = null,
        listener: View.OnClickListener? = null
    ) {
        try {
            val snackBar =
                Snackbar.make(
                    this.findViewById(android.R.id.content),
                    message,
                    Snackbar.LENGTH_LONG
                )
            action?.let {
                snackBar.setAction(action, listener ?: View.OnClickListener {
                    snackBar.dismiss()
                })
            }
            snackBar.show()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }


    /**
     * Create generic network error snackbar
     */
    fun handleNetworkError() {
        showSnackBar(getString(R.string.err_network))
    }


    fun hideBottomNavigation() {
        try {
            binding.appbarLayout.contentLayout.bottomNav.visibility = View.GONE
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun showBottomNavigation() {
        try {
            binding.appbarLayout.contentLayout.bottomNav.visibility = View.VISIBLE
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun updateToolbarTitleColor(id: Int) {
        try {
            binding.appbarLayout.toolbarTitle.setTextColor(resources.getColor(id))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun updateUiStyle() {
        try {
            var background = userType?.let {
                when (it) {
                    CommonConst.PersonaType.Parent.toString() -> {
                        R.drawable.ic_background
                    }
                    CommonConst.PersonaType.Student.toString() -> {
                        R.drawable.ic_student_bg
                    }
                    else -> {
                        R.drawable.ic_background
                    }
                }
            } ?: R.drawable.ic_background

            binding.appbarLayout.layoutRoot.apply {
                setBackgroundResource(background)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
    fun updateUiStyle(background: Int? = null) {
        try {
            binding.appbarLayout.layoutRoot.apply {
                background?.let {
                    setBackgroundResource(it)
                } ?: setBackgroundResource(R.drawable.ic_background)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun hideNavigationIcon() {
        //Todo hide backbutton
        //If the toolbar is hidden then it can cause crash
        try {
            if (this::toolbar.isInitialized) {
                toolbar?.setNavigationIcon(null)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun logoutuser() {
        try {
            viewModel.clearUser().observe(this, Observer {
                finish()
                startActivityFromString("org.evidyaloka.main.ui.SplashActivity")
            })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun initLanguageDrawable() {
        val tvLanguage: TextView =
            MenuItemCompat.getActionView(navView.menu.findItem(R.id.nav_locale)) as TextView
        tvLanguage?.let {
            it.gravity = Gravity.CENTER_VERTICAL
            it.setTypeface(null, Typeface.BOLD)
            it.setTextColor(getResources().getColor(R.color.student_orange))

            it.text = when (Lingver.getInstance().getLanguage()) {
                CommonConst.LANGUAGE_ENGLISH -> {
                    resources.getString(R.string.english)
                }
                CommonConst.LANGUAGE_HINDI -> {
                    resources.getString(R.string.hindi)
                }
                CommonConst.LANGUAGE_TAMIL -> {
                    resources.getString(R.string.tamil)
                }
                CommonConst.LANGUAGE_KANNADA -> {
                    resources.getString(R.string.kanada)
                }
                CommonConst.LANGUAGE_TELUGU -> {
                    resources.getString(R.string.telugu)
                }
                CommonConst.LANGUAGE_MARATHI -> {
                    resources.getString(R.string.marathi)
                }
                else -> resources.getString(R.string.english)
            }

        }
    }

    override fun onBackPressed() {
        if(navController?.currentDestination?.id == (R.id.fragmentRegister) || navController?.currentDestination?.id ==  R.id.fragmentLogin) {
            startActivityFromString(CommonConst.PERSONA_ACTIVITY_PATH)
        }else if(navController?.currentDestination?.id == (R.id.student_list_navigation) ){
            finish()
        }
        else{
            super.onBackPressed()
        }
    }
}