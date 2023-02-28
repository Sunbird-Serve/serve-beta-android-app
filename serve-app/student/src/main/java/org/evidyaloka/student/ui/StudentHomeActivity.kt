package org.evidyaloka.student.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.helper.WebViewLocaleHelper
import org.evidyaloka.common.helper.loadUrlWithGlide
import org.evidyaloka.common.helper.startActivityFromString
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.view.SuccessDialogFragment
import org.evidyaloka.common.view.webView.CustomWebViewArgs
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.student.BuildConfig
import org.evidyaloka.student.MobileNavigationDirections
import org.evidyaloka.student.R
import org.evidyaloka.student.StudentListNavigationDirections
import org.evidyaloka.student.databinding.ActivityStudentHomeBinding
import org.evidyaloka.student.ui.home.HomeFragmentDirections
import org.evidyaloka.student.ui.home.HomeViewModel
import org.evidyaloka.student.ui.rtc.RtcViewFragmentArgs
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class StudentHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var binding: ActivityStudentHomeBinding
    private lateinit var toolbar: Toolbar
    private lateinit var appBar: AppBarLayout
    private lateinit var progress_circular: ProgressBar
    private var studentList: List<Student> = listOf()
    private var mPhotoFile: File? = null
    private lateinit var dialogFragment: SuccessDialogFragment
    private var isDeepLink: Boolean = false
    private var missedDate: Date = Calendar.getInstance().time


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateUiStyle()
        try {
            appBar = binding.appbarLayout.appbar
            toolbar = binding.appbarLayout.toolbar
            toolbar.setNavigationIcon(R.drawable.ic_menu)
            setSupportActionBar(toolbar)
            getSupportActionBar()?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            drawerLayout = binding.drawerStudentLayout
            navView = binding.navStudentView
            initLanguageDrawable()
        } catch (e: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        try {

            val navHostFragment =
                this.supportFragmentManager.findFragmentById(R.id.nav_student_hos) as NavHostFragment
            navController = navHostFragment.navController
            var navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
            navController.graph = navGraph
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
//            appBarConfiguration = AppBarConfiguration.Builder(setOf(
//                R.id.scheduleCourseFragment,
//                R.id.nav_home,
//                R.id.nav_gallery
//            )).setDrawerLayout(drawerLayout).build()
//
            binding.appbarLayout.contentLayout.bottomNav.setupWithNavController(navController)
            if (this::navView.isInitialized) {
                navView.setupWithNavController(navController)
                navView.bringToFront()
                navView.setNavigationItemSelectedListener(this)
                navView.itemIconTintList = null
            }
            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                if (destination.id !== R.id.nav_home) {
                    binding.appbarLayout.contentLayout.llMissedClasses.visibility = View.GONE
                }
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
                    R.id.nav_home,
                    R.id.scheduleCourseFragment -> {
                        toolbar.setNavigationIcon(R.drawable.ic_menu)
                        toolbar.setNavigationOnClickListener {
                            drawerLayout.openDrawer(GravityCompat.START)
                        }
                        binding.appbarLayout.contentLayout.bottomNav.visibility = View.VISIBLE
                        if (destination.id === R.id.scheduleCourseFragment) {
                            binding.appbarLayout.contentLayout.bottomNav.visibility = View.GONE
                        }
                    }
                    R.id.classListFragment -> {
                        binding.appbarLayout.contentLayout.bottomNav.visibility = View.VISIBLE
                        toolbar.setNavigationIcon(R.drawable.ic_menu_black)
                        toolbar.setNavigationOnClickListener {
                            drawerLayout.openDrawer(GravityCompat.START)
                        }
                    }
                    R.id.courseTimetableFragment,
                    R.id.subTopicListFragment -> {
                        binding.appbarLayout.contentLayout.bottomNav.visibility = View.VISIBLE
                        toolbar.setNavigationIcon(R.drawable.ic_back_button_st)
                        toolbar?.setNavigationOnClickListener {
                            onBackPressed()
                        }
                    }
                    R.id.rtcViewFragment -> {
                        arguments?.let {
                            var args = RtcViewFragmentArgs.fromBundle(it)
                            isDeepLink = args.isDeepLink
                        }
                            binding.appbarLayout.contentLayout.bottomNav.visibility = View.VISIBLE
                        toolbar.setNavigationIcon(R.drawable.ic_back_button_st)
                        toolbar?.setNavigationOnClickListener {
                            onBackPressed()
                        }
                    }
                    else -> {
                        binding.appbarLayout.contentLayout.bottomNav.visibility = View.GONE
                        toolbar.setNavigationIcon(R.drawable.ic_back_button_st)
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

            viewModel.getProgressObserable().observe(this, Observer {
                binding.appbarLayout.progressCircular.visibility =
                    if (it) View.VISIBLE else View.GONE
            })

            viewModel.getStudentList().observe(this, Observer {
                studentList = it.filter { it.status == StudentConst.Status.Active.toString() }
                val adapter = ArrayAdapter(
                    this,
                    R.layout.spinner_list_item,
                    studentList.map { it.name })
                val headerView = navView.getHeaderView(0)
                val userName = headerView.findViewById<Spinner>(R.id.tv_toolbar_name)
                userName?.setAdapter(adapter)
                userName?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        drawerLayout?.closeDrawer(GravityCompat.START)
                        viewModel.setSelectedStudent(studentList.get(position))
                            .observe(this@StudentHomeActivity,
                                Observer { })
                    }

                })
                adapter.notifyDataSetChanged()
            })
            viewModel.getSelectedStudent().observe(this, Observer {
                setUpNavHeader(it)
                if(it.classStatus == 3){
                    try {
                        navController.navigate(R.id.action_global_scheduleCourseFragment)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                    return@Observer
                }
                when (it.onboardingStatus.toString()) {
                    StudentConst.pending,
                    StudentConst.course_not_opted,
                    StudentConst.schedule_not_opted -> {
                        try {
                            navController.navigate(R.id.action_global_scheduleCourseFragment)
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    }
                    StudentConst.completed -> {
                        try {
                            getMissedClasses()
                            if (navController.currentDestination?.id !== R.id.nav_home && !isDeepLink) {
                                navController.navigate(R.id.action_global_nav_home)
                            }else{
                                if(isDeepLink)isDeepLink = false
                            }
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    }

                }

            })

            viewModel.mErrorObservable?.observe(this, Observer {
                when (it?.code) {
                    401, 403 -> {
                        logoutuser()
                    }
                }
            })
            viewModel.ping().observe(this, Observer {
                //do nothing
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
            when (item.itemId) {
                R.id.nav_logout -> {
                    var map = HashMap<String, Any>().apply {
                        Utils.getDeviceInfo(this@StudentHomeActivity, CommonConst.PersonaType.Student.toString())?.let {
                            put("deviceInfo", it)
                        }
                    }
                    viewModel.logout(map).observe(this, Observer { response ->
                        logoutuser()
                    })
                }
                R.id.nav_school -> {
                    try{
                    navController.navigate(HomeFragmentDirections.actionNavHomeToSchoolListFragment())
                    }catch (e : Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
                R.id.nav_learn_tv -> {

                    val navigate = StudentListNavigationDirections.commonNavigation(
                        BuildConfig.LEARN_THROUGH_TV, getString(R.string.learn_throuht_tv)
                    )
                    try {
                        navController.navigate(navigate)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }

                R.id.nav_faq -> {
                    var url = "${BuildConfig.FAQ}Student&lan=${Lingver.getInstance().getLanguage()}"
                    val navigate = MobileNavigationDirections.commonNavigation(
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
                R.id.nav_terms_of_use -> {
                    val navigate = MobileNavigationDirections.commonNavigation(
                        BuildConfig.TERMS_AND_CONDITIONS_URL, getString(
                            R.string.menu_terms_of_use
                        )
                    )
                    try{
                    navController.navigate(navigate)
                    }catch (e : Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
                R.id.nav_privacy_policy -> {
                    val navigate = MobileNavigationDirections.commonNavigation(
                        BuildConfig.PRIVACY_POLICY_URL, getString(
                            R.string.menu_privacy_policy
                        )
                    )
                    try{
                    navController.navigate(navigate)
                    }catch (e : Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
                R.id.nav_doubts -> {
                    try{
                    navController.navigate(HomeFragmentDirections.actionNavHomeToDoubtListFragment())
                    }catch (e : Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
                R.id.nav_missed_classes -> {
                    navigateMissedClasses()
                }
                R.id.nav_download -> {
                    val navigate = MobileNavigationDirections.actionGlobalDownloadFragment2(
                        true
                    )
                    try{
                        navController.navigate(navigate)
                    }catch (e : Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)

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

                R.id.nav_bookmark_classes -> {
                    navController.navigate(R.id.bookmarkFragment)
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

    private fun setUpNavHeader(student: Student) {
        try {
            val headerView = navView.getHeaderView(0)
            val userName = headerView.findViewById<Spinner>(R.id.tv_toolbar_name)
            userName?.setSelection(studentList.indexOf(studentList.find { student.id == it.id }))
            val grade = headerView.findViewById<TextView>(R.id.tv_toolbar_grade)
            grade?.setText(
                "".plus(student.grade.toInt()).plus(
                    Utils.ordinalSuffix(
                        student.grade.toInt()
                    )
                ).plus(" ").plus(resources.getString(R.string.grade))
            )
            //Todo use logoID to show digitalSchool logo from Base url once API is live
            val profilePic = headerView.findViewById<ImageView>(R.id.iv_toolbar_profile)
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(46))
            profilePic?.loadUrlWithGlide(
                student.profileUrl,
                R.drawable.ic_student_placeholder,
                requestOptions = requestOptions
            )
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun getMissedClasses(){
        viewModel.getMissedClasses().observe(this, Observer {resource ->
            when(resource){
                is Resource.Success -> {
                    if(resource.data?.count?:0 <= 0)
                        return@Observer
                    resource.data?.firstMissedDate?.let{
                        try {
                            missedDate =  SimpleDateFormat("yyyy-MM-dd").parse(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    with(binding.appbarLayout.contentLayout){
                        llMissedClasses?.visibility = View.VISIBLE
                        imClose?.setOnClickListener {
                            llMissedClasses?.visibility = View.GONE
                        }
                        btAttend?.setOnClickListener {
                            llMissedClasses?.visibility = View.GONE
                            resource.data?.firstMissedDate?.let{
                                navigateMissedClasses()
                            }
                        }
                    }

                }
                is Resource.GenericError -> {

                }
            }
        })
    }

    private fun navigateMissedClasses(){
        try {
            val startDate = Utils.getLocalDateInSeconds(Calendar.getInstance().apply {
                time = missedDate
                set(Calendar.DAY_OF_MONTH,
                    this.getActualMinimum(Calendar.DAY_OF_MONTH))
            })
            val endDate = Utils.getLocalDateInSeconds(Calendar.getInstance().apply {
                time = missedDate
                set(Calendar.DAY_OF_MONTH,
                    this.getActualMaximum(Calendar.DAY_OF_MONTH))
            })
            navController.navigate(HomeFragmentDirections.actionNavHomeToCourseTimetableFragment(startDate,endDate,true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    fun setToolbarTitle(title: String) {
        try {
            binding.appbarLayout.toolbarTitle.text = title
        } catch (e: Exception) {
        }
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
                startActivityFromString(CommonConst.SPLASH_ACTIVITY_PATH)
            })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    fun showNetworkErrorDialog(isFullScreen: Boolean = true, click: () -> Unit) {
        try {
            if (this::dialogFragment.isInitialized) {
                dialogFragment.dismiss()
            }
        } catch (e: Exception) {

        }
        try {
            dialogFragment = SuccessDialogFragment.Builder(this)
                .setIsDialogCancelable(true)
                .setTitle(getString(R.string.no_internet_connection))
                .setDescription(getString(R.string.you_do_not_have_an_active_internet_connection_please_check_your_internet_connection_and_retry))
                .setIcon(R.drawable.ic_network_issue)
                .setViewType(if (isFullScreen) SuccessDialogFragment.DIALOG_TYPE.FULL_SCREEN else SuccessDialogFragment.DIALOG_TYPE.ALERT)
                .setButtonPositiveText(getString(R.string.retry))
                .setOnClickListner(View.OnClickListener {
                    if (Utils.isNetworkAvailable(this)) {
                        dialogFragment.dismiss()
                        click.invoke()
                    }
                })
                .build()
            dialogFragment.show(supportFragmentManager, "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initLanguageDrawable(){
        try {
            val tvLanguage: TextView = MenuItemCompat.getActionView(navView.menu.findItem(R.id.nav_locale)) as TextView
            tvLanguage?.let {
                it.gravity = Gravity.CENTER_VERTICAL
                it.setTypeface(null, Typeface.BOLD)
                it.setTextColor(getResources().getColor(R.color.student_orange))

                it. text = when (Lingver.getInstance().getLanguage()) {
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
                    else ->  resources.getString(R.string.english)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


}