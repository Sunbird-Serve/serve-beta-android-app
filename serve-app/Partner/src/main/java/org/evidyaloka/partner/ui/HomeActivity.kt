package org.evidyaloka.partner.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.WebViewLocaleHelper
import org.evidyaloka.common.helper.startActivityFromString
import org.evidyaloka.partner.R
import org.evidyaloka.common.interfaces.IOnBackPressed
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Settings
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.partner.NavGraphDirections
import org.evidyaloka.partner.ui.auth.LoginFragment
import org.evidyaloka.common.view.SuccessDialogFragment
import org.evidyaloka.core.partner.model.ExploreData
import org.evidyaloka.partner.BuildConfig

/*
    This class will serve as the Home Activity for the application
     */

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "HomeActivity"

    private val viewModel: HomeActivityViewModel by viewModels()

    /*

    This class will serve as the Home Activity for the application
     */

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var _user: User
    private var userType: PartnerConst.RoleType = PartnerConst.RoleType.DSP
    private lateinit var navView: NavigationView
    private lateinit var dialog: AlertDialog
    private lateinit var dialogFragment: SuccessDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        try {
            setSupportActionBar(toolbar)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button)
            toolbar?.setNavigationIcon(R.drawable.ic_nav_menu)
            drawerLayout = findViewById(R.id.drawer_layout)
            navView = findViewById(R.id.nav_view)
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            NavigationUI.setupWithNavController(toolbar, navController)
            if (this::navView.isInitialized) {
                navView.setupWithNavController(navController)
                navView.bringToFront()
                navView.setNavigationItemSelectedListener(this)
            }
            initListner()
            setLogoutListenerAndAppVersionUI()
            checkUserAndNavigate()
            viewModel.getProgressObserable().observe(this, Observer {
                progress_circular?.visibility = if (it) View.VISIBLE else View.GONE
            })

            viewModel.getNetworkErrorObserable().observe(this, Observer {
                showNetworkErrorDialog(viewModel::retry)
            })

            viewModel.mErrorObservable?.observe(this, Observer {
                when (it?.code) {
                    401 -> {
                        logoutuser()
                    }
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            WebViewLocaleHelper(this).implementWorkaround()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        initLanguageDrawable()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        try {
            when (item.itemId) {
                R.id.nav_users -> {
                    viewModel.getSettings().observe(this, Observer {
                        it?.let {
                            if (PartnerConst.PartnerStatus.Approved.name.equals(it.partnerStatus)) {
                                try {
                                    navController.navigate(R.id.action_global_usersFragment)
                                } catch (e: Exception) {
                                    FirebaseCrashlytics.getInstance().recordException(e)

                                }
                            } else {
                                showSnackBar(getString(R.string.partner_not_approved))
                            }
                        }
                    })
                }
                R.id.nav_log_issues -> {
                    try {
                        val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
                        emailSelectorIntent.data = Uri.parse("mailto:")
                        val emailIntent = Intent(Intent.ACTION_SEND)
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@evidyaloka.org"))
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Issues log")
                        emailIntent.selector = emailSelectorIntent
                        if (emailIntent.resolveActivity(packageManager) != null) startActivity(
                            emailIntent
                        )
                    } catch (e: Exception) {

                    }
                }
                R.id.nav_p_lounge -> {
                    var url = "${BuildConfig.P_LOUNGE}${_user.partnerId}"
                    val navigate = NavGraphDirections.commonNavigation(
                        url, getString(
                            R.string.menu_faq
                        )
                    )
                    try{
                        navController.navigate(navigate)
                    }catch (e : Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
                R.id.nav_about_us -> {
                    val navigate = NavGraphDirections.commonNavigation(
                        BuildConfig.ABOUT_US_URL,
                        getString(R.string.menu_dashboard_about_us),
                        true
                    )
                    try {
                        navController.navigate(navigate)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
                R.id.nav_contact_us -> {
                    val navigate = NavGraphDirections.commonNavigation(
                        BuildConfig.CONTACT_US_URL,
                        getString(R.string.menu_dashboard_contact_us),
                        true
                    )
                    try {
                        navController.navigate(navigate)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
                R.id.nav_locale -> {
                    navController.navigate(R.id.action_global_localeFragment)
                }
                R.id.nav_faq_dsp,
                R.id.nav_faq_dsm -> {
                    val user =
                        if (item.itemId == R.id.nav_faq_dsp) "Digital%20School%20Partner" else "Digital%20School%20Manager"
                    var url = "${BuildConfig.FAQ}${user}&lan=${Lingver.getInstance().getLanguage()}"
                    val navigate = NavGraphDirections.commonNavigation(
                        url, getString(
                            R.string.menu_dashboard_p_lounge
                        ),
                        true
                    )
                    try {
                        navController.navigate(navigate)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onBackPressed() {
        try {
            val navController = findNavController(R.id.nav_host_fragment)
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

            if (navController?.currentDestination?.id?.equals(R.id.loginFragment) == true) {
                hideToolbar()
                viewModel.getUser().observe(this@HomeActivity, Observer {
                    it?.let {
                        super.onBackPressed()
                    } ?: startActivityFromString(CommonConst.PERSONA_ACTIVITY_PATH)
                })
            } else if (navController?.currentDestination?.id?.equals(R.id.joinFragment) == true) {
                startActivityFromString(CommonConst.PERSONA_ACTIVITY_PATH)
            } else {
                navHostFragment?.childFragmentManager?.fragments?.get(0).let {
                    (it as? IOnBackPressed)?.onBackPressed()?.let { isHandled ->
                        if (!isHandled) {
                            if (!navController?.popBackStack()) {
                                super.onBackPressed()
                            }
                        }
                    }
                } ?: super.onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkUserAndNavigate() {
        viewModel.getUser().observe(this, Observer { user ->
            if (user != null && user.id != 0 && (user.roles.contains(PartnerConst.RoleType.DSM.toString())
                        || user.roles.contains(PartnerConst.RoleType.DSP.toString()))
            ) {
                if (user.roles.contains(PartnerConst.RoleType.DSP.toString()) == true) {
                    try {
                        if (user.partnerStatus.contains(PartnerConst.UserStatus.Lead.toString())) {
                            navController.navigate(
                                NavGraphDirections.actionGlobalPartnerExploreSchoolBoard(
                                    ExploreData(user = user)
                                )
                            )
                        } else {
                            navController.navigate(R.id.action_global_dspHomeFragment)
                        }

                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                    if (user.roles.contains(PartnerConst.RoleType.DSM.name)) {
                        showNavHeaderRoleToggleButton()
                    }
                } else
                    if (user.roles.contains(PartnerConst.RoleType.DSM.toString())
                    ) {
                        val navigationView =
                            findViewById<View>(R.id.nav_view) as NavigationView
                        navigationView.setNavigationItemSelectedListener(this)
                        val menu = navigationView.menu
                        val target = menu.findItem(R.id.nav_users)
                        target?.isVisible = false
                        try {
                            navController.navigate(R.id.action_global_dsmHomeFragment)
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }
                    }
            } else if (PartnerConst.UserActionType.Join.toString()
                    .equals(intent?.extras?.getString(CommonConst.USER_ACTION)?.toString())
            ) {
                try {
                    navController.navigate(R.id.joinFragment)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)

                }
            } else {
                try {
                    navController.navigate(R.id.action_global_loginFragment)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)

                }
            }
        })
    }

    fun showNavHeaderRoleToggleButton() {
        try {
            val roleToggler = ((nav_view.getHeaderView(0) as ViewGroup).getChildAt(1) as RadioGroup)
            roleToggler?.visibility = View.VISIBLE
            roleToggler?.check(R.id.rb_partner)
            roleToggler?.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb_dsm -> {
                        drawer_layout.closeDrawers()
//                    roleToggler?.visibility = View.GONE
                        try {
                            navController.navigate(R.id.action_global_dsmHomeFragment)
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    }
                    R.id.rb_partner -> {
                        drawer_layout.closeDrawers()
                        try {
                            navController.navigate(R.id.action_global_dspHomeFragment)
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideNavHeaderRoleToggleButton() {
        try {
            val roleToggler = ((nav_view.getHeaderView(0) as ViewGroup).getChildAt(1) as RadioGroup)
            roleToggler?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadDSPMenu() {
        try {
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.dsp_side_bar_menu)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadDSMMenu() {
        try {
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.dsm_side_bar_menu)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setLogoutListenerAndAppVersionUI() {
        try {
            nav_logout?.setOnClickListener {
                var map: HashMap<String, Any> = hashMapOf()
                try {
                    Utils.getDeviceInfo(this, "PARTNER")?.let {
                        map.put("deviceInfo", it)
                    }

                } catch (e: Exception) {
                    Log.e(TAG, e.message ?: "gson converter exception")
                }
                viewModel.logout(map).observe(this, Observer { response ->
                    hideProgressCircularBar()
                    logoutuser()
                })
            }
            tv_app_version?.text =
                getString(R.string.app_version).plus(" ").plus(BuildConfig.VersionName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun logoutuser() {
        try {
            viewModel.clearUser().observe(this, Observer {
                if (!(navController.currentDestination as FragmentNavigator.Destination)?.className.equals(
                        LoginFragment::class.qualifiedName
                    )
                ) {
                    lockDrawer()
                    try {
                        navController.navigate(R.id.action_global_loginFragment)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleGenericError(errorCode: Int) {
        when (errorCode) {
            102 -> {
                //Access Denied; most probably because of invalid session ID
            }
        }
    }

    fun showToolbar() {
        toolbar?.visibility = View.VISIBLE
    }

    fun hideToolbar() {
        toolbar?.visibility = View.GONE
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
            e.printStackTrace()
        }
    }

    fun showSuccessDialog(title: String, message: String): AlertDialog {
        //TODO Use DialogFragment instead of dialog, can cause memory leaks, else needs to handle it manually every time
        val alertLayout: View = layoutInflater.inflate(R.layout.custom_dialog, null)
        alertLayout.findViewById<TextView>(R.id.dlg_title)?.text = title
        alertLayout.findViewById<TextView>(R.id.dlg_message)?.text = message
        dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
            .setView(alertLayout).setCancelable(true).create()
        return dialog
    }

    fun showNetworkErrorDialog(click: () -> Unit) {
        dialogFragment = SuccessDialogFragment.Builder(this)
            .setIsDialogCancelable(true)
            .setTitle(getString(R.string.no_internet_connection))
            .setDescription(getString(R.string.you_do_not_have_an_active_internet_connection_please_check_your_internet_connection_and_retry))
            .setIcon(R.drawable.ic_network_issue)
            .setViewType(SuccessDialogFragment.DIALOG_TYPE.FULL_SCREEN)
            .setButtonPositiveText(getString(R.string.retry))
            .setOnClickListner(View.OnClickListener {
                if (Utils.isNetworkAvailable(this)) {
                    dialogFragment.dismiss()
                    click.invoke()
                }
            })
            .build()
        dialogFragment.show(supportFragmentManager, "")
    }

    private fun setUpNavHeader() {
        try {
            val headerView = nav_view.getHeaderView(0)
            val userName = headerView.findViewById<TextView>(R.id.tv_toolbar_name)
            userName?.setText(_user.fname + " " + _user.lname)
            val userMail = headerView.findViewById<TextView>(R.id.tv_toolbar_email)
            userMail?.setText(_user.email)
            //Todo use logoID to show digitalSchool logo from Base url once API is live
            var imageUrl = ""
            /*if(!user.profileImageUrl.isNullOrEmpty()){
                imageUrl = user.profileImageUrl
            }*/

            if (!_user.profileImageShortUrl.isNullOrEmpty()) {
                imageUrl = _user.profileImageShortUrl
            } else if (!_user.profileImageFullUrl.isNullOrEmpty()) {
                imageUrl = _user.profileImageFullUrl
            }

            Glide.with(this@HomeActivity)
                .load(imageUrl)
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .circleCrop()
                .into(nav_view.getHeaderView(0).findViewById<ImageView>(R.id.iv_toolbar_profile))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun lockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun unlockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    /*
    This function is to open Profile Fragment and close Drawer;
    Since Profile is a not part of Menu, need to handle it differently
     */
    private fun initListner() {
        try {
            nav_view.getHeaderView(0).setOnClickListener {
                try {
                    navController.navigate(R.id.action_global_profileFragment);
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)

                }
                drawer_layout.closeDrawers()
            }
            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                hideKeyboard()
                when (destination.id) {
                    R.id.dspHomeFragment -> {
                        getUserDetails()
                        loadDSPMenu()
                        loadPingAPI()
                        initLanguageDrawable()
                        supportActionBar!!.setHomeButtonEnabled(true)
                        toolbar.setNavigationIcon(R.drawable.ic_nav_menu)
                        toolbar.setNavigationOnClickListener {
                            drawerLayout.openDrawer(GravityCompat.START)
                        }

                        nav_view.getHeaderView(0).setOnClickListener {
                            var bundle = Bundle()
                            bundle.putSerializable(PartnerConst.ROLE, PartnerConst.RoleType.DSP)
                            try {
                                navController.navigate(R.id.action_global_profileFragment, bundle);
                            } catch (e: Exception) {
                                FirebaseCrashlytics.getInstance().recordException(e)

                            }
                            drawer_layout.closeDrawers()
                        }
                    }
                    R.id.dsmHomeFragment -> {
                        getUserDetails()
                        loadDSMMenu()
                        loadPingAPI()
                        initLanguageDrawable()
                        supportActionBar!!.setHomeButtonEnabled(true)
                        toolbar.setNavigationIcon(R.drawable.ic_nav_menu)
                        toolbar.setNavigationOnClickListener {
                            drawerLayout.openDrawer(GravityCompat.START)
                        }

                        nav_view.getHeaderView(0).setOnClickListener {
                            var bundle = Bundle()
                            bundle.putSerializable(PartnerConst.ROLE, PartnerConst.RoleType.DSM)
                            try {
                                navController.navigate(R.id.action_global_profileFragment, bundle);
                            } catch (e: Exception) {
                                FirebaseCrashlytics.getInstance().recordException(e)

                            }
                            drawer_layout.closeDrawers()
                        }
                    }

                    R.id.joinFragment, R.id.loginFragment -> {
                        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button)
                        toolbar?.setNavigationOnClickListener {
                            startActivityFromString(CommonConst.PERSONA_ACTIVITY_PATH)
                        }
                    }

                    R.id.localeFragment, R.id.usersFragment -> {
                        toolbar.setNavigationIcon(R.drawable.ic_nav_menu)
                        toolbar.setNavigationOnClickListener {
                            drawerLayout.openDrawer(GravityCompat.START)
                        }
                    }

                    R.id.customWebView -> {
                        arguments?.getString("title")?.let{ updateToolbarTitle(it) }
                        if (arguments?.getBoolean("isHome") == true) {
                            toolbar.setNavigationIcon(R.drawable.ic_nav_menu)
                            toolbar.setNavigationOnClickListener {
                                drawerLayout.openDrawer(GravityCompat.START)
                            }
                        }
                    }

                    else -> {
                        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button)
                        toolbar?.setNavigationOnClickListener {
                            onBackPressed()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getUserDetails() {
        try {
            viewModel.getUser().observe(this, Observer { user ->
                user?.let {
                    _user = it
                    setUpNavHeader()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //To update title dynamically
    fun updateToolbarTitle(title: String) {
        try {
            supportActionBar!!.title = title
        } catch (e: Exception) {
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
            e.printStackTrace()
        }
    }

    /**
     * Create generic network error snackbar
     */
    fun handleNetworkError() {
        showSnackBar(getString(R.string.err_network))
    }

    fun showProgressCircularBar() {
        progress_circular?.visibility = View.VISIBLE
    }

    fun hideProgressCircularBar() {
        progress_circular?.visibility = View.GONE
    }

    fun showHorizontalDeterminateBar() {
        progress_horizontal?.visibility = View.VISIBLE
        progress_horizontal?.progress = 1
    }

    fun updateHorizontalDeterminateBar(progress: Int) {
        progress_horizontal?.progress = progress
    }

    fun hideHorizontalDeterminateBar() {
        progress_horizontal?.visibility = View.GONE
    }

    private fun loadPingAPI() {
        try {
            viewModel.ping().observe(this, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressCircularBar()
                        response.data?.let { settings ->
                            updateUsingPingData(settings)
                        }
                    }

                    is Resource.GenericError -> {
                        response.error?.let {
                            handleGenericError(it.code)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUsingPingData(settings: Settings) {
        try {
            viewModel.saveSettings(settings).observe(this, Observer {

            })

            //TODO remove session and settings storage in User object
            viewModel.getUser().observe(this, Observer {

                it?.let {

                    it.apply {
                        userStatus = settings.userStatus
                        partnerStatus = settings.partnerStatus
                        partnerId = settings.partnerId
                        id = settings.userId
                        sessionId = settings.sessionId
                    }

                    viewModel.saveUser(it)

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun initLanguageDrawable() {
        val tvLanguage: TextView =
            MenuItemCompat.getActionView(navView.menu.findItem(R.id.nav_locale)) as TextView
        tvLanguage?.let {
            it.gravity = Gravity.CENTER_VERTICAL
            it.setTypeface(null, Typeface.BOLD)
            it.setTextColor(getResources().getColor(R.color.menu_locale_color))

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
}